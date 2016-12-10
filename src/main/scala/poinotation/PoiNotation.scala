package poinotation

import java.io.FileNotFoundException
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import poinotation.ir._
import poinotation.ir.Spin._
import poinotation.parser._


/**
  * Main program
  *
  * Usage: sbt "run <filepath>*"
  *     OR sbt <enter> run <filepath>*
  * Output: json for visual spinner simulator
  */
object PoiNotation extends App {

  val VSInstructions: String = 
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
| Note: Moves only go clockwise.                                       |
|                                                                      |
+----------------------------------------------------------------------+
"""

  val PoiInstructions: String = 
"""
+--------------------- 2D Poi Pattern Visualizer ----------------------+
|                                                                      |
| To see your poi moves in this simulator, copy and paste the URLs     |
| below. Each URL will simulate a single move, except those that are   |
| currently not supported.                                             |
|                                                                      |
| Note: This simulator's inspin flowers are actually antispin. Moves   |
|       only go clockwise.                                             |
|                                                                      |
+----------------------------------------------------------------------+
"""

  for (filepath <- args) {
    val fileContents = getFileContents(filepath)

    println(s"The inputted syntax is:\n$fileContents")

    // parse the choreography file
    val program = PoiNotationParser(fileContents)

    program match {
      // Error handling: syntax errors
      case e: PoiNotationParser.NoSuccess  => println(e)

      // if parsing succeeded
      case PoiNotationParser.Success(ir, _) =>

        // VisualSpinner3D simulator
        println(VSInstructions)
        printVisualSpinner(ir)

        // 2D Poi Pattern Visualizer simulator
        println(PoiInstructions)
        to2DPoi(ir).foreach{ println }

    }
  }

  /**
    * Converts IR to raw json string compatible with VisualSpinner3D (composer version)
    */
  def printVisualSpinner(moves: List[OnePoiMove]): Unit = {
    // defaults from VisualSpinner3D
    val defaultJson: JValue =
      parse("""{"prop1": { "hand": { "radius": 1, "azimuth": "NINE" },
        "prop": { "azimuth": "NINE" }, "color": "blue", "moves": [] } }""")

    val movesJson: JArray = moves.map(toVSMove)

    // prints JSON converted to string
    println( pretty(render(

      // updates the array of moves
      defaultJson transform {
        case JField("moves", _) => JField("moves", movesJson)
      }
    )))

    // Shows moves (unable to be simulated) after the main json
    val unsupportedMoves: List[String] =
      (moves zip movesJson.arr).filter(_._2 == JNothing).map(_._1.toJsonString)

    if (unsupportedMoves.nonEmpty) {
      println(s"NOTICE: The following moves cannot be simulated:\n")
      unsupportedMoves.foreach{ println }
    }
  }

  /**
   * Converts a OnePoiMove into a VisualSpinner recipe json
   *
   * Notes: Moves only go clockwise. Cat-eye is half-extended.
   *
   */
  def toVSMove(move: OnePoiMove): JValue = move match {

    // Hold
    case OnePoiMove(false, _, _, 0) => ("recipe" -> "staticspin") ~ ("speed" -> 0) ~ ("name" -> "Hold")

    // Static spin
    case OnePoiMove(false, _, _, 1) => ("recipe" -> "staticspin")

    // Extension
    case OnePoiMove(true, arm, handle, 1) if arm == handle => ("recipe" -> "extension")

    // Cat-Eye
    case OnePoiMove(true, _, ANTISPIN, 1) => ("recipe" -> "cateye")

    // Anti-Spin Flowers
    case OnePoiMove(true, _, ANTISPIN, 2) => ("recipe" -> "triquetra")
    case OnePoiMove(true, _, ANTISPIN, i) if i > 0 => ("recipe" -> "antispin") ~ ("petals" -> (i+1)) ~ ("name" -> s"${i+1}-Petal Anti-Spin Flower")

    // In-Spin Flowers
    case OnePoiMove(true, _, INSPIN, i) if i > 0 => ("recipe" -> "flower") ~ ("petals" -> (i-1)) ~ ("name" -> s"${i-1}-Petal In-Spin Flower")

    case _ => JNothing
  }


  /**
   * Converts IR to url string for rfong's 2D poi pattern visualizer
   *
   * Notes: Only one move can be simulated at a time. Inspin flowers aren't actually inspin.
   */
  def to2DPoi(moves: List[OnePoiMove]): List[String] = moves.map(get2DPoiURL)

  def get2DPoiURL(move: OnePoiMove): String = move match {
    // Extension
    case OnePoiMove(true, arm, handle, 1) if arm == handle => 
      s"""https://rfong.github.io/poi/#options={}&patterns=["extension","---"]&args=[null,null]\n"""

    // "In-Spin" Flowers
    case OnePoiMove(true, _, INSPIN, i) if (2 to 7) contains i =>
      s"""https://rfong.github.io/poi/#options={}&patterns=["n_petal_inspin","---"]&args=[[${i+1},45,0],null]\n"""

    // Anti-Spin Flowers
    case OnePoiMove(true, _, ANTISPIN, i) if (2 to 7) contains i => 
      s"""https://rfong.github.io/poi/#options={}&patterns=["n_petal_antispin","---"]&args=[[${i+1},45,0],null]\n"""

    case _ => s"CANNOT BE SIMULATED: ${move.toJsonString}\n"
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
