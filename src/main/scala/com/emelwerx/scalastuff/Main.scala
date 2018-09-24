package com.emelwerx.scalastuff

import akka.actor.ActorSystem
import com.emelwerx.scalastuff.repository.TemperatureRepository
import com.typesafe.config.ConfigFactory

object Main extends App {

  println(s"main app starting...")
  val filename = "datafile.txt"

  implicit val system: ActorSystem = ActorSystem("scalastuff")

  private val config = ConfigFactory.load()

  println(s"found config: $config")

  private val readingRepository = new TemperatureRepository

  import system.dispatcher

  new StreamCsvToRepository(readingRepository).importFromCsvFile(filename)
    .onComplete { _ =>
      readingRepository.shutdown()
      system.terminate()
    }
}
