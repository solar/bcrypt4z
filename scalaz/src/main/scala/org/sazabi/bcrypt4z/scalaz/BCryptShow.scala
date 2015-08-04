package org.sazabi.bcrypt4z
package scalaz

import _root_.scalaz.Show

trait BCryptShow {
  implicit def ShowToBCrypt[A: Show]: BCrypt[A] = new BCrypt[A] {
    def value(a: A) = Show[A].shows(a)
  }
}
