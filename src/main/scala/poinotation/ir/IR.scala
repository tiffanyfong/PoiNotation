package main.scala.poinotation.ir

/**
  * Created by tmf on 11/5/16.
  */

sealed abstract class Move

case class OnePoiMove(extend: Int, armSpin: Int, handleSpin: Int, rotations: Int, trace: String) extends Move
case class TwoPoiMove(poi1: OnePoiMove, poi2: OnePoiMove) extends Move