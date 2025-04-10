package com.github.apien.chesszio.engine.move

import com.github.apien.chesszio.engine.PieceType.Rok
import com.github.apien.chesszio.engine.move.RookMoveValidatorSpec.test
import com.github.apien.chesszio.engine.move.{MoveError, RookMoveValidator}
import com.github.apien.chesszio.engine.{Board, Piece, Square}
import com.github.apien.chesszio.test.TestDataBuilder
import zio.test.*

object RookMoveValidatorSpec extends ZIOSpecDefault with TestDataBuilder {
  private val board = new Board(
    Map(
      unsafeSquare(3, 1) -> Piece("P1", Rok, false),
      unsafeSquare(3, 3) -> Piece("P1", Rok, false),
      unsafeSquare(3, 6) -> Piece("P1", Rok, false),
      unsafeSquare(2, 3) -> Piece("P1", Rok, false),
      unsafeSquare(6, 3) -> Piece("P1", Rok, false),
      unsafeSquare(0, 3) -> Piece("P1", Rok, false),
      unsafeSquare(0, 7) -> Piece("P1", Rok, false)
    )
  )

  private val validator = new RookMoveValidator

  def spec = suite("RookMoveValidator")(
    validateSpec
  )

  def validateSpec = suite("validate")(
    test(
      "return DestinationSquareOccupied move when one of the field is occupied at attempt to move into a right direction"
    ) {
      val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(6, 3), board)
      assertTrue(result == Left(MoveError.DestinationSquareOccupied))
    },
    test(
      "return IllegalMove at attempt to move on diagonal"
    ) {
      val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(4, 2), board)
      assertTrue(result == Left(MoveError.IllegalMove))
    },
    suite("move horizontally")(
      suite("into right direction")(
        test("allow to move horizontally to the right") {
          val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(5, 3), board)
          assertTrue(result == Right(()))
        },
        test("return IllegalMove move when one of the field is occupied at attempt to move into a right direction") {
          val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(7, 3), board)
          assertTrue(result == Left(MoveError.IllegalMove))
        }
      ),
      suite("into left direction")(
        test("allow to move horizontally to the left") {
          val result = validator.validate(unsafeSquare(3, 6), unsafeSquare(0, 6), board)
          assertTrue(result == Right(()))
        },
        test("return IllegalMove move when one of the field is occupied at attempt to move into a left direction") {
          val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(1, 3), board)
          assertTrue(result == Left(MoveError.IllegalMove))
        }
      )
    ),
    suite("move vertically")(
      suite("into up direction")(
        test("allow to move vertically to the up") {
          val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(3, 2), board)
          assertTrue(result == Right(()))
        },
        test("return IllegalMove move when one of the field is occupied at attempt to move into a right direction") {
          val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(3, 0), board)
          assertTrue(result == Left(MoveError.IllegalMove))
        }
      ),
      suite("into down direction")(
        test("allow to move horizontally to the down") {
          val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(3, 5), board)
          assertTrue(result == Right(()))
        },
        test("return IllegalMove move when one of the field is occupied at attempt to move into a left direction") {
          val result = validator.validate(unsafeSquare(3, 3), unsafeSquare(3, 7), board)
          assertTrue(result == Left(MoveError.IllegalMove))
        }
      )
    )
  )

}
