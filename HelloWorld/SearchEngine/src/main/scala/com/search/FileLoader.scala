package com.search

import java.io.File
import scala.io.Source
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

object FileLoader {

  def getFileList(filePath: String): List[File] ={
    val f = new File(filePath)
    if (f.exists && f.isDirectory) {
      f.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def stripMargins(word: String): String ={
    val pattn = "[,.\"\']".r
    pattn.findAllIn(word).isEmpty match {
      case true => word
      case false =>
        if ( word.contains(".")){
          word.stripSuffix(".")
        }
        else if( word.contains(",")){
          word.stripSuffix(",")
        }
        else if( word.contains("\"")){
          word.stripSuffix("\"").stripPrefix("\"")
        }
        else if( word.contains("\'")){
          word.stripSuffix("\'").stripPrefix("\'")
        }
        else {
          word
        }
    }
  }

  def getFileContents(filePath: String): Seq[(String, List[String])] = getFileList(filePath).map { file =>
    val reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8")).lines.toArray
    (file.getName, reader.mkString(" ").split(" ").toList.filter(_ != "").map { word =>
      stripMargins(word).toLowerCase
    })
  }
}
