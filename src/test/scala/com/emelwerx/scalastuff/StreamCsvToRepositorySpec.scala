package com.emelwerx.scalastuff

import akka.Done
import akka.actor.ActorSystem
import com.emelwerx.scalastuff.model.Temperature
import com.emelwerx.scalastuff.repository.TemperatureRepository
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, Outcome, fixture}

import scala.concurrent.{ExecutionContext, Future}

class StreamCsvToRepositorySpec extends fixture.FlatSpec
  with Matchers
  with MockitoSugar
  with ScalaFutures
  with IntegrationPatience {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  class FixtureParam {
    val readingRepository: TemperatureRepository = mock[TemperatureRepository]
    val target: StreamCsvToRepository = new StreamCsvToRepository(readingRepository)
  }

  it should "call repo.save for every row in the source file" in { fixture =>

    val numberOfRows = 2

    when(fixture.readingRepository.save(any[Temperature])(any[ExecutionContext])).thenReturn(Future{})

    whenReady(fixture.target.importFromCsvFile("datafile.txt")) {response =>
      response shouldBe Done
    }

    verify(fixture.readingRepository, times(numberOfRows)).save(any())(any())
  }


  implicit val system: ActorSystem = ActorSystem("scalatest")

  override protected def withFixture(test: OneArgTest): Outcome = test(new FixtureParam)

}
