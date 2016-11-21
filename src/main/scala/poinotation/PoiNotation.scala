package poinotation

// TODO import stuff
import java.io.FileNotFoundException
import poinotation.parser._
import scala.tools.nsc.EvalLoop

object PoiNotation extends EvalLoop with App {

  override def prompt = "Enter path to file > "

  /**
    * Main program: eval loop
    *
    * Input: path to file
    * Output: tbd
    */
  loop { line =>
    // parse the choreography file
    val program = PoiNotationParser(getFileContents(line))

    program match {
        // Error handling: syntax errors
      case e: PoiNotationParser.NoSuccess  => println(e)

        // if parsing succeeded
      case PoiNotationParser.Success(t, _) =>
        println(program.get)
        // TODO: convert IR to visualspinner json
    }
  }





  /**
    * Given a filename, get the contents of the file
    */
  def getFileContents(filename: String): String =
    try {
      io.Source.fromFile(filename).mkString
    }
    catch { // Error handling: non-existent file
      case e: FileNotFoundException =>
        println(e.getMessage)
        sys.exit(1)
    }
}
