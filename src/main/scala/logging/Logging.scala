package logging

import izumi.logstage.api.routing.StaticLogRouter
import logging.Logging.Service
import logstage.circe._
import logstage.{ConsoleSink, Info, IzLogger, Warn}
import zio.{IO, URIO}

trait Logging {
  def logging: Service[Any]
}

object Logging {

  trait Service[R] {
    def logger: URIO[R, IzLogger]
  }

  def createLogger = {
    val jsonSink = ConsoleSink.text()
    val logger = IzLogger(Info, jsonSink, Map(
      "org.http4s.blaze.channel.nio1.NIO1SocketServerGroup" -> Warn))
    StaticLogRouter.instance.setup(logger.router)
    logger
  }

  trait Live extends Logging {
    val logging = new Service[Any] {
      val logger = IO.succeed(createLogger)
    }
  }

}
