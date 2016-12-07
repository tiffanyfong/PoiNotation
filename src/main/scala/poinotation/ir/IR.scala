package poinotation.ir

/**
  * Created by tmf on 11/5/16.
  */

import net.liftweb.json._
import poinotation.ir.Spin._

sealed abstract class Move

// move with one arm rotation and a single poi
// the default is a "still" move. The poi is at the center of mass and doesn't move.
case class OnePoiMove(extended: Boolean = false,   // false = arm is not extended, true = arm extended outwards
                      armSpin: Spin = CW,
                      handleSpin: Spin = CW,
                      rotations: Int = 0)   // >= 0. Number of handle rotations per one arm rotation.
                 //     petals: Int = 0)    // >= 0. Specific to in-spin / anti-spin??
                      //TODO trace: String = "")   // shape of hand trajectory (i.e. circle, square, semicircle...)
  extends Move {

  implicit val formats = DefaultFormats

  def addProperty(property: (String, String)) = property match {
    case ("extended", bool) => this.copy(extended = bool.toBoolean)
    case ("armSpin", spin) => this.copy(armSpin = spin)
    case ("handleSpin", spin) => this.copy(handleSpin = spin)
    case ("rotations", n) => this.copy(rotations = n.toInt)
    case _ => this
  }

  def toJson: JValue = Extraction.decompose(this) transform {
    case JField("handleSpin", _) => JField("handleSpin", JString(this.handleSpin.name))
    case JField("armSpin", _) => JField("armSpin", JString(this.armSpin.name))
  }

  def toJsonString: String = compactRender(this.toJson)

//  // adjusts antispin rotations
//  def adjustAntispin(): OnePoiMove = this.handleSpin match {
//    case ANTISPIN => this.copy(rotations = this.rotations-1)
//    case _ => this
//  }
}

object OnePoiMove {
  def defaultMove: OnePoiMove = OnePoiMove()
}

// WHERE TO ADD PHASE

// move with one arm rotation and two pois moving simultaneously
case class TwoPoiMove(leftPoi: OnePoiMove, leftPhase: Int, rightPoi: OnePoiMove, rightPhase: Int) extends Move

