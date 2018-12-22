

case class S3Target(bucket : String , keyPrefix : String) {
  def fullS3Path : String = s"s3://$bucket/$keyPrefix"
}

case class MasterConfig(
                         salesFileLandingZone : S3Target,
                         failedSaleFileDropZone : S3Target,
                         legacySalesFilesDropZone :S3Target,
                         monthlylegacySalesFilesDropZone :S3Target,
                         messageNotifierDropZone : S3Target,
                         successfulSaleFileDropZone : S3Target,
                         successfulModelsDropZone : S3Target,
                         numberOfWorkers : Int,
                         numberOfSQLWorkers : Int,
                         partnerCodeForDefault : Long,
                         partnerProductForDefault : String,
                         travelFileNamePrefix : String,
                         acceptedSchemaVersions : List[Int]
                       )


object MasterConfig {
  def apply(config : Config): MasterConfig = {
    new MasterConfig(

      salesFileLandingZone = S3Target(
        config.getString("salesFileLandingZone.bucket"),
        config.getString("salesFileLandingZone.keyPrefix")),

      failedSaleFileDropZone = S3Target(
        config.getString("failedSaleFileDropZone.bucket"),
        config.getString("failedSaleFileDropZone.keyPrefix")),

      legacySalesFilesDropZone = S3Target(
        config.getString("legacySalesFilesDropZone.bucket"),
        config.getString("legacySalesFilesDropZone.keyPrefix")),

      monthlylegacySalesFilesDropZone = S3Target(
        config.getString("monthlylegacySalesFilesDropZone.bucket"),
        config.getString("monthlylegacySalesFilesDropZone.keyPrefix")),

      messageNotifierDropZone = S3Target(
        config.getString("messageNotifierDropZone.bucket"),
        config.getString("messageNotifierDropZone.keyPrefix")),

      successfulSaleFileDropZone = S3Target(
        config.getString("successfulSaleFileDropZone.bucket"),
        config.getString("successfulSaleFileDropZone.keyPrefix")),

      successfulModelsDropZone = S3Target(
        config.getString("successfulModelsDropZone.bucket"),
        config.getString("successfulModelsDropZone.keyPrefix")),

      numberOfWorkers = config.getInt("actors.numberOfWorkers"),
      numberOfSQLWorkers = config.getInt("actors.numberOfSQLWorkers"),

      partnerCodeForDefault =  config.getLong("partnerConfig.partnerCodeForDefault"),
      partnerProductForDefault =  config.getString("partnerConfig.partnerProductForDefault"),

      travelFileNamePrefix =  config.getString("travel.travelFileNamePrefix"),
      acceptedSchemaVersions = config.getIntList("partnerConfig.acceptedSchemaVersions").asScala.map(_.toInt).toList)
  }
}
