package br.com.psoa.smbox

import slick.jdbc.JdbcBackend.Database
import slick.jdbc.SQLiteProfile.api._

import java.io.File
import scala.xml.XML
import java.sql.Date
import scala.xml.NodeSeq.Empty.text
import scala.language.postfixOps


case class SQLite(writeDB: String, readPath : String) {

  val database = Database.forURL(
    "jdbc:sqlite:/db/brainstorm.db" format writeDB,
    driver = "org.sqlite.JDBC",
    keepAliveConnection = true)


  def run() : Unit = {

    println("Read directory:" + readPath)
    println("Write database:" + writeDB)

    val files = getListOfFiles(new File(readPath), List("xml"))
    println("Iterate over the files bellow: (number of files [" + files.size + "])")

    for (file <- files) {
      try {
        //println("Processing: " + file)
        val xml = XML.loadFile(file)
        val post = xml \\ "post" \\ "subject"

        //println("the date: " + post.text)
        //for (n <- xml.child) println(n)
        // val subject = (xml \\ "subject" \ "@text") text
        // val subject = (xml \\ "post" \\ "subject" \ "@text") text
        // println(subject)
      }catch {
        case e: Throwable => println("Unable to parse" + file) //e.printStackTrace()
      }
    }
  }

  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }


  class Post(tag: Tag) extends Table[(Int, String, Date, String)](tag, "POSTS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def subject = column[String]("SUBJECT")
    def date = column[Date]("DATE")
    def body = column[String]("BODY")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, subject, date, body)
  }
}
