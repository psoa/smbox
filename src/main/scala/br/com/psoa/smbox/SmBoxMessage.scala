package br.com.psoa.smbox

import java.io.ByteArrayOutputStream
import java.text.{DateFormat, SimpleDateFormat}
import java.util.Date
import org.apache.james.mime4j.dom.{Body, Message, Multipart, TextBody}

case class SmBoxMessage(val subject: Option[String], charset: Option[String], origin: Option[String],
                        val date: Option[Date], val body: Option[String]) {

  val fmt = new SimpleDateFormat("yyyy-MM-dd")

  def toXml(): String = {
    val postBuilder = new StringBuilder
    postBuilder
      .append("<post>")
      .append("<subject>")
      .append(subject.getOrElse("Undefined"))
      .append("</subject>")
      .append("<date>")
      .append(getDate(date, fmt))
      .append("</date>")
      .append("<body>")
      .append(body.getOrElse("Undefined"))
      .append("</body>")
      .append("</post>")
      .toString()
  }

  def getDate() : String = getDate(date, new SimpleDateFormat("yyyyMMdd"))

  def getDate (date: Option[Date], formatter: DateFormat): String = date match {
    case  Some(d) =>  formatter.format(d)
    case None => "Undefined"
  }
}
