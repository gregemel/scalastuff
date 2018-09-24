package com.emelwerx.scalastuff

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.{Done, NotUsed}
import com.emelwerx.scalastuff.model.Temperature
import com.emelwerx.scalastuff.repository.TemperatureRepository
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

//use akka stream to read csv file and for each line, transform and save to a database
class StreamCsvToRepository(repository: TemperatureRepository)
                           (implicit system: ActorSystem) extends LazyLogging  {

//  lazy val repository: TemperatureRepository = new TemperatureRepository
  import system.dispatcher

  def importFromCsvFile(filePath: String): Future[Done] = {

    implicit val mat: ActorMaterializer = ActorMaterializer()
    implicit val fileName: String = filePath

    val concurrentFutures = 1

    val startTime = System.currentTimeMillis()

    println(s"starting import: $startTime")

    //akka streams flow from source to sink, via a transform step

    Source(source)
      .via(transform)
      .runWith(sinkToRepo(concurrentFutures))
      .andThen {
        case Success(_) =>
          val elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0
          println(s"Import finished in ${elapsedTime}s")
        case Failure(e) => println(s"Import failed: $e")
      }
  }

  private def source(implicit fileName: String) = scala.io.Source.fromFile(fileName).getLines().toList

  //transform source item into canonical type
  private def transform: Flow[String, Temperature, NotUsed] = {
    Flow.fromFunction[String, Temperature] (line => {
      println(s"transform line: $line")
      val fields = line.split(",")
      println(s"fields: ${fields(0)}, ${fields(1)}, ${fields(2)}")
      Temperature(fields(0), fields(1), fields(2).trim.toInt)
    })
  }

  //sink: destination is repository
  private def sinkToRepo(threadCount: Int)(implicit mat:Materializer,
                                           as:ActorSystem,
                                           ec:ExecutionContext): Sink[Temperature, Future[Done]] = {
    Flow[Temperature]
      .mapAsync(threadCount)(repository.save)
      .toMat(Sink.ignore)(Keep.right)
  }
}