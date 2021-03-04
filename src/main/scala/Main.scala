import Index.extractWordsFrom

import java.io.File
import scala.annotation.tailrec
import scala.util.Try
import scala.util.chaining.scalaUtilChainingOps

object Main extends App {
  Program
    .checkDir(args)
    .fold(
      println,
      Index.indexDirFiles(_).pipe(Program.iterate)
    )
}

case class Index() {
  type Word = String
  type FileName = String

  var idx: Map[Word, Set[FileName]] =
    Map[Word, Set[FileName]]() withDefaultValue Set.empty

  def addFileLine(fileName: FileName, line: String): Unit =
    extractWordsFrom(line).foreach(w => idx = idx + (w -> (idx(w) + fileName)))

  def get(word: Word): Set[FileName] = idx(word)

  def query(searchString: String): List[String] =
    extractWordsFrom(searchString)
      .map(get)
      .foldLeft(Set.empty[String])(_ union _)
      .toList
}

object Index {
  def extractWordsFrom(line: String): Iterator[String] =
    raw"([A-Za-z0-9]+)".r.findAllIn(line).map(_.toLowerCase)

  def indexDirFiles(dir: File): Index = {
    val idx = new Index()
    for (f <- dir.listFiles if f.isFile) {
      val source = scala.io.Source.fromFile(f)
      source.getLines
        .foreach(idx.addFileLine(f.getName, _))
      source.close()
    }
    idx
  }
}

object Program {
  import scala.io.StdIn.readLine

  sealed trait ReadFileError
  case object MissingPathArg extends ReadFileError
  case class NotDirectory(error: String) extends ReadFileError
  case class FileNotFound(t: Throwable) extends ReadFileError

  def checkDir(args: Array[String]): Either[ReadFileError, File] = {
    for {
      path <- args.headOption.toRight(MissingPathArg)
      file <- Try(new java.io.File(path))
        .fold(
          throwable => Left(FileNotFound(throwable)),
          file =>
            if (file.isDirectory) Right(file)
            else Left(NotDirectory(s"Path [$path] is not a directory"))
        )
    } yield file
  }

  @tailrec
  def iterate(index: Index): Unit = {
    print(s"search> ")
    val searchString = readLine()
    if (searchString == ":quit") System.exit(0)
    else {
      index.query(searchString) match {
        case r if r.isEmpty => println("no matches found ")
        case r              => println(r mkString "\n")
      }
    }
    iterate(index)
  }
}
