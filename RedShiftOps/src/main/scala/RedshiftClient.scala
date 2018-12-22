import java.sql.{Connection, DriverManager}
import java.util.Properties

trait RedshiftClient {
  def borrowSqlConnection[T](borrower: (Connection) => T): T

  val redshiftConfig : RedshiftConfig

}

class RealRedshiftClient(config: RedshiftConfig) extends RedshiftClient {


  val redshiftConfig : RedshiftConfig = config

  override def borrowSqlConnection[T](borrower: (Connection) => T): T = {
    var con: Connection = null
    Class.forName("com.amazon.redshift.jdbc41.Driver")
    try {
      val prop = new Properties()
      prop.setProperty("user", config.user)
      prop.setProperty("password", config.password)
      con = DriverManager.getConnection(config.JDBCUrl, prop)
      borrower(con)
    } catch {
      case e: Exception =>
        Option(con).foreach(_.rollback())
        throw e
    }
    finally {
      Option(con).foreach(_.close())
    }
  }
}
