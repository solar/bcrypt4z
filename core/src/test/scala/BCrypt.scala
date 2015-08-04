package org.sazabi.bcrypt4z

import org.mindrot.jbcrypt.{ BCrypt => JBCrypt }

import scalaprops._, Property.forAll

import _root_.scalaz._, std.string._
import BCrypt.ops._

object BCryptTest extends Scalaprops {
  private[this] val R = 4

  private[this] case class A(a: String)

  private[this] object A {
    implicit val BCryptA: BCrypt[A] = new BCrypt[A] {
      def value(a: A) = a.a
    }
  }

  val bcrypt = { 
    val string = forAll { (v: String) =>
      val hashed = v.bcryptWithRound(R)
      v.bcryptMatch(hashed)
    }(Gen.asciiString).toProperties("BCrypt[String]")

    val a = forAll { (v: String) =>
      val value = A(v)
      val hashed = value.bcryptWithRound(R)
      value.bcryptMatch(hashed)
    }(Gen.asciiString).toProperties("Custom BCrypt[A]")

    Properties.fromProps("BCrypt[A]", string, a)
  }

  val jbcrypt = {
    forAll { (v: String) =>
      val salt = JBCrypt.gensalt(R)
      val hashed = v.bcryptWithSalt(salt)
      val jhashed = JBCrypt.hashpw(v, salt)

      hashed == jhashed
    }(Gen.asciiString).toProperties("Generate same hash using with o.m.j.BCrypt")
  }
}
