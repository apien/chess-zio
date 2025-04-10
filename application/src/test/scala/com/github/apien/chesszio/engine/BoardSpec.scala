package com.github.apien.chesszio.engine

import BoardSpec.suite
import PieceType.Rok
import zio.*
import zio.test.{assert as assertZIO, *}

import scala.runtime.stdLibPatches.Predef.assert

object BoardSpec extends ZIOSpecDefault {

  val board = new Board(
    Map(
      Square(Column.at0, Row.at3) -> Piece("P1", Rok, false),
      Square(Column.at1, Row.at3) -> Piece("P2", Rok, false),
      Square(Column.at3, Row.at3) -> Piece("P3", Rok, false),
      Square(Column.at4, Row.at3) -> Piece("P4", Rok, false),
      Square(Column.at6, Row.at3) -> Piece("P5", Rok, false),
      Square(Column.at7, Row.at3) -> Piece("P6", Rok, false),
//      Vertically
      Square(Column.at3, Row.at1) -> Piece("P7", Rok, false),
      Square(Column.at3, Row.at2) -> Piece("P8", Rok, false),
      Square(Column.at3, Row.at6) -> Piece("P9", Rok, false),
      Square(Column.at3, Row.at7) -> Piece("P10", Rok, false)
    )
  )

  def spec = suite("Board")(
    getPiecesHorizontallySuite,
    getPiecesVerticallySuite,
    moveSuite
  )

  def getPiecesHorizontallySuite = suite("getPiecesHorizontally")(
    test("get pieces at right side (beginning exclusive and end inclusive)") {

      val result = board.getPiecesHorizontally(Row.at3, Column.at3, Column.at6)
      assertZIO(result.map(_._1))(
        Assertion.hasSameElements(List(Square(Column.at4, Row.at3), Square(Column.at6, Row.at3)))
      )
    },
    test("get pieces at left side (beginning exclusive and end inclusive)") {

      val result = board.getPiecesHorizontally(Row.at3, Column.at3, Column.at1)
      assertZIO(result.map(_._1))(Assertion.hasSameElements(List(Square(Column.at1, Row.at3))))
    }
  )

  def getPiecesVerticallySuite = suite("getPiecesVertically")(
    test("get pieces at up side (beginning exclusive and end inclusive)") {

      val result = board.getPiecesVertically(Column.at3, Row.at3, Row.at1)
      assertZIO(result.map(_._1))(
        Assertion.hasSameElements(List(Square(Column.at3, Row.at1), Square(Column.at3, Row.at2)))
      )
    },
    test("get pieces at down side (beginning exclusive and end inclusive)") {

      val result = board.getPiecesVertically(Column.at3, Row.at3, Row.at7)
      assertZIO(result.map(_._1))(
        Assertion.hasSameElements(List(Square(Column.at3, Row.at6), Square(Column.at3, Row.at7)))
      )
    }
  )

  def moveSuite = suite("move")(
    test("move a piece on a new location") {
      val board = new Board(
        Map(
          Square(Column.at0, Row.at0) -> Piece("P1", Rok, false),
          Square(Column.at3, Row.at4) -> Piece("P2", Rok, false),
          Square(Column.at6, Row.at2) -> Piece("P3", Rok, false)
        )
      )
      val expectedBoard = Map(
        Square(Column.at7, Row.at6) -> Piece("P1", Rok, false),
        Square(Column.at3, Row.at4) -> Piece("P2", Rok, false),
        Square(Column.at6, Row.at2) -> Piece("P3", Rok, false)
      )

      val boardAfterMove = board.move(Square(Column.at0, Row.at0), Square(Column.at7, Row.at6)).getBoardState

      assertTrue(boardAfterMove == expectedBoard)
    }
  )
}
