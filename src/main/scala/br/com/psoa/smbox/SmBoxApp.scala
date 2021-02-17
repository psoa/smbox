package br.com.psoa.smbox

import java.io.File

/**
 *
 * Simple tool created to extract a specific kind of e-mail
 * and export it content to a sqlite database to keep the
 * information.
 *
 * A lot of fun and learning from it
 *
 * Tools:
 *  SQLite
 *  slick
 *  XML
 *  Apache James
 *
 */
object SmBoxApp extends App {
  if (args.length < 1) throw new IllegalArgumentException("Please inform the action.")
  val action = args(0)
  action match {
    case "extract" => extract()
    case "sql" => toSql()
    case _ => help()
  }

  def help(): Unit = {
    println("Please chose a valid action: (run <action> <in> <out> <subject>)")
    println("extract: Use it to read a MBOX file directory from a directory and export to another as XML")
    println("sql: Use it to read the generated XML file and insert the data in a sqlite database")
  }

  def extract() {
    if (args.length < 4) throw new IllegalArgumentException("invalid parameter: run <action> <in> <out> <subject>.")
    val readPath = args(1)
    val writePath = args(2)
    val filterSubject = args(3).toLowerCase
    //List("windows-1252", "iso-8859-1","utf-8", "us-ascii")
    List("iso-8859-1")
      .foreach(c => SmBox(readPath, writePath, filterSubject, c).run())
  }

  def toSql(): Unit = {
    val readPath = args(1)
    val writeDb = args(2)
    SQLite(writeDb, readPath).run()
  }
}
