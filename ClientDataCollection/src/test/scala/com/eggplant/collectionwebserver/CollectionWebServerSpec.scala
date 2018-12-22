package com.eggplant.collectionwebserver

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.eggplant.collectionwebserver.CollectionWebServer.routes
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport


class UserRoutCollectionWebServerSpecesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest with SprayJsonSupport{

  "CollectionWebServer" should {
    "return no content if we call with (GET /beacon)" in {
      Get("/beacon") ~> routes ~> check {
        status should === (StatusCodes.NoContent)
      }
    }
  }
}
