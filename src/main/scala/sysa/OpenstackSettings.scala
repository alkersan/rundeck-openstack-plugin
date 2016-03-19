package sysa

import java.util.Properties

case class OpenstackSettings(sourceId: String, tenant: String, username: String, password: String, endpoint: String, tagsSeparator: String)

object OpenstackSettings {
	def apply(props: Properties): OpenstackSettings = new OpenstackSettings(
		sourceId = props.getProperty("id"),
		tenant = props.getProperty("tenant"),
		username = props.getProperty("username"),
		password = props.getProperty("password"),
		endpoint = props.getProperty("endpoint"),
		tagsSeparator = props.getProperty("tags-separator", ",")
	)
}
