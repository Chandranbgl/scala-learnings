package com.eggplant.collectionwebserver

import java.sql.{Connection, DriverManager, SQLException}
import java.util.Properties

object SqlClient {
  def apply(config: SqlConfig): SqlClient = new SqlClient(config)
}

class SqlClient(config: SqlConfig) {

  val connection: Option[Connection] = {
    Class.forName(config.driver)
    try {
      val prop = new Properties()
      prop.setProperty("user", config.username)
      prop.setProperty("password", config.password)
      Some(DriverManager.getConnection(config.jdbcUrl, config.username, config.password))
    } catch {
      case e: Exception =>
        println(e.getMessage)
        connection.foreach(_.rollback())
        throw e
    }
  }

  def close = connection.foreach(_.close())

}
