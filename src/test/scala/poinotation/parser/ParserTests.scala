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
    assert("{}" ~> List(defaultMove))
  }

  test("concatenate default still moves") {
    assert("{} ~ {}" ~> List(defaultMove, defaultMove))
    assert("{extended: true} ~ {} ~ {arm: none}" ~> List(OnePoiMove(extended = true), defaultMove, OnePoiMove(armSpin = NONE)))
  }

  test("change one property from default") {
    assert("{extended: true}" ~> List(OnePoiMove(extended = true)))
  }

  test("change all properties from default") {
    assert("{extended: true, arm: ccw, handle: antispin, petals: 3}" ~> List(OnePoiMove(true, CCW, ANTISPIN, 2)))

    // order shouldn't matter
    assert("{handle: antispin, petals: 3, extended: true, arm: ccw}" ~> List(OnePoiMove(true, CCW, ANTISPIN, 2)))
  }

  test("testing the extended property") {
    assert("{extended: true}" ~> List(OnePoiMove(extended = true)))
    assert("{extended: false}" ~> List(OnePoiMove(extended = false)))
  }

  test("testing petal computation") {
    // positive ints
    for (i <- 1 to 5) {
      assert(s"{petals: $i}" ~> List(OnePoiMove(rotations = i-1)))
    }

    // non-positive ints should fail
    for (i <- -2 to 0) {
      assert(s"{petals: $i}".fails())
    }
  }

  test("testing rotations") {
    // nonnegative ints
    for (i <- 0 to 5) {
      assert(s"{rotations: $i}" ~> List(OnePoiMove(rotations = i)))
    }

    // negative ints should fail
    assert("{rotations: -1}".fails())
  }

  // TODO: avoid petals + rotations in same json

  test("testing spins") {
    assert("{armSpin: cw, handleSpin: none}" ~> List(OnePoiMove(armSpin = CW, handleSpin = NONE)))
    assert("{arm: ccw, handle: inspin}" ~> List(OnePoiMove(armSpin = CCW, handleSpin = INSPIN)))
    assert("{arm: cw, handle: anti-spin}" ~> List(OnePoiMove(armSpin = CW, handleSpin = ANTISPIN)))
    assert("{arm: none, handle: cw}" ~> List(OnePoiMove(armSpin = NONE, handleSpin = CW)))
  }

  test("testing duplicate moves") {
    for (i <- 1 to 5) {
      assert(s"{} * $i" ~> List.fill(i)(defaultMove))
      assert(s"{extended: true} * $i" ~> List.fill(i)(OnePoiMove(extended = true)))
      assert(s"{extended: true, rotations: $i} * $i" ~> List.fill(i)(OnePoiMove(extended = true, rotations = i)))
    }
  }

  test("duplicates should have tighter binding than concat") {
    assert("{extended: true, rotations: 1} * 2 ~ {}" ~>
      List(OnePoiMove(extended = true, rotations = 1), OnePoiMove(extended = true, rotations = 1), defaultMove))

    assert("{extended: true, rotations: 1} ~ {} * 2" ~> List(OnePoiMove(extended = true, rotations = 1), defaultMove, defaultMove))
  }

  test("parentheses should change natural ordering") {
    assert("({extended: true} ~ {}) * 2" ~> List(OnePoiMove(extended = true), defaultMove, OnePoiMove(extended = true), defaultMove))
  }
}
