package smithy4sdemo

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import hello.QuestionType
import hello.GetQuestionOutput
import hello.SurveyService
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import smithy4s.http4s.SimpleRestJsonBuilder

object Main extends IOApp.Simple {

  val impl: SurveyService[IO] =
    new SurveyService[IO] {

      def getQuestion(
        questionType: QuestionType
      ): IO[GetQuestionOutput] =
        IO.println(s"getQuestion($questionType)") *>
          IO.pure(GetQuestionOutput("Are you looking for a new job?"))

    }

  def run: IO[Unit] =
    SimpleRestJsonBuilder
      .routes(impl)
      .resource
      .flatMap { routes =>
        EmberServerBuilder
          .default[IO]
          .withHttpApp(routes.orNotFound)
          .build
      }
      .evalMap(srv => IO.println(srv.addressIp4s))
      .useForever

}
