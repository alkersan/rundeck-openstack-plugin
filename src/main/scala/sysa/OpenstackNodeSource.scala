package sysa

import java.util.Properties

import com.dtolabs.rundeck.core.common.{INodeSet, NodeEntryImpl, NodeSetImpl}
import com.dtolabs.rundeck.core.resources.ResourceModelSource
import org.apache.log4j.Logger
import org.jclouds.ContextBuilder
import org.jclouds.openstack.nova.v2_0.domain.Address
import org.jclouds.openstack.nova.v2_0.{NovaApi, NovaApiMetadata}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._


class OpenstackNodeSource(private val settings: OpenstackSettings) extends ResourceModelSource {

	val log = Logger.getLogger(s"${getClass.getName}-${settings.sourceId}")

	override def getNodes: INodeSet = {
		log.debug(s"Fetching server list")

		val nodeSet = new NodeSetImpl()

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
							val node = new NodeEntryImpl()
							node.setDescription(s"OpenStack node from '${settings.sourceId}' source")
							node.setNodename(server.getName)
							node.setHostname(addr.getAddr)
							node.setAttribute("os_source_id", settings.sourceId)
							node.setAttribute("os_instance_id", server.getId)
							node.setAttribute("os_region", region)
							node.setAttribute("os_status", server.getStatus.toString)
							server.getMetadata.filterKeys(_ != "tags").foreach { case (k, v) => node.setAttribute(k, v) }
							val tags = server.getMetadata.getOrElse("tags", "")
								.split(settings.tagsSeparator)
								.map(_.trim)
								.filterNot(_.isEmpty)
								.toSet
							if (tags.nonEmpty) node.setTags(tags.asJava)
							nodeSet.putNode(node)

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

		log.debug(s"Fetched #${nodeSet.size} entries")

		nodeSet
	}
}
