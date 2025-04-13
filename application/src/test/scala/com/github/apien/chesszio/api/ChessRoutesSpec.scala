package com.github.apien.chesszio.api

import com.github.apien.chesszio.MemoryPiecesRepository.{GamePieceKey, PieceSquareDb}
import com.github.apien.chesszio.api.ChessRoutesSpec.test
import com.github.apien.chesszio.api.model.{CreatePoundApiRequest, PieceWithSquare}
import com.github.apien.chesszio.engine.PieceType.{Bishop, Rook}
import com.github.apien.chesszio.engine.*
import com.github.apien.chesszio.{ActionId, ChessService, MemoryActionRepository, MemoryPiecesRepository}
import sttp.client3.testing.SttpBackendStub
import sttp.client3.ziojson.*
import sttp.client3.{SttpBackend, UriContext, basicRequest}
import sttp.model.StatusCode
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.tapir.ztapir.*
import zio.*
import zio.test.*

import java.util.UUID

object ChessRoutesSpec extends ZIOSpecDefault {
  override def spec = suite("ChessRoutes")(
    createSuite
  )

  def backendStub(endpoint: ZServerEndpoint[Any, Any]): SttpBackend[[_$1] =>> RIO[Any, _$1], Nothing] =
    TapirStubInterpreter(SttpBackendStub(new RIOMonadError[Any]))
      .whenServerEndpoint(endpoint)
      .thenRunLogic()
      .backend()

  given RIOMonadError[Any] = new RIOMonadError[Any]

  private val gameId1: GameId = "G1"

  val createPoundApiRequest: CreatePoundApiRequest = CreatePoundApiRequest(
    position = Square(Column.at0, Row.at0),
    kind = Bishop
  )

  val pieceWithSquare: PieceWithSquare = PieceWithSquare(
    piece = Piece(PieceId.fromString("P1"), Bishop, deleted = false),
    square = Square(Column.at0, Row.at0)
  )

  private def createSuite = suite("create")(
    test("create a new created piec with 200 status") {
      for {
        routes <- ZIO.service[ChessRoutes]
        route = routes.createPieceRoute
        response <- basicRequest
          .post(uri"http://test.com/api/games/G1/pieces")
          .body(createPoundApiRequest)
          .response(asJson[PieceWithSquare])
          .send(backendStub(route))
      } yield zio.test.assert(response.code)(Assertion.equalTo(StatusCode.Ok)) && zio.test.assert(response.body) {
        Assertion.equalTo(Right(pieceWithSquare))
      }
    }.provideShared(buildLayer(generatePieceId = () => "P1")),
    test("response 409 when a requested square is occupied already") {
      for {
        routes       <- ZIO.service[ChessRoutes]
        chessService <- ZIO.service[ChessService]
        _            <- chessService.addPiece(gameId1, Rook, Square(Column.at0, Row.at0))
        route = routes.createPieceRoute
        response <- basicRequest
          .post(uri"http://test.com/api/games/G1/pieces")
          .body(createPoundApiRequest)
          .response(asJson[PieceWithSquare])
          .send(backendStub(route))
      } yield zio.test.assert(response.code)(Assertion.equalTo(StatusCode.Conflict))
    }.provideShared(buildLayer()),
    test("response 400 when a row is out of range <0,7>") {
      val bodyWithInvalidRow =
        """
          |{
          |  "position": {
          |    "column": 3,
          |    "row": 20
          |  },
          |  "kind": "Bishop"
          |}
          |""".stripMargin
      for {
        routes       <- ZIO.service[ChessRoutes]
        chessService <- ZIO.service[ChessService]
        _            <- chessService.addPiece(gameId1, Rook, Square(Column.at0, Row.at0))
        route = routes.createPieceRoute
        response <- basicRequest
          .post(uri"http://test.com/api/games/G1/pieces")
          .body(bodyWithInvalidRow)
          .response(asJson[PieceWithSquare])
          .send(backendStub(route))
      } yield zio.test.assert(response.code)(Assertion.equalTo(StatusCode.BadRequest))
    }.provideShared(buildLayer()),
    test("response 400 when a column is out of range <0,7>") {
      val bodyWithInvalidRow =
        """
          |{
          |  "position": {
          |    "column": -1,
          |    "row": 4
          |  },
          |  "kind": "Bishop"
          |}
          |""".stripMargin
      for {
        routes       <- ZIO.service[ChessRoutes]
        chessService <- ZIO.service[ChessService]
        _            <- chessService.addPiece(gameId1, Rook, Square(Column.at0, Row.at0))
        route = routes.createPieceRoute
        response <- basicRequest
          .post(uri"http://test.com/api/games/G1/pieces")
          .body(bodyWithInvalidRow)
          .response(asJson[PieceWithSquare])
          .send(backendStub(route))
      } yield zio.test.assert(response.code)(Assertion.equalTo(StatusCode.BadRequest))
    }.provideShared(buildLayer())
  )

  private def buildLayer(
    generatePieceId: () => PieceId = UUID.randomUUID().toString,
    initialPieces: Map[GamePieceKey, PieceSquareDb] = Map()
  ): ZLayer[Any, Nothing, ChessService & MemoryPiecesRepository & ChessEndpoints & ChessRoutes] = {
    val repoLayer: ZLayer[Any, Nothing, MemoryPiecesRepository] = {
      ZLayer.fromZIO(Ref.make(initialPieces)) >>> ZLayer.fromFunction(new MemoryPiecesRepository(_))
    }
    val chessService =
      ZLayer.fromFunction(new ChessService(_, _, generatePieceId, () => ActionId.generate()))

    MemoryActionRepository.live >+> repoLayer >+> chessService >+> ChessEndpoints.live >+> ChessRoutes.live
  }

}
