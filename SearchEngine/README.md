# SearchEngine

## Building and running

You will need SBT installed on your machine to create a jar or run it locally or execute test cases.
Refer below website to install SBT.
https://www.scala-sbt.org/0.13/docs/Setup.html

To create package or Jar file:
```
sbt packege
```
Find the Jar file in the below path
<project-dir>/target/scala-<version>
eg: target/scala-2.12/simplesearch_2.12-0.1.jar

To run the tests:

```
 sbt test
```

You can also run the application locally by:-

```
sbt run
```


To execute the Jar file on command line

scala <jar file> <search string - give in double quote if it has more than one word> <path of the files >

Note:
- space will be treated as delimiter for search words
- to quit the application use ":quit"(case sensitive)