package sysa

import java.util.Properties

case class OpenstackSettings(id: String, tenant: String, username: String, password: String, endpoint: String, refreshInterval: Long, tagsSeparator: String)

object OpenstackSettings {
	def apply(props: Properties): OpenstackSettings = new OpenstackSettings(
		id = s"${props.getProperty("project")}-${props.getProperty("id")}",
		tenant = props.getProperty("tenant"),
		username = props.getProperty("username"),
		password = props.getProperty("password"),
		endpoint = props.getProperty("endpoint"),
		refreshInterval = props.getProperty("refresh-interval").toLong,
		tagsSeparator = props.getProperty("tags-separator", ",")
	)
}
