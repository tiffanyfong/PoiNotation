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
  */
sealed trait Spin {def multiplier: Int}
object Spin {
  case object CCW extends Spin { val multiplier = -1 }
  case object CW extends Spin { val multiplier = 1 }
  case object NONE extends Spin { val multiplier = 0 } // debating whether to keep this

  def opposite(s: Spin): Spin = s match {
    case CCW => CW
    case CW => CCW
    case NONE => NONE
  }

  implicit def string2Spin(s: String): Spin = s match {
    case "CCW" => CCW
    case "CW" => CW
    case _ => NONE
  }
}

