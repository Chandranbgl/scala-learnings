
val awsV = "1.11.340"

name := "RedShiftOps"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-sts" % awsV,
  "com.amazonaws" % "aws-java-sdk-ses" % awsV,
  "com.amazonaws" % "aws-java-sdk-s3" % awsV
)