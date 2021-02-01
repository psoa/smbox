package br.com.psoa.smbox

// run /home/psoa/dev/smbox/in/ /home/psoa/dev/smbox/out brainstorm
// Remember to convert the files to the Unix End of line (\n) instead of (\r\n) from windows files - GMAIL do that
// for f in *.mbox; do dos2unix -f $f; done

object SmBoxApp extends App {
  if (args.length < 3) throw new IllegalArgumentException("Please inform the origin and destination files.")
  val readPath = args(0)
  val writePath = args(1)
  val filterSubject = args(2).toLowerCase
  val gmailSmBox = SmBox
  SmBox(readPath, writePath, filterSubject, "windows-1252".toLowerCase).run()
  SmBox(readPath, writePath, filterSubject, "ISO-8859-1".toLowerCase).run()
  SmBox(readPath, writePath, filterSubject, "us-ascii".toLowerCase).run()
}
