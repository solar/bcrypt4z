package org.sazabi.bcrypt4z

import org.sazabi.bcrypt4z.all._

import org.mindrot.jbcrypt.{ BCrypt => JBCrypt }

import org.scalacheck._

import org.scalatest._, prop.PropertyChecks

import scalaz._, Scalaz._
import scalaz.concurrent._

class BCryptSpec extends FlatSpec with Matchers with PropertyChecks {
  val genStr = for {
    len <- Gen.choose(8, 24)
    str <- Gen.listOfN(len, Gen.alphaChar).map(_.mkString)
  } yield str

  val genSalt = Gen.wrap(Gen.const(JBCrypt.gensalt()))

  val genRound = Gen.choose(8, 11)

  implicit override val generatorDrivenConfig =
    PropertyCheckConfig(minSuccessful = 20)

  "BCryptString" should "hash password using bcrypt" in {
    forAll(genStr, genStr, genRound) { (a, b, round) =>
      val t = for {
        ha <- a.bcrypt
        hb <- b.bcryptWithRound(round)
        aa <- ha.bcryptMatch(a)
        ab <- ha.bcryptMatch(b)
        ba <- hb.bcryptMatch(a)
        bb <- hb.bcryptMatch(b)
        jha <- Task { JBCrypt.hashpw(a, JBCrypt.gensalt()) }
        jaa <- jha.bcryptMatch(a)
        jab <- jha.bcryptMatch(b)
      } yield {
        aa shouldBe true
        ab shouldBe false
        ba shouldBe false
        bb shouldBe true

        JBCrypt.checkpw(a, ha) shouldBe true
        JBCrypt.checkpw(b, ha) shouldBe false

        JBCrypt.checkpw(a, hb) shouldBe false
        JBCrypt.checkpw(b, hb) shouldBe true

        jaa shouldBe true
        jab shouldBe false
      }

      t.run
    }
  }

  it should "generate same hash using with o.m.j.BCrypt" in {
    forAll(genStr, genSalt) { (str, salt) =>
      val t = for {
        h1 <- str.bcryptWithSalt(salt)
        h2 <- Task { JBCrypt.hashpw(str, salt) }
      } yield {
        h1 shouldBe h2
      }

      t.run
    }
  }
}
