package br.com.psoa.smbox

import br.com.psoa.smbox.Control.using
import slick.jdbc.JdbcBackend
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.SQLiteProfile.api._
import slick.jdbc.meta.MTable

import java.io.File
import scala.concurrent.Await
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.xml.NodeSeq.Empty.text
import scala.language.postfixOps


case class SQLite(writeDB: String, readPath : String) {

  def run() : Unit = {
    using (Database.forURL("jdbc:sqlite:"+ writeDB,
      driver = "org.sqlite.JDBC", keepAliveConnection = true)) {
      database => {
        val posts = TableQuery[Post]
        val files = FileUtil.getListOfFiles(new File(readPath), List("xml"))
        println("Iterate over the files bellow: (number of files [" + files.size + "])")
        val setup = DBIO.seq((posts.schema).createIfNotExists)
        val setupResult = database.run(setup)
        Await.result(setupResult, Duration.Inf)
        files.foreach(file =>
          Await.result(database.run(posts += xmlToPost(file)), Duration.Inf))
      }
    }
  }

  def xmlToPost(file: File) : (Int, String, String, String) = {
    val xml = XML.loadFile(file)
    (0
      , ((xml \\ "post" \\ "subject") text).trim
      , ((xml \\ "post" \\ "date") text).trim
      , ((xml \\ "post" \\ "body") text).trim)
  }

  class Post(tag: Tag) extends Table[(Int, String, String, String)](tag, "POST") {
    def id = column[Int]("POST_ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def subject = column[String]("POST_SUBJECT")
    def date = column[String]("POST_DATE")
    def content = column[String]("POST_CONTENT")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, subject, date, content)
  }
}
