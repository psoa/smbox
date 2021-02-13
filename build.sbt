name := "SmBox"

version := "0.1"

scalaVersion := "2.13.1"
// https://mvnrepository.com/artifact/com.sun.mail/javax.mail
libraryDependencies += "com.sun.mail" % "javax.mail" % "1.5.6"
// http://james.apache.org/mime4j/dependency-info.html
libraryDependencies += "org.apache.james" % "apache-mime4j" % "0.8.3"
// https://mvnrepository.com/artifact/com.google.guava/guava
libraryDependencies += "com.google.guava" % "guava" % "29.0-jre"

libraryDependencies += "commons-io" % "commons-io" % "2.7"

//https://scala-slick.org/doc/3.3.3/introduction.html
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3"
)

libraryDependencies ++= Seq("org.scala-lang.modules" % "scala-xml_2.13" % "1.3.0")

//https://github.com/xerial/sqlite-jdbc
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.27.2.1"

// https://mvnrepository.com/artifact/com.ibm.icu/icu4j
libraryDependencies += "com.ibm.icu" % "icu4j" % "3.4.4"

libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test