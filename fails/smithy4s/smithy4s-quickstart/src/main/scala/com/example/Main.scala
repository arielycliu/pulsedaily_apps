package com.example

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import smithy4s.http4s.SimpleRestJsonBuilder

object Main extends IOApp.Simple {
    def run: IO[Unit] = 
        EmberServerBuilder
            .default[IO]
            .build
            .evalMap(srv => IO.println(srv.addressIp4s))
            .useForever
}