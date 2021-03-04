import java.io.File
import scala.annotation.tailrec
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
