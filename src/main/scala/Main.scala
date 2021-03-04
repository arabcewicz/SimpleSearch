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
      Index.createWithDirFiles(_).pipe(Program.iterate)
    )
}

case class Index() {
  type Word = String
  type FileName = String

  import Index._

  val idx: collection.mutable.Map[Word, Set[FileName]] =
    collection.mutable.Map[Word, Set[FileName]]() withDefaultValue Set.empty

  def addFileLine(fileName: FileName, line: String): Unit =
    extractWords(line).foreach(w => idx(w) = idx(w) + fileName)

  def get(word: Word): ResultSet =
    ResultSet(idx(word).map(ResultItem(_, Rank(1))))

  def query(searchString: String): List[String] = {
    val words = extractWords(searchString).toList
    words
      .map(get)
      .foldLeft(ResultSet.empty)(_ union _)
      .toResult
      .map(_.toResultString(words.length))
  }
}

object Index {
  type FileName = String

  final case class Rank(value: Int) extends AnyVal with Ordered[Rank] {
    def +(other: Rank): Rank = Rank(this.value + other.value)

    def compare(other: Rank): Int = this.value.compare(other.value)

    def toResultString(allWords: Int): FileName = s"${(value / (allWords * 1.0) * 100).round.toString}%"
  }

  final case class ResultItem(fileName: FileName, rank: Rank) extends Ordered[ResultItem] {
    def +(other: ResultItem): ResultItem =
      other.fileName match {
        case this.fileName => ResultItem(this.fileName, this.rank + other.rank)
        case _             => throw new IllegalStateException("") // can't happen!
      }

    def compare(other: ResultItem): Int = this.rank.compare(other.rank)

    def toResultString(base: Int): String =
      s"$fileName : ${rank.toResultString(base)}"
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

  def extractWords(line: String): Iterator[String] = {
    "([A-Za-z0-9]+)".r
      .findAllMatchIn(line)
      .map(g => g.group(1).toLowerCase)
      .filter(_.length > 1)
  }
  def createWithDirFiles(dir: File): Index = {
    val idx = new Index()
    val codec = Codec.ISO8859
    codec.onMalformedInput(CodingErrorAction.IGNORE)
    var counter = 0
    for (f <- dir.listFiles if f.isFile && f.canRead) {
      counter += 1
      val source = scala.io.Source.fromFile(f)(codec)
      source.getLines
        .foreach(idx.addFileLine(f.getName, _))
      source.close()
    }
    println(s"$counter files read in directory ${dir.getName}")
    idx
  }

  def createWithStrings(input: List[(String, String)]): Index = {
    val idx = new Index()
    for ((name, content) <- input) {
      val source = scala.io.Source.fromString(content)
      source.getLines
        .foreach(idx.addFileLine(name, _))
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
        case r if r.isEmpty => println("no matches found")
        case r              => println(r mkString ", ")
      }
    }
    iterate(index)
  }
}
