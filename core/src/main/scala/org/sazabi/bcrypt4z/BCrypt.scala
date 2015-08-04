package org.sazabi.bcrypt4z

import org.mindrot.jbcrypt.{ BCrypt => JBCrypt }
import simulacrum._

@typeclass trait BCrypt[A] {
  def value(a: A): String

  def bcrypt(a: A): String = JBCrypt.hashpw(value(a), JBCrypt.gensalt())

  def bcryptWithRound(a: A)(round: Int): String =
    JBCrypt.hashpw(value(a), JBCrypt.gensalt(round))

  def bcryptWithSalt(a: A)(salt: String): String =
    JBCrypt.hashpw(value(a), salt)

  def bcryptMatch(a: A)(hashed: String): Boolean =
    JBCrypt.checkpw(value(a), hashed)
}

trait LowPriorityBCrypt {
  implicit val StringBCrypt: BCrypt[String] = new BCrypt[String] {
    def value(a: String) = a
  }
}

object BCrypt extends LowPriorityBCrypt
