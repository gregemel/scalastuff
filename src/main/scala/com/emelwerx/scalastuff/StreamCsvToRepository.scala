package com.emelwerx.scalastuff

import akka.Done
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import com.emelwerx.scalastuff.model.Temperature
import com.emelwerx.scalastuff.repository.TemperatureRepository
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class StreamCsvToRepository(config: Config, readingRepository: TemperatureRepository)
                           (implicit system: ActorSystem) extends LazyLogging  {

  lazy val repository: TemperatureRepository = new TemperatureRepository
  import system.dispatcher

  def importFromCsvFile(fileName: String): Future[Done] = {
    implicit val mat: ActorMaterializer = ActorMaterializer()

    val concurrentFutures = 1

    val source: List[String] = scala.io.Source.fromFile(fileName).getLines().toList

    val startTime = System.currentTimeMillis()

    println(s"starting import: $startTime")

    //flow from source to sink

    Source(source)
      .mapAsync(concurrentFutures)(transformLineToTemperature)
      .runWith(sinkToRepo(concurrentFutures))
      .andThen {
        case Success(_) =>
          val elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0
          println(s"Import finished in ${elapsedTime}s")
        case Failure(e) => println(s"Import failed: $e")
      }
  }

  //transform source item into canonical type
  private def transformLineToTemperature(line: String)(implicit ec: ExecutionContext) = Future {
    println(s"transformed line: $line")
    val fields = line.split(",")
    println(s"fields: ${fields(0)}, ${fields(1)}, ${fields(2)}")
    Temperature(fields(0), fields(1), fields(2).trim.toInt)
  }

  //final step of flow: destination is repository
  private def sinkToRepo(threadCount: Int)(implicit mat:Materializer,
                                           as:ActorSystem,
                                           ec:ExecutionContext): Sink[Temperature, Future[Done]] = {
    Flow[Temperature]
      .mapAsync(threadCount)(repository.save)
      .toMat(Sink.ignore)(Keep.right)
  }
}