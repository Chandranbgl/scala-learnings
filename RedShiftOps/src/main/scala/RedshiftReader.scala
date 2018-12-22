import java.sql.{Connection, ResultSet}


trait RedshiftReader {
  def read[T](sql: String, transform: (RedshiftResult) => T): T
}

class RealRedshiftReader(redshiftClient: RedshiftClient) extends RedshiftReader {

  override def read[T](sql: String, transform: (RedshiftResult) => T): T = {
    redshiftClient.borrowSqlConnection[T]((connection: Connection) => {
      val stmt = connection.prepareStatement(sql)
      var rst: ResultSet = null
      try {
        rst = stmt.executeQuery()
        transform(rst)
      } finally {
        Option(rst).foreach(_.close())
        Option(stmt).foreach(_.close())
      }

    })
  }


}

object RedshiftReader {
  def apply(partnerConfiguration: PartnerConfiguration): RealRedshiftReader = {
    new RealRedshiftReader(
      new RealRedshiftClient(partnerConfiguration.redshiftConfig))
  }
}
