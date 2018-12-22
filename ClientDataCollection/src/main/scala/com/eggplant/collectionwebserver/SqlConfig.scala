package com.eggplant.collectionwebserver

import com.typesafe.config.Config

case class SqlConfig(jdbcUrl: String, driver: String, username: String, password: String)

object SqlConfig {
  def apply(config : Config): SqlConfig = {
    new SqlConfig(
      config.getString("mysql.jdbcUrl"),
      config.getString("mysql.driver"),
      config.getString("mysql.username"),
      config.getString("mysql.password")
    )
  }
}
