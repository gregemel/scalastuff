package com.emelwerx.scalastuff.repository

import com.emelwerx.scalastuff.model.Temperature

import scala.concurrent.{ExecutionContext, Future}

class TemperatureRepository {

  def save(item: Temperature)(implicit ex:ExecutionContext): Future[Unit] = {
    println(s"TemperatureRepository.save($item)")
    Future.successful(Unit)
  }

  def shutdown(): Unit = {
    println("shutting down...")
  }
}