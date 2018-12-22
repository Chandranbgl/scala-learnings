import java.io.File

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._

import scala.util.Try


object Main extends App{


  val config = {
    val default = ConfigFactory.load()
    val merged = Option(System.getProperty("app.config")) map { configFile =>
      ConfigFactory.parseFile(new File(configFile)).withFallback(default)
    } getOrElse default

    merged.resolve()
  }

  val config1 = config.as[Config]("conf.test")

  println(config1.as[String]("test1.man"))

  def fun[T](a: Int)(body:Int => T): Int ={
    println("hello2")
    body(20)
    a * 2
  }
  println("hello1")
  fun(10) {  x =>
    val y = x*4
    println("hello")
    println(y)
  }

}
