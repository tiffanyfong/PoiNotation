package poinotation.ir

/**
  * Created by tmf on 11/12/16.
  *
  * This file contains "enums" used in the IR.
  * I chose to use case objects instead of Scala's enumerations so that I can pattern match
  */
import scala.language.implicitConversions

/**
  * Describes spin direction (for both the arm and the handle)
  *
  * CCW = counterclockwise
  * CW = clockwise
  * NONE = no spin
  *
  * Describes spins relative to the handle's spin
  *
  * ANTISPIN = spins in opposite direction of arm
  * INSPIN = spins in same direction as arm
  */
sealed trait Spin {def name: String }
object Spin {
  case object CCW extends Spin { val direction = -1; val name = "ccw"}
  case object CW extends Spin { val direction = 1; val name = "cw" }
  case object NONE extends Spin { val direction = 0; val name = "none" } // debating whether to keep this
  case object ANTISPIN extends Spin { val multiplier = -1; val name = "antispin" }
  case object INSPIN extends Spin { val multiplier = 1; val name = "inspin" }

  def opposite(s: Spin): Spin = s match {
    case CCW => CW
    case CW => CCW
    case NONE => NONE
    case _ => s
  }

  implicit def string2Spin(s: String): Spin = s.toLowerCase match {
    case "ccw" | "counterclockwise" => CCW
    case "cw" | "clockwise" => CW
    case "antispin" | "anti-spin" => ANTISPIN
    case "inspin" | "in-spin" => INSPIN
    case _ => NONE
  }
}

