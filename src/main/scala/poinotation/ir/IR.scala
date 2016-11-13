package poinotation.ir

/**
  * Created by tmf on 11/5/16.
  */

import poinotation.ir.Spin._
import scala.language.implicitConversions

sealed abstract class Move

// move with one arm rotation and a single poi
// the default is a "still" move. The poi is at the center of mass and doesn't move.
case class OnePoiMove(extended: Boolean = false,   // false = arm is not extended, true = arm extended outwards
                      armSpin: Spin = CW,
                      handleSpin: Spin = CW,
                      rotations: Int = 0)   // >= 0. Number of handle rotations per one arm rotation.
                      //TODO trace: String = "")   // shape of hand trajectory (i.e. circle, square, semicircle...)
  extends Move {

  def addProperty(property: (String, String)): OnePoiMove = property match {
    case ("spin", "antispin") => this.copy(handleSpin = opposite(this.armSpin))
    case ("spin", "inspin") => this.copy(handleSpin = this.armSpin)
    case ("dir", d) => this.copy(armSpin = d)
    case ("rotations", r) => this.copy(rotations = r.toInt)
    case ("extended", "true") => this.copy(extended = true)
    case ("extended", "false") => this.copy(extended = false)
    case _ => this // given property is unaccounted for
  }
}

object OnePoiMove {
  def defaultMove: OnePoiMove = OnePoiMove()
}

// WHERE TO ADD PHASE

// move with one arm rotation and two pois moving simultaneously
case class TwoPoiMove(leftPoi: OnePoiMove, leftPhase: Int, rightPoi: OnePoiMove, rightPhase: Int) extends Move

