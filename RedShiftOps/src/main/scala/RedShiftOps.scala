
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider

import scala.annotation.tailrec
import java.sql.{Connection, ResultSet}
import com.typesafe.config.Config


case class PartnerConfiguration(
                                 partnerCodeForDefault : Long,
                                 partnerProductForDefault : String,
                                 partnerConfigTableNamePrefix : String,
                                 redshiftConfig : RedshiftConfig)

object PartnerConfiguration{
  def apply( config : Config): PartnerConfiguration = {
    PartnerConfiguration(
      partnerCodeForDefault =  config.getLong("partnerConfig.partnerCodeForDefault"),
      partnerProductForDefault =  config.getString("partnerConfig.partnerProductForDefault"),
      partnerConfigTableNamePrefix = config.getString("partnerConfig.partnerConfigTableNamePrefix"),
      redshiftConfig = RedshiftConfig(config.getConfig("partnerConfig.redshift"))
    )
  }
}

case class BrandNames(legacySaleBrand : Option[String], brandName : Option[String]) {

}

case class PartnerConfig(partnerID: String,
                         partnercode: Long,
                         partnername: String,
                         product: String,
                         legacyfilenameprefix: String,
                         legacyproductname: String,
                         frequency: String,
                         version : String,
                         partnercontact: List[String],
                         ctmcontact: List[String],
                         to_s3: String,
                         to_ftp: String,
                         validBrands: Map[String, BrandNames], // map of brands keyed by partner_brand_mapping.product
                         lateFileNotification: Boolean = false
                        )
class RedShiftOps(reader: RedshiftReader, redshiftLoaders : Seq[RedshiftCopier])  {
    override def getPartnerConfig( partnerConfiguration : PartnerConfiguration): Map[String, PartnerConfig] = {
      val schema = partnerConfiguration.redshiftConfig.schema
      val tableNamePrefix = partnerConfiguration.partnerConfigTableNamePrefix
      val partnerConfigSql =
        s"""
        select distinct l.partnercode,p.partnername,l.product,l.legacyfilenameprefix,p.legacyproductname,l.version,l.frequency,p.partnercontact,p.ctmcontact,l.ftpinboundlocation,
        l.ftpoutboundlocation,pbm.brandid,b.legacysalebrand, b.brandname, l.enablelatenotification
        from
            $schema.${tableNamePrefix}partner_locations l
        left join $schema.${tableNamePrefix}partner p on l.partnercode = p.partnercode and l.product = p.product
        left join $schema.${tableNamePrefix}partner_brand_mapping pbm on l.partnercode = pbm.partnercode
        left join $schema.${tableNamePrefix}brand b on b.brandid = pbm.brandid
        where
          l.partnercode is not null and
          l.product is not null and
          l.legacyfilenameprefix is not null and
          p.ctmcontact is not null
        """
      reader.read[Map[String, PartnerConfig]](partnerConfigSql, extractPartnerConfig(Map[String, PartnerConfig]()))
    }

    @tailrec
    private def extractPartnerConfig(configs: Map[String, PartnerConfig])(results: RedshiftResult): Map[String, PartnerConfig] = {
      if (results.next()) {

        val partnerConfig = configs.getOrElse("",
          PartnerConfig(
            partnerID = "",
            partnercode = results.getLong("partnercode"),
            partnername = results.getString("partnername"),
            product = results.getString("product"),
            legacyfilenameprefix = results.getString("legacyfilenameprefix"),
            legacyproductname = results.getString("legacyproductname"),
            frequency = results.getString("frequency"),
            version = results.getString("version"),
            partnercontact = Option(results.getString("partnercontact")).map(_.split('|').toList).getOrElse(Nil),
            ctmcontact = Option(results.getString("ctmcontact")).map(_.split('|').toList).getOrElse(Nil),
            to_s3 = results.getString("ftpinboundlocation"),
            to_ftp = results.getString("ftpoutboundlocation"),
            validBrands = Map(results.getString("brandid") -> BrandNames(Option(results.getString("legacysalebrand")), Option(results.getString("brandname")))),
            lateFileNotification = results.getBoolean("enablelatenotification")
          )
        )

        val partnerConfigWithBrands = partnerConfig.copy(validBrands = partnerConfig.validBrands
          + (results.getString("brandid") -> BrandNames(Option(results.getString("legacysalebrand")), Option(results.getString("brandname")))))
        val updatedConfigs = configs.updated("", partnerConfigWithBrands)
        extractPartnerConfig(updatedConfigs)(results)
      } else {
        configs
      }
    }

    override def loadToRedshift(s3Target: S3Target, credentialsConfig: CredentialsConfig): Unit = {

      redshiftLoaders.foreach { redshiftLoader =>
        val credentials = getCredentials(redshiftLoader.redshiftConfig, credentialsConfig)

        redshiftLoader.copy(getCopyCommand("sales", s3Target.fullS3Path, credentials, redshiftLoader.redshiftConfig))

      }
    }

    private def getCredentials(redshiftConfig : RedshiftConfig, credentialsConfig: CredentialsConfig) : String = {
      (credentialsConfig.roleArn, credentialsConfig.sessionName) match {
        case (Some(roleArn), Some(sessionName)) =>
          val (tmpAccessKey, tmpAccessSecret, tmpAccessToken) = {
            val cred = new STSAssumeRoleSessionCredentialsProvider.Builder(roleArn, sessionName).build().getCredentials
            (cred.getAWSAccessKeyId, cred.getAWSSecretKey, cred.getSessionToken)
          }
          s"access_key_id '$tmpAccessKey' secret_access_key '$tmpAccessSecret' session_token '$tmpAccessToken'"
        case _ =>
          s"iam_role '${redshiftConfig.awsIamRole}'"
      }
    }

    private def getCopyCommand(tableName: String, s3Path: String, credentials : String, redshiftConfig: RedshiftConfig): String = {
      val additionalCommands = "json 'auto' EMPTYASNULL BLANKSASNULL ACCEPTANYDATE ACCEPTINVCHARS TRUNCATECOLUMNS gzip"

      s"copy ${redshiftConfig.schema}.$tableName from '$s3Path' $credentials $additionalCommands;"
    }
  }


}
