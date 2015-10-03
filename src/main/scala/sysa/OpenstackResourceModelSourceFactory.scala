package sysa

import java.util.Properties

import com.dtolabs.rundeck.core.common.Framework
import com.dtolabs.rundeck.core.plugins.Plugin
import com.dtolabs.rundeck.core.plugins.configuration.{Description, Describable}
import com.dtolabs.rundeck.core.resources.{ResourceModelSourceFactory, ResourceModelSource}
import com.dtolabs.rundeck.plugins.ServiceNameConstants
import com.dtolabs.rundeck.plugins.util.{PropertyBuilder, DescriptionBuilder}
import org.apache.log4j.Logger


@Plugin(name = "openstack", service = ServiceNameConstants.ResourceModelSource)
class OpenstackResourceModelSourceFactory(private val framework: Framework) extends ResourceModelSourceFactory with Describable {

	val log = Logger.getLogger(getClass.getName)

	def this() = this(null)

	override def createResourceModelSource(props: Properties): ResourceModelSource = {
		new OpenstackResourceModelSource(OpenstackSettings(props))
	}

	override def getDescription: Description = DescriptionBuilder.builder
		.name("openstack")
		.title("OpenStack")
		.description("Obtains node definitions from OpenStack Compute service (aka Nova)")
		.property(
			PropertyBuilder.builder()
				.string("tenant")
				.title("Tenant")
				.description("OpenStack tenant (aka project)")
				.required(true))
		.property(
			PropertyBuilder.builder()
				.string("username")
				.title("Username")
				.description("User, authorized to query OpenStack Compute service")
				.required(true))
		.property(
			PropertyBuilder.builder()
				.string("password")
				.title("Password")
				.renderingAsPassword()
				.description("Password (or API Key) to query OpenStack")
				.required(true))
		.property(
			PropertyBuilder.builder()
				.string("endpoint")
				.title("Endpoint")
				.description("URL of OpenStack Identity service (aka Keystone)")
				.required(true))
		.property(
			PropertyBuilder.builder()
				.longType("refresh-interval")
				.title("Refresh interval")
				.description("Interval (in seconds) between refreshing nodes state")
				.defaultValue("30")
				.required(true))
		.build
}
