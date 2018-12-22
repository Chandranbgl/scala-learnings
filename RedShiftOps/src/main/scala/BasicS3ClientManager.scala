import com.amazonaws.ClientConfiguration
import com.amazonaws.auth._
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}

class BasicS3ClientManager(credentialsConfig: CredentialsConfig) extends S3ClientManager {
  override val client: AmazonS3 = {
    val config = new ClientConfiguration()
    config.setMaxErrorRetry(5)

    val s3Client = AmazonS3ClientBuilder
      .standard
      .withRegion(Regions.EU_WEST_1)
      .withClientConfiguration(config)

    (credentialsConfig.roleArn,  credentialsConfig.sessionName) match {
      case (Some(roleARN), Some(sessionName)) =>
        s3Client.withCredentials(
          new STSAssumeRoleSessionCredentialsProvider.Builder(roleARN, sessionName).build()).build()
      case _ =>
        s3Client.withCredentials(new InstanceProfileCredentialsProvider(true)).build()
    }
  }
}
