package br.com.psoa.smbox
import java.io._
import java.io.File

object FileUtil {

  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }
}
