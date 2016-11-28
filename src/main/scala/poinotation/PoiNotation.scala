package poinotation

// TODO import stuff
import java.io.FileNotFoundException
import poinotation.ir._
import poinotation.ir.Spin._
import poinotation.parser._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import scala.tools.nsc.EvalLoop


/**
  * Main program
  *
  * Usage: sbt "run <filepath>*"
  *     OR sbt <enter> run <filepath>*
  * Output: json for visual spinner simulator
  */
object PoiNotation extends App {

  val instructions: String = 
"""
+-------------------------- VisualSpinner3D ---------------------------+
|                                                                      |
| To see your poi moves in this simulator, go to                       |
|                                                                      |
| https://infiniteperplexity.github.io/visual-spinner-3d/composer.html |
|                                                                      |
| Click on "Import from JSON". Copy and paste the json below (which    |
| represents your poi moves) into the simulator. Any moves from the    |
| DSL that cannot be simulated will not appear in the json.            |
|                                                                      |
+----------------------------------------------------------------------+
"""

  for (filepath <- args) {
    // parse the choreography file
    val program = PoiNotationParser(getFileContents(filepath))

    program match {
      // Error handling: syntax errors
      case e: PoiNotationParser.NoSuccess  => println(e)

      // if parsing succeeded
      case PoiNotationParser.Success(t, _) =>
        val ir = program.get
        println(instructions)
        println(toVisualSpinner(ir) + "\n")
    }
  }

  /**
    * Converts IR to raw json string compatible with VisualSpinner3D (composer version)
    */
  def toVisualSpinner(moves: List[OnePoiMove]): String = {
      // defaults from VisualSpinner3D
      val defaultJson: JValue = 
        parse("""{"prop1": { "hand": { "radius": 1, "azimuth": "NINE" }, 
          "prop": { "azimuth": "NINE" }, "color": "blue", "moves": [] } }""")


      val movesArray: JArray = moves.map(toVSMove(_))

      // converts JSON to string
      pretty(render(

        // updates the array of moves
        defaultJson transform {
          case JField("moves", _) => JField("moves", movesArray)
        }
      ))
  }

  /**
   * Converts a OnePoiMove into a VisualSpinner recipe json
   */
  def toVSMove(move: OnePoiMove): JValue = move match {

    // Nothing moves
    case OnePoiMove(false, _, _, 0) => (("recipe" -> "staticspin") ~ ("speed" -> 0))

    // Static spin
    case OnePoiMove(false, _, _, 1) => ("recipe" -> "staticspin")

    // Extension
    case OnePoiMove(true, arm, handle, 1) if arm == handle => ("recipe" -> "extension")

    // In-Spin Flowers
    case OnePoiMove(true, _, INSPIN, i) => (("recipe" -> "inspin") ~ ("name" -> s"$i-Petal In-Spin Flower")) // TODO check

    // Anti-Spin Flowers
    case OnePoiMove(true, _, ANTISPIN, 2) => (("recipe" -> "triqueta"))
    case OnePoiMove(true, _, ANTISPIN, i) => (("recipe" -> "antispin") ~ ("name" -> s"${i+1}-Petal Anti-Spin Flower")) // TODO check

    case _ => JNothing
  }


  // // Converts IR to url string to rfong's 2D poi pattern visualizer
  // // Only one move can be simulated at a time
  // def toPoi(moves: List[OnePoiMove]): String = {
  //   val defaultURL = "https://rfong.github.io/poi/"
  //   moves.headOption match {
  //     case None => defaultURL
  //     case Some(m)
  //   }
  // }

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
