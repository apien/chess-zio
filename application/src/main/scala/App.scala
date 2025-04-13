import com.github.apien.chesszio._
import com.github.apien.chesszio.api.{ChessEndpoints, ChessRoutes}
import com.github.apien.chesszio.config.ApplicationConfiguration
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zio.http.Server
import zio.{Console, Scope, ZIO, ZIOAppArgs, ZIOAppDefault, ZLayer}

object App extends ZIOAppDefault {
  private val port = 8080

  override def run: ZIO[ZIOAppArgs & Scope, Any, Any] = {
    (for {
      _         <- ZIO.log("Chess-zio start of bootstrap")
      endpoints <- ZIO.service[Endpoints]
      httpApp = ZioHttpInterpreter(ZioHttpServerOptions.default).toHttp(endpoints.endpoints)
      actualPort <- Server.install(httpApp)
      _          <- Console.printLine(s"Go to http://localhost:$actualPort/docs to open SwaggerUI")
      worker     <- ZIO.service[ActionPublisherWorker]
      _          <- worker.run().runDrain.fork
      _          <- ZIO.log("Chess-zio start of is over. Application is ready.")
      _          <- ZIO.never
    } yield ())
      .provide(
        ZLayer.fromZIO(ZIO.attempt(ApplicationConfiguration.loadOrThrow())),
        MemoryPiecesRepository.live,
        MemoryActionRepository.live,
        ChessService.live,
        ChessEndpoints.live,
        ChessRoutes.live,
        Endpoints.live,
        KafkaActionPublisher.live,
        ActionPublisherWorker.live,
        Server.defaultWithPort(port)
      )
      .exitCode
  }
}
