package com.github.apien.chesszio.engine.move

import com.github.apien.chesszio.engine.Square
import com.github.apien.chesszio.engine.move.Diagonal
import com.github.apien.chesszio.test.TestDataBuilder
import zio.Scope
import zio.test.*

object DiagonalSpec extends ZIOSpecDefault with TestDataBuilder {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("Diagonal")(
    topLeftSuite,
    downRight,
    topRightSuite,
    downLeftSuite
  )

  private def topLeftSuite = suite("topLeft")(
    test("determine diagonal cords") {
      assertTrue(Diagonal.topLeft(unsafeSquare(3, 4)) == List(unsafeSquare(2, 3), unsafeSquare(1, 2), unsafeSquare(0, 1)))
    },
    test("return empty list for the most top left unsafeSquare") {
      assertTrue(Diagonal.topLeft(unsafeSquare(0, 0)) == Nil)
    }
  )

  private def downRight = suite("downRight")(
    test("determine diagonal cords") {
      assertTrue(Diagonal.downRight(unsafeSquare(3, 4)) == List(unsafeSquare(4, 5), unsafeSquare(5, 6), unsafeSquare(6, 7)))
    },
    test("return empty list for the most down right square") {
      assertTrue(Diagonal.downRight(unsafeSquare(7, 7)) == Nil)
    }
  )
  private def topRightSuite = suite("topRight")(
    test("determine diagonal cords") {
      assertTrue(
        Diagonal.topRight(unsafeSquare(3, 4)) == List(
          unsafeSquare(4, 3),
          unsafeSquare(5, 2),
          unsafeSquare(6, 1),
          unsafeSquare(7, 0)
        )
      )
    },
    test("return empty list for the most top right square") {
      assertTrue(Diagonal.topRight(unsafeSquare(7, 0)) == Nil)
    }
  )
  private def downLeftSuite = suite("downLeft")(
    test("determine diagonal cords") {
      assertTrue(
        Diagonal.downLeft(unsafeSquare(3, 4)) == List(
          unsafeSquare(2, 5),
          unsafeSquare(1, 6),
          unsafeSquare(0, 7)
        )
      )
    },
    test("return empty list for the most down left square") {
      assertTrue(Diagonal.downLeft(unsafeSquare(0, 7)) == Nil)
    }
  )
}
