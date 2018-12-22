package com.facebook.coolplay

//#quick-start-server
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.facebook.coolplay.CoolPlayOperationsActor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.util.{Failure, Success}

//#main-class
object CoolPlayApp {

  def main(args: Array[String]): Unit = {
    //implicit val system = ActorSystem("coolPlayApp")
   // implicit val materializer = ActorMaterializer()
    //implicit val executionContext = system.dispatcher
    val username = "ChandranT"
    val apiKey = "6D6F3513E278ABCT"
    val BASE_URI = "https://coolpay.herokuapp.com/api"
    implicit lazy val timeout = Timeout(10.seconds)
    val coolPlayOperationsActor: ActorRef = system.actorOf(CoolPlayOperationsActor.props, "CoolPlayOperationsActor")


    def authentication() ={
      val res= coolPlayOperationsActor ? Authenticate(username,apiKey)
      val result = Await.result(res, timeout.duration)
      println(result)
      println("done")
    }
    def addRecipient(): Unit = {
      coolPlayOperationsActor ? AddRecipient
    }

    def sendMoney: Unit ={
      coolPlayOperationsActor ? SendMoney
    }

    def isPaymentSuccessfull: Unit ={
      coolPlayOperationsActor ? IsPaymentSuccess
    }

    authentication()
    //coolPlayOperationsActor ! kill

  }
}