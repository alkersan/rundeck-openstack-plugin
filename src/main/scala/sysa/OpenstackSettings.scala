package sysa

import java.util.Properties

case class OpenstackSettings(id: String, tenant: String, username: String, password: String, endpoint: String, refreshInterval: Long)

object OpenstackSettings {
	def apply(props: Properties): OpenstackSettings = new OpenstackSettings(
		s"${props.getProperty("project")}-${props.getProperty("id")}",
		props.getProperty("tenant"),
		props.getProperty("username"),
		props.getProperty("password"),
		props.getProperty("endpoint"),
		props.getProperty("refresh-interval").toLong
	)
}
