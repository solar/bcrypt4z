package org.sazabi.bcrypt4z

import java.util.concurrent.ExecutorService

import org.mindrot.jbcrypt.{ BCrypt => JBCrypt }

import scalaz.Show
import scalaz.concurrent.Task
import scalaz.concurrent.Strategy.{ DefaultExecutorService => ES }

trait BCrypt[F] {
  def value(f: F): String

  def bcrypt(f: F)(implicit es: ExecutorService = ES): Task[String] = Task {
    JBCrypt.hashpw(value(f), JBCrypt.gensalt())
  }

  def bcryptWithRound(f: F, round: Int)(implicit es: ExecutorService = ES): Task[String] =
    Task { JBCrypt.hashpw(value(f), JBCrypt.gensalt(round)) }

  def bcryptWithSalt(f: F, salt: String)(implicit es: ExecutorService = ES): Task[String] =
    Task { JBCrypt.hashpw(value(f), salt) }

  def bcryptMatch(hashed: String, target: F)(implicit es: ExecutorService = ES): Task[Boolean] =
    Task { JBCrypt.checkpw(hashed, value(target)) }
}

trait LowPriorityBCrypts {
  implicit def ShowToBCrypt[F: Show]: BCrypt[F] = new BCrypt[F] {
    def value(f: F) = Show[F].shows(f)
  }
}

trait BCrypts extends LowPriorityBCrypts {
  /** No need of Show for String. */
  implicit val StringBCrypt: BCrypt[String] = new BCrypt[String] {
    def value(f: String) = f
  }
}

object BCrypt extends BCrypts

final class BCryptOps[F] private[bcrypt4z] (val self: F) extends AnyVal {
  def bcrypt(implicit F: BCrypt[F], es: ExecutorService = ES): Task[String] =
    F.bcrypt(self)(es)

  def bcryptWithRound(round: Int)(implicit F: BCrypt[F], es: ExecutorService = ES): Task[String] =
    F.bcryptWithRound(self, round)(es)

  def bcryptWithSalt(salt: String)(implicit F: BCrypt[F], es: ExecutorService = ES): Task[String] =
    F.bcryptWithSalt(self, salt)(es)

  def bcryptMatch(hashed: String)(implicit F: BCrypt[F], es: ExecutorService = ES): Task[Boolean] =
    F.bcryptMatch(hashed, self)(es)
}

trait ToBCryptOps {
  implicit def toBCryptOps[A: BCrypt](a: A): BCryptOps[A] = new BCryptOps[A](a)
}

object all extends BCrypts with ToBCryptOps
