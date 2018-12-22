package com.eggplant.collectionwebserver

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.typesafe.config.ConfigFactory
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn
import scala.concurrent.Future

object CollectionWebServer extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  implicit val itemFormat = jsonFormat7(UserHostDetail)

  val config = ConfigFactory.load()
  val sqlClient = new SqlClient(SqlConfig(config))
  lazy val routes =
      get {
        pathPrefix("beacon") {
          complete(StatusCodes.NoContent)
        }
      } ~
        post {
            entity(as[UserHostDetail]) { userHostDetail =>
              val saved: Future[Done] = UploadClientData.storeHostData(sqlClient, userHostDetail)
              complete(StatusCodes.OK)
            }
        }


  val bind = Http().bindAndHandle(routes, "localhost", 8080)
  println("Server is Running...")
  StdIn.readLine()
  bind
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
