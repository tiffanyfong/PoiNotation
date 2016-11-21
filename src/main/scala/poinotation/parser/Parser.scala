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
  *       declarations ::= dec declarations  |  dec
  *       dec ::= name "=" move
  *
  *       name ::= sequence
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
  *       sequence ::= sequence "~" duplicateMove  |  duplicateMove
  *       duplicateMove ::= duplicateMove "*" num  |  move
  *       move ::= "(" sequence ")"  |  json "}"
  *
  *       json ::= json "," property  |  "{" property  |  "{"
  *
  *       property ::=
  *         "extended" ":" bool
  *         |  ("arm" | "armSpin") ":" armSpin
  *         |  ("handle" | "handleSpin") ":" handleSpin
  *         |  "petals" ":" positiveInt
  *         |  "rotations" ":" nonNegativeInt
  *
  *       REGEX
  *       armSpin, bool, handleSpin, nonNegativeInt, positiveInt
  *
  */

object PoiNotationParser extends RegexParsers with PackratParsers {
  // for parsing comments
  override protected val whiteSpace =
  """(\s|#.*)+""".r

  // regex
  def positiveInt: Parser[String] = """[1-9]\d*""".r
  def nonNegativeInt: Parser[String] = """\d+""".r
  def bool: Parser[String] = "(true)|(false)".r
  def armSpin: Parser[String] = "(c?cw)|((counter)?clockwise)|(none)".r
  def handleSpin: Parser[String] = armSpin | "(anti-?spin)|(in-?spin)".r

  // parsing interface
  def apply(s: String): ParseResult[List[OnePoiMove]] = {
    println(s"DEBUG: The program is = $s")
    println(s"DEBUG: The result is = ${parseAll(program, s)}\n")
    parseAll(program, s)
  }

  lazy val program: PackratParser[List[OnePoiMove]] = sequence

  lazy val sequence: PackratParser[List[OnePoiMove]] =
    (
      (sequence ~ "~" ~ duplicateMove ^^ { case seq ~ "~" ~ dup => seq ++ dup })
      | duplicateMove
      | failure("expected a sequence of moves")
      )

  lazy val duplicateMove: PackratParser[List[OnePoiMove]] =
    (
      (duplicateMove ~ "*" ~ nonNegativeInt ^^ { case dup ~ "*" ~ n => List.fill(n.toInt)(dup).flatten})
      | move
      | failure("expected duplicate moves or a single move")
      )

  lazy val move: PackratParser[List[OnePoiMove]] =
    (
      ("(" ~> sequence <~ ")")
      | (json <~ "}" ^^ { j => List(j)})
      | failure("expected json or a sequence in parantheses")
      )

  lazy val json: PackratParser[OnePoiMove] =
    (
      (json ~ "," ~ property ^^ { case j ~ "," ~ p => j.addProperty(p) })
      | ("{" ~> property ^^ { p => defaultMove.addProperty(p) })
      | ("{" ^^^ defaultMove)
      | failure("failed to parse json")
      )

  // key-value pair where the key corresponds to the property in the IR case class,
  // and the value is a string that will be converted into the correct type for the specific key
  lazy val property: PackratParser[(String, String)] =
    (
      ("extended" ~> ":" ~> bool ^^ { b => ("extended", b)})
      | ("arm(Spin)?".r ~> ":" ~> armSpin ^^ {a => ("armSpin", a)})
      | ("handle(Spin)?".r ~> ":" ~> handleSpin ^^ {h => ("handleSpin", h)})
      | ("petals" ~> ":" ~> positiveInt ^^ {n => ("rotations", s"${n.toInt-1}")})
      | ("rotations" ~> ":" ~> nonNegativeInt ^^ {n => ("rotations", n)})
      | failure("could not parse one of the properties: spin, direction, # of handle rotations, extended")
      )
}
