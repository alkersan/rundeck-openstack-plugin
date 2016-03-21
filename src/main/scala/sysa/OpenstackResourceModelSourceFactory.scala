package sysa

import java.util.Properties

import com.dtolabs.rundeck.core.common.Framework
import com.dtolabs.rundeck.core.plugins.Plugin
import com.dtolabs.rundeck.core.plugins.configuration.{StringRenderingConstants, Describable}
import com.dtolabs.rundeck.core.resources.{ResourceModelSourceFactory, ResourceModelSource}
import com.dtolabs.rundeck.plugins.ServiceNameConstants
import com.dtolabs.rundeck.plugins.util.{PropertyBuilder, DescriptionBuilder}


@Plugin(name = "openstack", service = ServiceNameConstants.ResourceModelSource)
class OpenstackResourceModelSourceFactory(private val framework: Framework) extends ResourceModelSourceFactory with Describable {

	override def createResourceModelSource(props: Properties): ResourceModelSource =
		new OpenstackNodeSource(framework, OpenstackSettings(props))

	override def getDescription = description

	private val description = DescriptionBuilder.builder
		.name("openstack")
		.title("OpenStack")
		.description("Obtains node definitions from OpenStack Compute service (aka Nova)")
		.property(
			PropertyBuilder.builder()
				.string("id")
				.title("Id")
				.description("Uniquely identify this OpenStack source. Nodes retrieved from this source will be marked with `os_source_id` attribute")
				.required(true))
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
				.renderingOption(StringRenderingConstants.DISPLAY_TYPE_KEY, StringRenderingConstants.DisplayType.PASSWORD)
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
				.string("tags-separator")
				.title("Tags separator")
				.description("A character used to split instance metadata attribute named `tags` (if present) into Rundeck tags")
				.defaultValue(",")
				.required(true))
		.property(
			PropertyBuilder.builder()
				.booleanType("include-server-node")
				.title("Include server node")
				.description("Automatically include this Rundeck server node to resulting node-set?")
				.required(true)
			  .defaultValue("false"))
		.build
}
