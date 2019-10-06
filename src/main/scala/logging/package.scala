import logging.Logging.Service
import logstage.IzLogger
import zio.{URIO, ZIO}

package object logging extends Service[Logging] {
  val logger: URIO[Logging, IzLogger] = ZIO.accessM(_.logging.logger)
}
