package poinotation.parser

/**
  * Created by tmf on 11/12/16.
  */

import org.scalatest._
import poinotation.ir._
import OnePoiMove._
import Spin._
import scala.language.implicitConversions

class ParserTests extends FunSuite with Matchers {

  // some syntactic sugar for expressing parser tests
  implicit class ParseResultChecker(input: String) {
    def ~>(output: List[Move]): Boolean = {
      val result = PoiNotationParser(input)
      if (result.successful && (result.get === output))
        true
      else {
        info(s"$result")
        false
      }
    }

    def fails(): Boolean = {
      !PoiNotationParser(input).successful
    }
  }


  // it's unintentional, but the program is designed to modify the default move
  test("single default still move") {
    assert("()" ~> List(defaultMove))
  }

  test("concatenate default still moves") {
    assert("() ~ ()" ~> List(defaultMove, defaultMove))
  }

  test("change one property from default") {
    assert("(extend)" ~> List(OnePoiMove(extended = true)))
  }

  test("change all properties from default") {
    // TODO order shouldnt matter
    assert("(extend, antispin, ccw, 3-petal)" ~> List(OnePoiMove(true, CCW, CW, 2)))
  }

  test("testing the extended property") {
    assert("(extended)" ~> List(OnePoiMove(extended = true)))
    assert("(no-extend)" ~> List(OnePoiMove(extended = false)))
  }

  test("testing petal computation") {
    // positive ints
    for (i <- 1 to 5) {
      assert(s"($i-petal)" ~> List(OnePoiMove(rotations = i-1)))
    }

    // non-positive ints should fail
    for (i <- -2 to 0) {
      assert(s"($i-petal)".fails())
    }
  }

  test("testing rotations") {
    // nonnegative ints
    for (i <- 0 to 5) {
      assert(s"($i-rotation)" ~> List(OnePoiMove(rotations = i)))
    }

    // negative ints should fail
    assert("(-1-rotation)".fails())
  }

  test("testing multiple concatenation") {
    for (i <- 1 to 5) {
      assert(s"() * $i" ~> List.fill(i)(defaultMove))
      assert(s"(extend) * $i" ~> List.fill(i)(OnePoiMove(extended = true)))
    }
  }

  // TODO uh oh
  test("combine multiplier with concat") {
    assert("(extend, 1-rotation) * 2 ~ ()" ~>
      List(OnePoiMove(extended = true, rotations = 1), OnePoiMove(extended = true, rotations = 1), defaultMove))

    assert("(extend, 1-rotation) ~ () * 2" ~> List(OnePoiMove(extended = true, rotations = 1), defaultMove, defaultMove))
  }
}
