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

// https://mvnrepository.com/artifact/com.ibm.icu/icu4j
libraryDependencies += "com.ibm.icu" % "icu4j" % "3.4.4"

libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test