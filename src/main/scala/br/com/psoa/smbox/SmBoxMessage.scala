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
      .append(System.lineSeparator)
      .append("<post>")
      .append(System.lineSeparator)
      .append("<subject>")
      .append(System.lineSeparator)
      .append(subject.getOrElse("Undefined"))
      .append(System.lineSeparator)
      .append("</subject>")
      .append(System.lineSeparator)
      .append("<date>")
      .append(System.lineSeparator)
      .append(getDate(date, fmt))
      .append(System.lineSeparator)
      .append("</date>")
      .append(System.lineSeparator)
      .append("<body>")
      .append(System.lineSeparator)
      .append(body.getOrElse("Undefined"))
      .append(System.lineSeparator)
      .append("</body>")
      .append(System.lineSeparator)
      .append("</post>")
      .append(System.lineSeparator)
      .toString()
  }
  def getDate() : String = getDate(date, new SimpleDateFormat("yyyyMMdd"))

  def getDate (date: Option[Date], formatter: DateFormat): String = date match {
    case  Some(d) =>  formatter.format(d)
    case None => "Undefined"
  }

  def fromXML (xml : String): Unit = {

  }


}
