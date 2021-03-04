import Index.extractWordsFrom

import java.io.File
import java.nio.charset.CodingErrorAction
import scala.annotation.tailrec
import scala.io.Codec
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

final case class Rank(value: Int) extends AnyVal with Ordered[Rank] {
  def +(other: Rank): Rank = Rank(this.value + other.value)

  def compare(other: Rank): Int = this.value.compare(other.value)
}

case class Index() {
  type Word = String
  type FileName = String

  var idx: Map[Word, Set[FileName]] =
    Map[Word, Set[FileName]]() withDefaultValue Set.empty

  case class ResultItem(fileName: FileName, rank: Rank)
      extends Ordered[ResultItem] {
    def +(other: ResultItem): ResultItem =
      other.fileName match {
        case this.fileName => ResultItem(this.fileName, this.rank + other.rank)
        case _             => throw new IllegalStateException("") // can't happen!
      }

    def compare(other: ResultItem): Int = this.rank.compare(other.rank)
  }

  case class ResultSet(set: Set[ResultItem]) {
    def union(other: ResultSet): ResultSet =
      ResultSet(
        (this.set.toSeq ++ other.set.toSeq)
          .groupBy(_.fileName)
          .map(g => g._2.reduce(_ + _))
          .toSet
      )

    def toResult: List[ResultItem] =
      set.toList
        .sorted(Ordering[ResultItem].reverse)
        .take(10)
  }

  object ResultSet {
    val empty: ResultSet = ResultSet(Set.empty)
  }

  def addFileLine(fileName: FileName, line: String): Unit =
    extractWordsFrom(line).foreach(w => idx = idx + (w -> (idx(w) + fileName)))

  def get(word: Word): ResultSet =
    ResultSet(idx(word).map(ResultItem(_, Rank(1))))

  def query(searchString: String): List[ResultItem] =
    extractWordsFrom(searchString)
      .map(get)
      .foldLeft(ResultSet.empty)(_ union _)
      .toResult
}

object Index {
  def extractWordsFrom(line: String): Iterator[String] =
    raw"([A-Za-z0-9]+)".r.findAllIn(line).map(_.toLowerCase)

  def indexDirFiles(dir: File): Index = {
    val idx = new Index()
    val codec = Codec.ISO8859
    codec.onMalformedInput(CodingErrorAction.IGNORE)
    for (f <- dir.listFiles if f.isFile) {
      val source = scala.io.Source.fromFile(f)(codec)
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
