package poinotation.ir

/**
  * Created by tmf on 11/5/16.
  */

sealed abstract class Move

// move with one arm rotation and a single poi
case class OnePoiMove(extend: Int,      // 0 = arm is not extended, 1 = arm extended outwards
                      armSpin: Int,     // -1 = counterclockwise, 0 = no arm spin, 1 = clockwise
                      handleSpin: Int,  // -1 = counterclockwise, 0 = no handle spin, 1 = clockwise
                      rotations: Int,   // >= 0. Number of handle rotations per one arm rotation.
                      trace: String)    // shape of hand trajectory (i.e. circle, square, semicircle...)
  extends Move

// move with one arm rotation and two pois moving simultaneously
case class TwoPoiMove(poi1: OnePoiMove, poi2: OnePoiMove) extends Move
