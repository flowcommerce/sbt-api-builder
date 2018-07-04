package apibuilder.sbt

import java.net.URL

import gigahorse.support.okhttp.Gigahorse
import sbt.util.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scala.util.control.NonFatal

final class ApiBuilderClient(log: Logger, baseURL: URL, basicAuth: String) {
  def retrieveAll(apiBuilderRequests: Seq[ApiBuilderRequest]): Future[Seq[ApiBuilderResponse]] = {
    val client = Gigahorse.http(Gigahorse.config)

    val futureResult = Try {
      Future.traverse(apiBuilderRequests) {
        case ApiBuilderRequest(path, matchers, target) =>
          val Valid = new CodeValidator(log, matchers)
          client.run {
            val request = Gigahorse.url(s"$baseURL/$path").addHeaders("Authorization" -> basicAuth)
            log.debug(s"sending $request")
            request
          }
          .collect {
            case Valid(lastModified, codeFiles) =>
              codeFiles.map { cf =>
                val filePath = cf.dir.map(_.resolve(cf.name)).getOrElse(cf.name)
                ApiBuilderResponse(lastModified, target, filePath, cf.contents)
              }
          }
      }.map(_.flatten)
    }.recover {
      case NonFatal(e) => Future.failed(e)
    }.get

    futureResult.onComplete { _ =>
      client.close()
    }

    futureResult
  }
}
