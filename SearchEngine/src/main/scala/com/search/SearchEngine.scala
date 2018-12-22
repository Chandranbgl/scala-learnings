package com.search

object SearchEngine {
  def search(fileContents: Seq[(String, List[String])], searchString: String): Seq[(Int, String)] = {
    if(fileContents.length == 0) throw new Exception("No File contents or Invalid Path");
    fileContents.map { contentPerFile =>
      val searchWords = searchString.split(" ")
      val res = searchWords.map { word =>
        contentPerFile._2.contains(word.toLowerCase) match {
          case true => 1
          case false => 0
        }
      }
      (((res.reduce(_ + _).toFloat / searchWords.length).toDouble * 100).toInt, contentPerFile._1)
    }.sorted.reverse.take(10) //hard coded for top 10 files
  }
}
