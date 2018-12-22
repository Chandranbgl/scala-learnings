package com.facebook.coolplay

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.util.ByteString
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration._

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object CoolPlayOperationsActor {

  final case class Authenticate(username: String, apikey: String)

  final case class AddRecipient(recipient: Recipient)

  final case class Recipient(name: String)

  final case class SendMoney(payment: Payment)

  final case class Payment(amount: Double, currency: String, recipientId: String)
  final case object IsPaymentSuccess
  final case class Token(token:String)
  def props: Props = Props[CoolPlayOperationsActor]
  implicit val system = ActorSystem("coolPlayApp")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher
  val username = "ChandranT"
  val apiKey = "6D6F3513E278ABCT"
  val baseUri = "https://coolpay.herokuapp.com/api/"

}


class CoolPlayOperationsActor extends Actor with ActorLogging {

  import CoolPlayOperationsActor._
 // implicit val token = jsonFormat1(Token)
//  def parse(line: ByteString): Token = {line.utf8String.parseJson.convertTo[Token]}
  override def receive: Receive = {
    case authenticate: Authenticate =>
      implicit val token = jsonFormat1(Token)
      val responseFuture = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = s"${baseUri}login",
        entity = HttpEntity(ContentTypes.`application/json`, s"""{"username": "${authenticate.username}", "apikey": "${authenticate.apikey}"}""")
      ))

      val response = Await.result(responseFuture, 5.seconds)

      response match {
        case HttpResponse(StatusCodes.OK, _, entity, _) =>
          val byteStringFuture = entity.dataBytes.runFold(ByteString(""))(_ ++ _)
          val byteString = Await.result(byteStringFuture, 5.seconds)
          sender() ! byteString.utf8String.parseJson.convertTo[Token]
        case _ => sender() ! Token("")
      }

    case AddRecipient =>
      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"${baseUri}/login"))
      responseFuture
        .onComplete {
          case Success(res) => sender() ! true
          case Failure(_)   => sys.error("something wrong")
        }
      sender() ! true
    case SendMoney =>
      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"${baseUri}/login"))
      responseFuture
        .onComplete {
          case Success(res) => sender() ! true
          case Failure(_)   => sys.error("something wrong")
        }
      sender() ! true
    case IsPaymentSuccess =>
      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = s"${baseUri}/login"))
      responseFuture
        .onComplete {
          case Success(res) => sender() ! true
          case Failure(_)   => sys.error("something wrong")
        }
      sender() ! true
  }
}