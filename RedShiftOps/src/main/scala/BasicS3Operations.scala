import java.io._
import java.nio.charset.MalformedInputException
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import com.amazonaws.services.s3.model._
import com.amazonaws.{AmazonServiceException, SdkClientException}

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.io.Source

case class S3File(key: String, size: Long, lastModified: Long)

class BasicS3Operations(clientManager: S3ClientManager) {


  def getS3CSVFileList(bucket: String, key: String): Seq[String] = {
    lists3Files(bucket, key)
      .filter(_.key.endsWith(".csv"))
      .map(_.key)
  }

  private def lists3Files(bucket: String, key: String) = {

    val listObjectsRequest: ListObjectsRequest = new ListObjectsRequest()
      .withBucketName(bucket)
      .withPrefix(key)

    @tailrec
    def loop(objectListing: ObjectListing, s3File: Seq[S3File]): Seq[S3File] = {
      val files = objectListing.getObjectSummaries.iterator().asScala
        .filter(_.getSize > 0)
        .map(summary => S3File(summary.getKey, summary.getSize, summary.getLastModified.getTime))
      listObjectsRequest.setMarker(objectListing.getNextMarker)
      if (objectListing.isTruncated) {
        loop(clientManager.client.listObjects(listObjectsRequest), s3File ++ files)
      } else {
        s3File ++ files
      }
    }

    loop(clientManager.client.listObjects(listObjectsRequest), Seq())
  }

  def getS3FileList(bucket: String, key: String): Seq[String] = {
    lists3Files(bucket, key)
      .map(_.key)
  }

  def uploadStringToS3(bucket: String, key: String, data: String): Unit = {
    import java.io.{BufferedInputStream, ByteArrayInputStream}

    import com.amazonaws.services.s3.model.{ObjectMetadata, PutObjectRequest}

    var inputStream: BufferedInputStream = null
    try {
      val bytes = data.getBytes
      inputStream = new BufferedInputStream(new ByteArrayInputStream(bytes))
      val objectMeta = new ObjectMetadata()
      objectMeta.setContentLength(bytes.length)
      val request = new PutObjectRequest(bucket, key, inputStream, objectMeta)
      clientManager.client.putObject(request)
    } finally {
      Option(inputStream).foreach(_.close())
    }
  }

  def uploadGZipStringToS3(bucket: String, key: String, data: String): Unit = {
    import java.io.ByteArrayInputStream

    import com.amazonaws.services.s3.model.{ObjectMetadata, PutObjectRequest}

    var inputStream: BufferedInputStream = null
    try {
      val out = new ByteArrayOutputStream(data.length)
      val writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(out), "UTF-8"))
      writer.write(data)
      writer.close()
      val gz = out.toByteArray
      inputStream = new BufferedInputStream(new ByteArrayInputStream(gz))

      val objectMeta = new ObjectMetadata()
      objectMeta.setContentLength(gz.length)
      val request = new PutObjectRequest(bucket, key, inputStream, objectMeta)

      clientManager.client.putObject(request)

    } catch {
      case th: Throwable => {
        delete(key, bucket)
        throw th
      }
    } finally {
      Option(inputStream).foreach(_.close())
    }
  }

  /*
  TODO: issue was identified in MockS3 library during testing
  i.e. if path contains special characters like '=' it will be encoded and MockS3 will fail to locate local file in /tmp.. folder
  * */
  def copyS3ToS3(sourceBucket: String, sourceKey: String, destBucket: String, destKey: String) : Unit = {
    try{

      val copyObjRequest = new CopyObjectRequest(sourceBucket, sourceKey, destBucket, destKey);
      clientManager.client.copyObject(copyObjRequest);
      //clientManager.client.copyObject(sourceBucket, sourceKey, destBucket, destKey)
    } catch {
      case ae: AmazonServiceException => {
        ae.printStackTrace();
        throw ae;
      }
      case se: SdkClientException => {
        se.printStackTrace();
        throw se;
      }
    }
  }


  def delete(bucket: String, key: String): Unit = {
    import scala.util.{Failure, Success, Try}
    Try(clientManager.client.deleteObject(bucket, key)) match {
      case Success(e) => println("S3 file deleted : " + key)
      case Failure(e) => {
        println("Failed while deleting s3 file : " + key)
        throw e
      }
    }
  }

  def getData(bucket: String, path: String, enc: String = ""): List[String] = {
    var inputStream: Option[InputStream] = None
    var s3Object: Option[S3Object] = None
    try {
      s3Object = Option(clientManager.client.getObject(new GetObjectRequest(bucket, path)))
      inputStream = s3Object.map(_.getObjectContent)
      inputStream.map {
        if (enc.isEmpty) Source.fromInputStream(_).getLines.toList
        else Source.fromInputStream(_, enc).getLines.toList
      }.getOrElse(List.empty[String])
    } catch {
      case _: MalformedInputException if enc.isEmpty => getData(bucket, path, "iso-8859-1")
      case _: AmazonS3Exception => List.empty[String]
      case e: Throwable => throw e
    } finally {
      s3Object.foreach(_.close())
      inputStream.foreach(_.close())
    }
  }

  def recursiveDelete(bucket: String, key: String): Unit = {
    lists3Files(key, bucket).foreach(file => delete(file.key, bucket))
  }

  def listFolders(bucket: String, key: String): List[String] = {
    clientManager.client.listObjects(new ListObjectsRequest()
      .withBucketName(bucket)
      .withDelimiter("/")
      .withPrefix(key))
      .getCommonPrefixes.asScala.toList
  }

  def moveFile(srcBucket: String, srcKey: String, destBucket: String, destKey: String): Unit = {
    clientManager.client.copyObject(srcBucket, srcKey, destBucket, destKey)
    clientManager.client.deleteObject(srcBucket, srcKey)
  }

  def getGZipData(bucket: String, path: String): List[String] = {
    var inputStream: Option[InputStream] = None
    var s3Object: Option[S3Object] = None
    try {
      s3Object = Option(clientManager.client.getObject(new GetObjectRequest(bucket, path)))
      inputStream = s3Object.map(_.getObjectContent)
      inputStream.map { stream =>
        IOUtils.toString(new GZIPInputStream(new ByteArrayInputStream(IOUtils.toByteArray(stream))), "UTF-8").split('\n').toList
      }.getOrElse(List.empty[String])
    } catch {
      case _: AmazonS3Exception => List.empty[String]
      case e: Throwable => throw e
    } finally {
      s3Object.foreach(_.close())
      inputStream.foreach(_.close())
    }
  }

  object BasicS3Operations {
    def apply(credentialsConfig: CredentialsConfig): BasicS3Operations = {
      new BasicS3Operations(new BasicS3ClientManager(credentialsConfig))
    }
  }

}