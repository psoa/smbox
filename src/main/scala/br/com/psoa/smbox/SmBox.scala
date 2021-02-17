package br.com.psoa.smbox

import java.io._
import java.nio.charset.{Charset, CharsetEncoder}

import br.com.psoa.smbox.Control.using
import com.ibm.icu.text.CharsetDetector
import org.apache.commons.io.IOUtils
import org.apache.james.mime4j.MimeIOException
import org.apache.james.mime4j.dom.{Body, Multipart, SingleBody}
import org.apache.james.mime4j.mboxiterator.{CharBufferWrapper, MboxIterator}
import org.apache.james.mime4j.message.DefaultMessageBuilder
import org.apache.james.mime4j.stream.MimeConfig

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
    val files = FileUtil.getListOfFiles(new File(readPath), List("mbox"))
    println("Iterate over the files bellow: (number of files [" + files.size + "])")

    for (file <- files) {
      try {
        println("Processing" + file + " with charset: " + charsetName)
        MboxIterator
          .fromFile(file)
          .maxMessageSize(50 * 1024 * 1024) //MAX Gmail size for a single message
          .build
          .forEach(message => write(fromMBOXToMessage(message, file.getName), charsetName))
      }catch {
        case e: Throwable => e.printStackTrace()
      }
    }
  }

  def write (m: Option[SmBoxMessage], charsetName : String): Unit = m match {
    case Some (m) =>
      val dirOrigin = String.valueOf(writePath + "/" + m.origin.getOrElse("Undefined") + "/")
      val directoryOrigin = new File(dirOrigin)
      if (!directoryOrigin.exists)
        directoryOrigin.mkdir
      val dir = String.valueOf(writePath + "/" + m.origin.getOrElse("Undefined") + "/" + charsetName + "/")
      val directory = new File(dir)
      if (!directory.exists)
        directory.mkdir
      val file = fileToWrite(dir, m.getDate(), 0)
      val postBuilder = new StringBuilder
        postBuilder.append("<?xml version=\"1.0\" encoding=\"")
          .append(charsetName)
          .append("\" ?>")
          .append(m.toXml())
      write(file.getAbsolutePath, postBuilder.toString())
    case None => /* do nothing */
  }

  def fileToWrite(dir: String, fileName: String, suffix: Int): File = {
    val file =  if (suffix == 0)
       new File(dir + "/"  + fileName  + ".xml") else new File(dir + "/"  + fileName + "_" + suffix  + ".xml")
    if (file.exists()) fileToWrite(dir, fileName, suffix + 1) else file
  }

  def write (file: String, message: String): Unit = {
    using(new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(file, true)
        , ENCODER.charset))) {
      source => {
        source.write(message)
      }
    }
  }

  def fromMBOXToMessage(message: CharBufferWrapper, fileName: String) : Option[SmBoxMessage] = {
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
      case e: Throwable => println(e.getMessage)

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
        detectCharset(charset.getOrElse("iso-8859-1"), body.getInputStream))
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
