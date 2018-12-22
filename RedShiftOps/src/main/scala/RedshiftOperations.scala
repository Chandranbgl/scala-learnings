

/**
  * Created by styagi on 12/06/2017.
  */
trait RedshiftOperations {
  def getPartnerConfig(partnerConfiguration: PartnerConfiguration): Map[String, PartnerConfig]
  def loadToRedshift(s3Target: S3Target, credentialsConfig: CredentialsConfig) : Unit
}
