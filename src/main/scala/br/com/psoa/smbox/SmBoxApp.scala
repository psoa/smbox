package br.com.psoa.smbox

import br.com.psoa.smbox.SmBoxApp.args

import java.io.File

// run <action> <in> <out> <subject>
// Remember to convert the files to the Unix End of line (\n) instead of (\r\n) from windows files - GMAIL do that
// for f in *.mbox; do dos2unix -f $f; done

object SmBoxApp extends App {
  if (args.length < 1) throw new IllegalArgumentException("Please inform the action.")
  val action = args(0)
  action match {
    case "extract" => extract()
    case "sql" => toSql()
    case _ => help()
  }

  def help(): Unit = {
    println("Please chose a valid action:")
    println("extract: Use it to read a MBOX file directory from a directory and export to another as XML")
  }

  def extract() {
    if (args.length < 4) throw new IllegalArgumentException("invalid parameter: run <action> <in> <out> <subject>.")
    val readPath = args(1)
    val writePath = args(2)
    val filterSubject = args(3).toLowerCase
    List("windows-1252", "iso-8859-1", "us-ascii")
      .foreach(c => SmBox(readPath, writePath, filterSubject, c).run())

  }

  def toSql(): Unit = {
    val readPath = args(1)
    //val writePath = args(2)
    val directory = new File("db/brainstorm.db")
    if (!directory.exists) {

    }
    SQLite("brain", readPath).run()

  }



}
