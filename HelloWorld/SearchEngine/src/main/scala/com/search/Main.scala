package com.search

import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object Main {
  def main(args: Array[String]): Unit ={
    if (args.length < 1) {
      println("useage: need atleast one parameter \"scala <jar file path> <file path>\"")
      System.exit(1)
    }
    while(true) {
      readLine("search> ") match {
        case ":quit" => System.exit(0)
        case searchString if(searchString.replaceAll(" ", "") == "") => None
        case searchString =>
          Try {
            SearchEngine.search(
              FileLoader.getFileContents(args(0)),
              searchString
            )
        } match {
            case Success(persentagePerFile) =>
              if(persentagePerFile.head._1 != 0) {
                persentagePerFile.foreach { res =>
                  (res._1, res._2) match {
                    case (0, fileName) => None
                    case (percentage, fileName) =>
                      println(s"${fileName}: ${percentage}%")
                  }
                }
              } else { println("no matches found") }
            case Failure(e) =>
              println(s"Oops search Error!, ${e}")
              System.exit(1)
          }
      }
    }
  }
}
