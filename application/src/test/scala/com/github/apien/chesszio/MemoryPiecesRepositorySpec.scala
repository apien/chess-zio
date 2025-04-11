package com.github.apien.chesszio

import com.github.apien.chesszio.ChessService.{PieceDoesNotExist, SquareOccupied}
import com.github.apien.chesszio.MemoryPiecesRepository.{GamePieceKey, PieceSquareDb}
import com.github.apien.chesszio.MemoryPiecesRepositorySpec.test
import com.github.apien.chesszio.engine.*
import com.github.apien.chesszio.engine.PieceType.{Bishop, Rok}
import com.github.apien.chesszio.test.TestDataBuilder
import zio.*
import zio.test.Assertion.{equalTo, fails}
import zio.test.{assertTrue, *}

object MemoryPiecesRepositorySpec extends ZIOSpecDefault with TestDataBuilder {

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("MemoryPiecesRepository")(
    addPieceSuite,
    removePieceSuite,
    storeGameStateSuite
  )

  val layer: ZLayer[Any, Nothing, MemoryPiecesRepository] =
    ZLayer.fromZIO(Ref.make(Map.empty[GamePieceKey, PieceSquareDb])) >>> ZLayer.fromFunction(
      new MemoryPiecesRepository(_)
    )

  private val gameId1: GameId   = "G1"
  private val gameId2: GameId   = "G2"
  private val pieceId1: PieceId = "P1"
  private val pieceId2: PieceId = "P2"
  private val pieceId3: PieceId = "P3"

  private def addPieceSuite = suite("addPiece")(
    test("add a piece") {
      val expectedMap = Map(
        GamePieceKey(gameId1, pieceId1) -> PieceSquareDb(
          gameId1,
          pieceId1,
          Bishop,
          deleted = false,
          Square(Column.at0, Row.at0)
        )
      )

      for {
        repository       <- ZIO.service[MemoryPiecesRepository]
        _                <- repository.addPiece(gameId1, pieceId1, Bishop, Square(Column.at0, Row.at0))
        stateAfterAction <- repository.getAll
      } yield zio.test.assertTrue(stateAfterAction == expectedMap)
    }.provideShared(layer),
    test("fail Swith quareOccupied when given square is not free ") {
      val expectedMap = Map(
        GamePieceKey(gameId1, pieceId1) -> PieceSquareDb(
          gameId1,
          pieceId1,
          Bishop,
          deleted = false,
          Square(Column.at0, Row.at0)
        )
      )

      for {
        repository       <- ZIO.service[MemoryPiecesRepository]
        _                <- repository.addPiece(gameId1, pieceId1, Bishop, Square(Column.at0, Row.at0))
        operation2       <- repository.addPiece(gameId1, pieceId2, Bishop, Square(Column.at0, Row.at0)).exit
        stateAfterAction <- repository.getAll
      } yield assert(operation2)(fails(equalTo(SquareOccupied))) && zio.test.assertTrue(stateAfterAction == expectedMap)
    }.provideShared(layer)
  )

  private def removePieceSuite = suite("removePiece")(
    test("mark an existing piece as removed") {
      val expectedMap = Map(
        GamePieceKey(gameId1, pieceId1) -> PieceSquareDb(
          gameId1,
          pieceId1,
          Bishop,
          deleted = false,
          Square(Column.at0, Row.at0)
        ),
        GamePieceKey(gameId1, pieceId2) -> PieceSquareDb(
          gameId1,
          pieceId2,
          Rok,
          deleted = true,
          Square(Column.at1, Row.at1)
        )
      )

      for {
        repository       <- ZIO.service[MemoryPiecesRepository]
        _                <- repository.addPiece(gameId1, pieceId1, Bishop, Square(Column.at0, Row.at0))
        _                <- repository.addPiece(gameId1, pieceId2, Rok, Square(Column.at1, Row.at1))
        _                <- repository.removePiece(gameId1, pieceId2)
        stateAfterAction <- repository.getAll
      } yield zio.test.assertTrue(stateAfterAction == expectedMap)
    }.provideShared(layer),
    test("success when a pieces is deleted already") {
      val expectedMap = Map(
        GamePieceKey(gameId1, pieceId1) -> PieceSquareDb(
          gameId1,
          pieceId1,
          Bishop,
          deleted = true,
          Square(Column.at0, Row.at0)
        )
      )

      for {
        repository       <- ZIO.service[MemoryPiecesRepository]
        _                <- repository.addPiece(gameId1, pieceId1, Bishop, Square(Column.at0, Row.at0))
        _                <- repository.removePiece(gameId1, pieceId1)
        _                <- repository.removePiece(gameId1, pieceId1)
        stateAfterAction <- repository.getAll
      } yield zio.test.assertTrue(stateAfterAction == expectedMap)
    }.provideShared(layer),
    test("fail when a piece in given game does not exist") {
      for {
        repository       <- ZIO.service[MemoryPiecesRepository]
        removeResult     <- repository.removePiece(gameId1, pieceId1).exit
        stateAfterAction <- repository.getAll
      } yield assertTrue(stateAfterAction.isEmpty) && assert(removeResult)(fails(equalTo(PieceDoesNotExist)))
    }.provideShared(layer)
  )

