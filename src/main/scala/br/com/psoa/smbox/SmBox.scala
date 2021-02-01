package br.com.psoa.smbox

import br.com.psoa.smbox.Control.using
import com.ibm.icu.text.CharsetDetector
import org.apache.commons.io.IOUtils
import org.apache.james.mime4j.MimeIOException
import org.apache.james.mime4j.dom.{Body, Multipart, SingleBody}
import org.apache.james.mime4j.mboxiterator.{CharBufferWrapper, MboxIterator}
import org.apache.james.mime4j.message.DefaultMessageBuilder
import org.apache.james.mime4j.stream.MimeConfig

import java.io.{BufferedInputStream, BufferedWriter, ByteArrayInputStream, ByteArrayOutputStream, File, FileOutputStream, InputStream, OutputStreamWriter, StringWriter}
import java.nio.charset.{Charset, CharsetEncoder}

case class SmBox (readPath : String, writePath : String, filterSubject: String, charsetName: String) {

  val ENCODER: CharsetEncoder = Charset.forName(charsetName).newEncoder
  val messageBuilder = new DefaultMessageBuilder

  def run() : Unit = {

    println("Read directory:" + readPath)
    println("Write directory:" + writePath)
    println("filter subject:" + filterSubject)
    println("charset:" + charsetName)


    messageBuilder.setContentDecoding(true)
    val config = new MimeConfig.Builder().setMaxLineLen(50000).build
    messageBuilder.setMimeEntityConfig(config)
    val files = getListOfFiles(new File(readPath), List("mbox"))
    println("Iterate over the files bellow: (number of files [" + files.size + "])")

    for (file <- files) {
      try {
        println("Processing" + file + " with charset: " + charsetName)
        val messages = MboxIterator
          .fromFile(file)
          .maxMessageSize(50 * 1024 * 1024) //MAX Gmail size for a single message
          .build
        messages.forEach(message => write(parseMessage(message, file.getName), charsetName))
      }catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }

  def write (m: Option[SmBoxMessage], charsetName : String): Unit = m match {
    case Some (m) =>
      if (m.charset.getOrElse("Undefined").toLowerCase.contains(charsetName)) {
        write(m.getDate()
          + "_" + m.origin.getOrElse("Undefined")
          + ".xml", m.toXml())
      }
      write("available_charset.txt", m.charset.getOrElse("Undefined"))
    case None => /* do nothing */
  }

  def write (fileName: String, message: String): Unit = {
    using(new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(writePath + fileName, true), ENCODER.charset))) {
      source => {
        source.write(System.lineSeparator)
        source.write(message)
      }
    }
  }

  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  def parseMessage(message: CharBufferWrapper, fileName: String) : Option[SmBoxMessage] = {
    try {
      val mail = messageBuilder.parseMessage(new ByteArrayInputStream(message.toString.getBytes))
      val subject = Option(mail.getSubject)
      if (subject.getOrElse("undefined").toLowerCase.contains(filterSubject)) {
        val charset = Option(mail.getCharset)
        val smMail = SmBoxMessage(subject, Option(mail.getCharset), Option(fileName),
          Option(mail.getDate), getTextPlain(charset, mail.getBody))
        return Some(smMail)
      }
    }catch {
      case e: MimeIOException => println(e.getMessage) //For now just ignore
      case e: Throwable => e.printStackTrace()
    }
    None
  }

  def getTextPlain(charset: Option[String], body: Body) : Option[String] = body match {
    case body: Multipart =>
      body.getBodyParts.forEach(part => {
        if (part.getMimeType.contains("text/plain") ||
          part.getMimeType.contains("multipart/alternative"))
          return getTextPlain(Option(part.getCharset), part.getBody)
      })
      None
    case body: SingleBody =>
      val bao = new ByteArrayOutputStream
      body.writeTo(bao)
      Some(bao.toString(ENCODER.charset))
      val writer = new StringWriter
      IOUtils.copy(body.getInputStream, writer,
        detectCharset(charset.getOrElse("UTF-8"), body.getInputStream))
      Some(writer.toString)
    case _ =>
      println("Unhandled body type")
      None
  }

  def detectCharset (charset: String, inputStream: InputStream): String = {
    val detectedCharset = detectCharset(inputStream)
    if (detectedCharset != null) {
      val conf = detectedCharset.getConfidence
      if (conf >= 80)
        return detectedCharset.getName
    }
    charset
  }

  private def detectCharset(inputStream: InputStream) = {
    val cd = new CharsetDetector
    // CharDetector requires support of mark/reset
    val is = if (inputStream.markSupported) inputStream
    else new BufferedInputStream(inputStream)
    cd.setText(is)
    cd.enableInputFilter(true)
    cd.detect
  }
}
