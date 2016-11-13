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
    def ~>(output: List[Move]) = {
      val result = PoiNotationParser(input)
      if (result.successful && (result.get === output))
        true
      else {
        info(s"$result")
        false
      }
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
    for (i <- 1 to 5) {
      assert(s"($i-petal)" ~> List(OnePoiMove(rotations = i-1)))
    }
  }

  test("testing rotations") {
    for (i <- 0 to 5) {
      assert(s"($i rotations)" ~> List(OnePoiMove(rotations = i)))
    }
  }
}
