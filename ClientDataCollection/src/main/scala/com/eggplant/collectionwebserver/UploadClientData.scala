package com.eggplant.collectionwebserver

import akka.Done
import scala.concurrent.Future

object UploadClientData {
  implicit val executionContext = CollectionWebServer.system.dispatcher
  def storeHostData(client: SqlClient, userHostDetail: UserHostDetail): Future[Done] = {
    try {
      val sql =
        s"""INSERT INTO UserHostDetails VALUES(
           '${userHostDetail.customerID}',
           |'${userHostDetail.hostname}',
           |'${userHostDetail.path}',
           |'${userHostDetail.ipAddress}',
           |'${userHostDetail.eventStartTime}',
           |'${userHostDetail.eventFinishtime}',
           |'${userHostDetail.userAgent}')""".stripMargin
      println(userHostDetail)
      client.connection.foreach{ c =>
        c.prepareStatement(sql).executeUpdate()
      }
    Future(Done)
    } catch {
      case e: Exception =>
        throw e
    }
  }
}
