package br.com.psoa.smbox

import java.io._
import java.nio.charset.Charset

import br.com.psoa.smbox.Control._
import com.ibm.icu.text.{CharsetDetector, CharsetMatch}
import org.apache.commons.io.IOUtils
import org.apache.james.mime4j.MimeIOException
import org.apache.james.mime4j.dom.{Body, Multipart, SingleBody, TextBody}
import org.apache.james.mime4j.mboxiterator.{CharBufferWrapper, MboxIterator}
import org.apache.james.mime4j.message.DefaultMessageBuilder
import org.apache.james.mime4j.stream.MimeConfig

object SmBoxApp extends App {
  if (args.length < 3) throw new IllegalArgumentException("Please inform the origin and destination files.")
  val readPath = args(0)
  val writePath = args(1)
  val filterSubject = args(2).toLowerCase
  val charsetName = "windows-1252"
  val ENCODER = Charset.forName(charsetName).newEncoder
  val messageBuilder = new DefaultMessageBuilder
  messageBuilder.setContentDecoding(true)
  val config = new MimeConfig.Builder().setMaxLineLen(50000).build
  messageBuilder.setMimeEntityConfig(config)
  write("<feed>")
  write(System.lineSeparator)
  //val files = getListOfFiles(new File(readPath), List("mbox"))
  val files = List(new File("/Users/psoa/workspace/smbox/input/Enviado.mbox"))
  println("Iterate over the files bellow: (number of files [" + files.size + "])")
  for (file <- files) {
    try {
      println(file)
      val messages = MboxIterator
        .fromFile(file)
        .maxMessageSize(50 * 1024 * 1024) //MAX Gmail size for a single message
        //.charset(ENCODER.charset)
        .build
      messages.forEach(message => write(parseMessage(message, file.getName)))
    }catch {
      case e: Throwable => e.printStackTrace()
    }
  }
  write(System.lineSeparator)
  write("</feed>")

  def parseMessage(message: CharBufferWrapper, fileName: String) : Option[SmBoxMessage] = {
    try {
      val mail = messageBuilder.parseMessage(new ByteArrayInputStream(message.toString.getBytes))
      val subject = Option(mail.getSubject)
      val charset = Option(mail.getCharset)
      if (subject.getOrElse("undefined").toLowerCase.contains(filterSubject)) {
        val smMail = SmBoxMessage(subject, charset, Option(fileName),
          Option(mail.getDate), getTextPlain(charset, mail.getBody))
        return Some(smMail)
      }
    }catch {
      case e: MimeIOException => //println(e.getMessage) //For now just ignore
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
//      val writer = new StringWriter
//      IOUtils.copy(body.getInputStream, writer,
//        detectCharset(charset.getOrElse("UTF-8"), body.getInputStream))
//      Some(writer.toString)
    case body: TextBody =>
      val bao = new ByteArrayOutputStream
      body.writeTo(bao)
      Some(bao.toString(ENCODER.charset))
//      val writer = new StringWriter
//      IOUtils.copy(body.getInputStream, writer,
//        detectCharset(charset.getOrElse("UTF-8"), body.getInputStream))
//      Some(writer.toString)
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
  def write (message: Option[SmBoxMessage]): Unit = message match {
    case Some (m) =>
      if (m.subject.getOrElse("Undefined").toLowerCase.contains(filterSubject))
        write(m.toXml())
    case None => /* do nothing */
  }

  def write (message: String): Unit = {
    using(new BufferedWriter(new OutputStreamWriter(
      new FileOutputStream(writePath, true), ENCODER.charset))) {
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
}
