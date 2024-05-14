// package smithy4sdemo

// import cats.effect.IO
// import cats.effect.IOApp
// import cats.effect.Resource
// import hello.Question
// import hello.GetQuestionOutput
// import hello.SurveyService
// import org.http4s.HttpRoutes
// import org.http4s.ember.server.EmberServerBuilder
// import smithy4s.http4s.SimpleRestJsonBuilder

// object Main extends IOApp.Simple {

//   val impl: SurveyService[IO] =
//     new SurveyService[IO] {

//       def getQuestion: IO[Question] =
//         IO.println("getQuestion") *>
//         IO.pure(Question("How are you doing?"))

//     }

//   def run: IO[Unit] =
//     SimpleRestJsonBuilder
//       .routes(impl)
//       .resource
//       .flatMap { routes =>
//         EmberServerBuilder
//           .default[IO]
//           .withHttpApp(routes.orNotFound)
//           .build
//       }
//       .evalMap(srv => IO.println(srv.addressIp4s))
//       .useForever

// }




package smithy4sdemo

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import hello.GetQuestionOutput
import hello.SurveyService
import org.http4s.HttpRoutes
import org.http4s.ember.server.EmberServerBuilder
import smithy4s.http4s.SimpleRestJsonBuilder

object Main extends IOApp.Simple {

  val impl: SurveyService[IO] =
    new SurveyService[IO] {

      def getQuestion(
        questionId: Integer
      ): IO[GetQuestionOutput] = 
        IO.pure(GetQuestionOutput("How are you doing?"))

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