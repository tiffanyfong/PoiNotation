package poinotation.parser

import poinotation.ir._
import scala.language.postfixOps
import scala.util.parsing.combinator._

/**
  * -----------
  * Grammar
  * -----------
  *
  *                   n ∈ 𝒵
  *
  *       e ∈ Expr ::= e + t | e - t | t
  *       t ∈ Term ::= t * f | t / f | f
  *       f ∈ Fact ::= n | ( e )
  *
  */

object PoiNotationParser extends RegexParsers {

}
