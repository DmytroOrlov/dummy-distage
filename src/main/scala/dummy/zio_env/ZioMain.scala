package dummy.zio_env

import buildinfo.BuildInfo.version
import capture.Capture
import conf.AppError._
import conf._
import logging._
import org.http4s._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import tapir._
import tapir.server.http4s.Http4sServerOptions
import zio._
import zio.clock.Clock
import zio.delegate._
import zio.interop.catz._

object ZioMain extends App {
  type AppEnvironment = Logging with Clock
  type Effect[A] = RIO[AppEnvironment, A]
  type RouteF[F[_]] = HttpRoutes[F]
  type Route = RouteF[Effect]

  def service[A](req: A): ZIO[Logging, Nothing, HealthCheckResponse] = for {
    logger <- logger
    _ <- UIO(logger.info(s"$req"))
  } yield HealthCheckResponse(version)

  val server = for {
    logger <- logger
    appEnv <- appEnv
    _ <- UIO(logger.info(s"Starting $appEnv"))
    route = toZioRoutes(Endpoints.healthCheck, service): Route
    _ <- ZIO.runtime[AppEnvironment].flatMap { implicit rts =>
      BlazeServerBuilder[Effect]
        .bindHttp(host = "0.0.0.0")
        .withHttpApp(route.orNotFound)
        .serve
        .compile
        .drain
    }.mapError(serverBuilderErr)
  } yield ()

  def run(args: List[String]): ZIO[Clock, Nothing, Int] =
    server.foldM(e => {
      val error = e.continue(`AppError[String]`)
      logger.map(_.error(s"Application failed to start $error"))
        .as(1)
    }, _ => IO.succeed(0))
      .provideSome(new BaseEnv(_) with AppLive)

  class BaseEnv(@delegate base: Clock)

  trait AppLive extends HasAppConf.Live with Logging.Live

  def toZioRoutes[R, I, E, O](e: Endpoint[I, E, O, EntityBody[RIO[R, ?]]], logic: I => ZIO[R, E, O])(implicit serverOptions: Http4sServerOptions[RIO[R, ?]]): RouteF[RIO[R, ?]] = {
    import tapir.server.http4s._
    e.toRoutes(i => logic(i).either)
  }
}

import io.circe.generic.auto._
import tapir._
import tapir.json.circe._

object Endpoints {
  val healthCheck =
    endpoint.get
      .in("health-check")
      .out(jsonBody[HealthCheckResponse])
}

case class HealthCheckResponse(version: String)
