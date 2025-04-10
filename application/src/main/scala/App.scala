import com.github.apien.chesszio.{ChessService, MemoryPiecesRepository}
import com.github.apien.chesszio.api.{ChessEndpoints, ChessRoutes}
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zio.http.Server
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object App extends ZIOAppDefault {
  override def run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] = {
    val port = 8080
    (for {
      _         <- ZIO.log("hello world")
      endpoints <- ZIO.service[Endpoints]
      httpApp = ZioHttpInterpreter(ZioHttpServerOptions.default).toHttp(endpoints.endpoints)
      actualPort <- Server.install(httpApp)
      _          <- Console.printLine(s"Go to http://localhost:$actualPort/docs to open SwaggerUI")
      _          <- ZIO.never
    } yield ())
      .provide(
        ChessEndpoints.live,
        ChessRoutes.live,
        Endpoints.live,
        MemoryPiecesRepository.live,
        ChessService.live,
        Server.defaultWithPort(port)
      )
      .exitCode
  }
}
