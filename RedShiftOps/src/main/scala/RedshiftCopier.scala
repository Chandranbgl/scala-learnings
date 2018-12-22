import java.sql.Connection


trait RedshiftCopier {
  def copy(sql: String)
  val redshiftConfig : RedshiftConfig
}

class RealRedshiftCopier(redshiftClient: RedshiftClient) extends RedshiftCopier {


  val redshiftConfig : RedshiftConfig = redshiftClient.redshiftConfig


  override def copy(sql: String): Unit = {
    redshiftClient.borrowSqlConnection((connection: Connection) => {
      val stmt = connection.createStatement()
      try {
        stmt.execute(sql)
      } finally {
        Option(stmt).foreach(_.close())
      }
    })
  }
}

object RedshiftCopier {
  def apply(redshiftConfig : RedshiftConfig): RealRedshiftCopier = {
    new RealRedshiftCopier( new RealRedshiftClient(redshiftConfig))
  }
}
