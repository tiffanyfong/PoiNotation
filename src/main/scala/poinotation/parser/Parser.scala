package poinotation.parser

import poinotation.ir._
import OnePoiMove._
import scala.language.postfixOps
import scala.util.parsing.combinator._

/**
  * -------------
  * Ideal Grammar
  * -------------
  *
  *       program ::= declarations name
  *       declarations ::= dec declarations | dec
  *       dec ::= name "=" move
  *
  *       name ::= sequence
  *       sequence ::= "(" sequence "*" num ")" | move "~" sequence | move
  *
  *       // and everything else in the current grammar
  *
  * ---------------
  * Current Grammar
  * ---------------
  *
  *       num ∈ 𝒵 (non-negative integer)
  *
  *       program ::= sequence
  *       sequence ::= move "~" sequence | move
  *       move ::= "(" move  |  property "," move  |  property ")"  |  ")"
  *
  *       property ::= spin  | dir | rotations | extended
  *       spin ::= "inspin" | "antispin"
  *       dir ::= "cw" | "clockwise" | "ccw" | "counterclockwise" | "none"
  *       rotations ::= num "-petal" | num "rotations"
  *       extended ::= "extend" | "extended" | "no-extend"
  *
  */

object PoiNotationParser extends RegexParsers with PackratParsers {
  // for parsing comments
  override protected val whiteSpace =
  """(\s|#.*)+""".r

  // regex
  def positiveInt: Parser[String] = """[1-9]\d*""".r
  def nonNegativeInt: Parser[String] = """\d+""".r

  // parsing interface
  def apply(s: String): ParseResult[List[OnePoiMove]] = {
    println(s"THE PROGRAM IS: $s\n")
    println(s"THE RESULT IS: ${parseAll(program, s)}\n")
    parseAll(program, s)
  }

  lazy val program: PackratParser[List[OnePoiMove]] = sequence

  lazy val sequence: PackratParser[List[OnePoiMove]] =
    (
      (move ~ "~" ~ sequence ^^ { case m ~ "~" ~ seq => m :: seq })
      | (move ^^ { m => List(m) })
      | failure("expected a move or sequence of moves")
      )

  lazy val move: PackratParser[OnePoiMove] =
    (
      ("(" ~> move)
      | (property ~ "," ~ move ^^ { case p ~ "," ~ m => m.addProperty(p) })
      | (property <~ ")" ^^ { p => defaultMove.addProperty(p) })
      | (")" ^^^ defaultMove)
      | failure("expected properties separated by commas and enclosed with parentheses")
      )

  // key-value pair where the key corresponds to the property in the IR case class,
  // and the value is a string that will be converted into the correct type for the specific key
  lazy val property: PackratParser[(String, String)] =
    (
      (spin ^^ { s => ("spin", s)})
      | (dir ^^ { d => ("dir", d) })
      | (rotations ^^ { r => ("rotations", r) })
      | (extended ^^ { e => ("extended", e)})
      | failure("could not parse one of the properties: spin, direction, # of handle rotations, extended")
      )

  lazy val spin: PackratParser[String] =
    (
      "inspin"
      | "antispin"
      | failure("""expected "inspin" or "antispin" for relative spin""")
      )

  lazy val dir: PackratParser[String] =
    (
      (("cw" | "clockwise") ^^^ "CW")
      | (("ccw" | "counterclockwise") ^^^ "CCW")
      | ("none" ^^^ "NONE")
      | failure("""expected "cw", "ccw", or "none" for direction of arm rotation""")
      )

  lazy val rotations: PackratParser[String] =
    (
      (positiveInt <~ "-petal" ^^ { n => s"${n.toInt-1}"})
      | (nonNegativeInt <~ "rotations")
      | failure("""expected at least 1 "-petal" and 0 "rotations" as an integer""")
      )

  lazy val extended: PackratParser[String] =
    (
      (("extended" | "extend") ^^^ "true")
      | ("no-extend" ^^^ "false")
      )
}
