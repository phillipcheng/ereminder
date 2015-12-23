name := "stockmlscala"
 
version := "1.0"
 
scalaVersion := "2.11.7"
 
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.4.1"
libraryDependencies += "org.apache.spark" %% "spark-mllib" % "1.4.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.4.1"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka" % "1.4.1"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"