package com.search

import org.scalatest.{FunSuite, Matchers}

class SearchEngineSpec extends FunSuite with Matchers {

  test ("catching an exception when no files prescent on the givem path") {
    val fileContents = FileLoader.getFileContents("src/test/resources/files/dummy")
    val thrown = intercept[Exception] {
      SearchEngine.search(fileContents, "dummy")
    }
    assert(thrown.getMessage === "No File contents or Invalid Path")
  }

  test ("catching an exception when passing invalid path") {
    val fileContents = FileLoader.getFileContents("src/test/resources/files/dummy1")
    val thrown = intercept[Exception] {
      SearchEngine.search(fileContents, "dummy")
    }
    assert(thrown.getMessage === "No File contents or Invalid Path")
  }

  test ("top 10 files matching on search1") {
    val fileContents = FileLoader.getFileContents("src/test/resources/files")
    val res = SearchEngine.search(fileContents, "word12 word13 word1 word14")
    assert(s"${res(0)._2}: ${res(0)._1}%" === "file4.txt: 100%")
    assert(s"${res(1)._2}: ${res(1)._1}%" === "file3.txt: 100%")
    assert(s"${res(2)._2}: ${res(2)._1}%" === "file2.txt: 100%")
    assert(s"${res(3)._2}: ${res(3)._1}%" === "file16.txt: 100%")
    assert(s"${res(4)._2}: ${res(4)._1}%" === "file15.txt: 100%")
    assert(s"${res(5)._2}: ${res(5)._1}%" === "file14.txt: 100%")
    assert(s"${res(6)._2}: ${res(6)._1}%" === "file13.txt: 100%")
    assert(s"${res(7)._2}: ${res(7)._1}%" === "file1.txt: 100%")
    assert(s"${res(8)._2}: ${res(8)._1}%" === "file5.txt: 50%")
    assert(s"${res(9)._2}: ${res(9)._1}%" === "file9.txt: 25%")
  }

  test ("top 10 files matching on search2") {
    val fileContents = FileLoader.getFileContents("src/test/resources/files")
    val res = SearchEngine.search(fileContents, "word1 word2 word3 word4 word5 word6")
    assert(s"${res(0)._2}: ${res(0)._1}%" === "file6.txt: 83%")
    assert(s"${res(1)._2}: ${res(1)._1}%" === "file5.txt: 83%")
    assert(s"${res(2)._2}: ${res(2)._1}%" === "file4.txt: 83%")
    assert(s"${res(3)._2}: ${res(3)._1}%" === "file3.txt: 83%")
    assert(s"${res(4)._2}: ${res(4)._1}%" === "file2.txt: 83%")
    assert(s"${res(5)._2}: ${res(5)._1}%" === "file16.txt: 83%")
    assert(s"${res(6)._2}: ${res(6)._1}%" === "file15.txt: 83%")
    assert(s"${res(7)._2}: ${res(7)._1}%" === "file14.txt: 83%")
    assert(s"${res(8)._2}: ${res(8)._1}%" === "file13.txt: 83%")
    assert(s"${res(9)._2}: ${res(9)._1}%" === "file1.txt: 83%")
  }

  test ("top 3 files matching on search2") {
    val fileContents = FileLoader.getFileContents("src/test/resources/files")
    val res = SearchEngine.search(fileContents, "word100 word200")
    assert(s"${res(0)._2}: ${res(0)._1}%" === "file14.txt: 100%")
    assert(s"${res(1)._2}: ${res(1)._1}%" === "file11.txt: 100%")
    assert(s"${res(2)._2}: ${res(2)._1}%" === "file15.txt: 50%")
  }

  test ("top 1 files matching on search") {
    val fileContents = FileLoader.getFileContents("src/test/resources/files")
    val res = SearchEngine.search(fileContents, "word99")
    assert(s"${res(0)._2}: ${res(0)._1}%" === "file12.txt: 100%")
  }

  test ("zero matching on search") {
    val fileContents = FileLoader.getFileContents("src/test/resources/files")
    val res = SearchEngine.search(fileContents, "khwekdb")
    assert(res.head._1 === 0)
  }
}
