import scala.util.Try

case class CredentialsConfig(roleArn : Option[String], sessionName : Option[String])


object CredentialsConfig {

  def apply(config : Config): CredentialsConfig = {
    new CredentialsConfig(
      Try(config.getString("awsCredentials.roleArn")).toOption.find(_.nonEmpty),
      Try(config.getString("awsCredentials.sessionName")).toOption.find(_.nonEmpty)
    )
  }
}