  private def storeGameStateSuite = suite("storeGameState")(
    test("store a state and do not overwrite deleted pieces") {
      val newState = Map(
        Square(Column.at6, Row.at6) -> Piece(pieceId2, Rok, deleted = false),
        Square(Column.at7, Row.at7) -> Piece(pieceId3, Bishop, deleted = false)
      )

      val expectedMap = Map(
        GamePieceKey(gameId1, pieceId1) -> PieceSquareDb(
          gameId1,
          pieceId1,
          Bishop,
          deleted = true,
          Square(Column.at0, Row.at0)
        ),
        GamePieceKey(gameId1, pieceId2) -> PieceSquareDb(
          gameId1,
          pieceId2,
          Rok,
          deleted = false,
          Square(Column.at6, Row.at6)
        ),
        GamePieceKey(gameId1, pieceId3) -> PieceSquareDb(
          gameId1,
          pieceId3,
          Bishop,
          deleted = false,
          Square(Column.at7, Row.at7)
        )
      )

      for {
        repository       <- ZIO.service[MemoryPiecesRepository]
        _                <- repository.addPiece(gameId1, pieceId1, Bishop, Square(Column.at0, Row.at0))
        _                <- repository.removePiece(gameId1, pieceId1)
        _                <- repository.addPiece(gameId1, pieceId2, Rok, Square(Column.at1, Row.at1))
        _                <- repository.addPiece(gameId1, pieceId3, Bishop, Square(Column.at1, Row.at2))
        _                <- repository.storeGameState(gameId1, newState)
        stateAfterAction <- repository.getAll
      } yield zio.test.assertTrue(stateAfterAction == expectedMap)
    }.provideShared(layer),
    test("do not modify state of other games") {
      val newState = Map(
        Square(Column.at6, Row.at6) -> Piece(pieceId3, Bishop, deleted = false)
      )

      val expectedMap = Map(
        GamePieceKey(gameId1, pieceId1) -> PieceSquareDb(
          gameId1,
          pieceId1,
          Bishop,
          deleted = false,
          Square(Column.at0, Row.at0)
        ),
        GamePieceKey(gameId1, pieceId2) -> PieceSquareDb(
          gameId1,
          pieceId2,
          Bishop,
          deleted = true,
          Square(Column.at0, Row.at1)
        ),
        GamePieceKey(gameId2, pieceId3) -> PieceSquareDb(
          gameId2,
          pieceId3,
          Bishop,
          deleted = false,
          Square(Column.at6, Row.at6)
        )
      )

      for {
        repository       <- ZIO.service[MemoryPiecesRepository]
        _                <- repository.addPiece(gameId1, pieceId1, Bishop, Square(Column.at0, Row.at0))
        _                <- repository.addPiece(gameId1, pieceId2, Bishop, Square(Column.at0, Row.at1))
        _                <- repository.removePiece(gameId1, pieceId2)
        _                <- repository.storeGameState(gameId2, newState)
        stateAfterAction <- repository.getAll
      } yield zio.test.assertTrue(stateAfterAction == expectedMap)
    }.provideShared(layer)
  )

}
