package org.sazabi.bcrypt4z
package scalaz

import org.mindrot.jbcrypt.{ BCrypt => JBCrypt }

import scalaprops._, Property.forAll

import _root_.scalaz._, std.anyVal._, std.string._
import BCrypt.ops._

object BCryptScalazTest extends Scalaprops with BCryptShow {
  private[this] val R = 4

  val bcryptFromShow = { 
    val int = forAll { (v: Int) =>
      val hashed = v.bcryptWithRound(R)
      v.bcryptMatch(hashed)
    }.toProperties("BCrypt[Int] from Show[Int]")

    Properties.fromProps("BCrypt[A] from Show[A]", int)
  }
}
