import com.amazonaws.services.s3.AmazonS3

trait S3ClientManager extends Serializable {
  val client: AmazonS3
}
