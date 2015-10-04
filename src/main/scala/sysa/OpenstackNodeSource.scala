package sysa

import java.util.Properties
import java.util.concurrent.TimeUnit._
import java.util.concurrent.atomic.AtomicReference

import com.dtolabs.rundeck.core.common.{NodeEntryImpl, NodeSetImpl, INodeSet}
import com.dtolabs.rundeck.core.resources.ResourceModelSource
import com.google.common.util.concurrent.AbstractScheduledService
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler
import org.apache.log4j.Logger
import org.jclouds.ContextBuilder
import org.jclouds.openstack.nova.v2_0.domain.Address
import org.jclouds.openstack.nova.v2_0.{NovaApiMetadata, NovaApi}

import scala.collection.JavaConversions._


class OpenstackNodeSource(val settings: OpenstackSettings) extends AbstractScheduledService with ResourceModelSource {

	val log = Logger.getLogger(s"${getClass.getName}-${settings.id}")

	val nodeSet = new AtomicReference[INodeSet](new NodeSetImpl())

	override def serviceName(): String = s"${getClass.getSimpleName}-${settings.id}"

	override def scheduler() = Scheduler.newFixedRateSchedule(0, settings.refreshInterval, SECONDS)

	override def startUp(): Unit = log.debug(s"Starting with refreshInterval: ${settings.refreshInterval}s")

	override def shutDown(): Unit = log.debug(s"Stopping")

	override def getNodes: INodeSet = nodeSet.get()

	override def runOneIteration(): Unit = {
		log.debug(s"Fetching server list")
		val newNodeSet = new NodeSetImpl()

		val jcloudsProps = new Properties()
		jcloudsProps.setProperty(org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT, "5000")
		jcloudsProps.setProperty(org.jclouds.Constants.PROPERTY_REQUEST_TIMEOUT, "5000")

		var novaApi: NovaApi = null

		try {
			log.debug("Creating Nova API client")
			novaApi = ContextBuilder.newBuilder(new NovaApiMetadata())
				.endpoint(settings.endpoint)
				.credentials(s"${settings.tenant}:${settings.username}", settings.password)
				.overrides(jcloudsProps)
				.buildApi(classOf[NovaApi])

			novaApi.getConfiguredRegions.foreach { region =>
				log.debug(s"Walking over region #$region")

				log.debug(s"Creating support API clients")
				val serverApi = novaApi.getServerApi(region)
				val floatingApi = novaApi.getFloatingIPApi(region)

				val nodeIpFindStrategy = floatingApi.isPresent match {
					case true =>
						log.debug("Fetching floating IPs")
						val floatingIPs = floatingApi.get.list.toIndexedSeq.map(_.getIp)
						// Strategy: select first non-floating IP
						(a: Address) => !floatingIPs.contains(a.getAddr)
					case false =>
						// Strategy: select any first IP address, because floating IP extension not present
						(_: Address) => true
				}

				log.debug("Fetching servers")
				serverApi.listInDetail.concat.foreach { server =>
					server.getAddresses.values.find(nodeIpFindStrategy) match {
						case Some(addr) =>
							val node = new NodeEntryImpl(server.getName)
							node.setHostname(addr.getAddr)
							node.setAttribute("os_instance", server.getId)
							node.setAttribute("os_region", region)
							node.setAttribute("os_status", server.getStatus.toString)
							server.getMetadata.foreach { case (k, v) => node.setAttribute(k, v) }
							newNodeSet.putNode(node)

						case None =>
					}
				}
			}
		} catch {
			case ex: Throwable =>
				log.error("Error during fetching nodes", ex)
		} finally {
			if (novaApi != null) novaApi.close()
		}

		log.debug(s"Fetched #${newNodeSet.size} entries")
		nodeSet.set(newNodeSet)
	}

}

object OpenstackNodeSource {

	private var sources = Map.empty[String, OpenstackNodeSource]

	def apply(settings: OpenstackSettings): ResourceModelSource = this.synchronized {
		if (!sources.contains(settings.id) || sources(settings.id).settings != settings) {
			sources.get(settings.id).foreach(_.stopAsync())
			val newSource = new OpenstackNodeSource(settings)
			sources = sources.updated(settings.id, newSource)
			newSource.startAsync()
			newSource
		} else {
			sources(settings.id)
		}
	}

	def removeUnusedSources(currentSourceIDs: Set[String]): Unit = this.synchronized {
		val removedSources = sources.keySet.diff(currentSourceIDs)
		removedSources.foreach { id => sources(id).stopAsync() }
		sources = sources -- removedSources
	}
}
