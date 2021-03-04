import java.io.File
import java.nio.charset.CodingErrorAction
import scala.io.Codec

case class Index() {
  import Index._

  val idx: collection.mutable.Map[Word, Set[FileName]] =
    collection.mutable.Map[Word, Set[FileName]]() withDefaultValue Set.empty

  def addFileLine(fileName: FileName, line: String): Unit =
    extractWords(line).foreach(w => idx(w) = idx(w) + fileName)

  def query(searchString: String): List[String] = {
    val words = extractWords(searchString).toList
    words
      .map(get)
      .foldLeft(ResultSet.empty)(_ union _)
      .toResult
      .map(_.toResultString(words.length))
  }

  private def get(word: Word): ResultSet =
    ResultSet(idx(word).map(ResultItem(_, Rank(1))))
}

object Index {
  type FileName = String
  type Word = String

  final case class Rank(value: Int) extends AnyVal with Ordered[Rank] {
    def +(other: Rank): Rank = Rank(this.value + other.value)

    def compare(other: Rank): Int = this.value.compare(other.value)

    def toResultString(base: Int): FileName = s"${(value / (base * 1.0) * 100).round.toString}%"
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

  final case class ResultSet(set: Set[ResultItem]) {
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
}
