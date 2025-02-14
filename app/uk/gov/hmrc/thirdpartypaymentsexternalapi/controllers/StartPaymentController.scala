/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.thirdpartypaymentsexternalapi.controllers
import play.api.libs.json._
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{ThirdPartyPayRequest, ThirdPartyResponseError, ThirdPartyResponseErrors}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.services.PayApiService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class StartPaymentController @Inject() (cc: ControllerComponents, payApiService: PayApiService)(implicit executionContext: ExecutionContext)
  extends BackendController(cc) {

  def pay(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val jsonBody: Either[ThirdPartyResponseError, ThirdPartyPayRequest] =
      request.body.asJson
        .fold[Either[ThirdPartyResponseError, ThirdPartyPayRequest]](Left(ThirdPartyResponseErrors.NonJsonBodyError)) {
          _.validate[ThirdPartyPayRequest] match {
            case JsSuccess(value, _) => Right(value)
            case e @ JsError(_: collection.Seq[(JsPath, collection.Seq[JsonValidationError])]) =>
              Left(
                jsErrorToMessages(e)
                  .headOption
                  .fold[ThirdPartyResponseError](ThirdPartyResponseErrors.UnexpectedError)(errorMessageKeyToThirdPartyResponseErrors)
              )
          }
        }

    jsonBody match {
      case Left(value) => Future.successful(thirdPartyResponseErrorToResult(value))
      case Right(value) =>
        payApiService
          .startPaymentJourney(value)
          .map {
            case Left(error)                  => thirdPartyResponseErrorToResult(error)
            case Right(thirdPartyPayResponse) => Created(Json.toJson(thirdPartyPayResponse))
          }
    }
  }

  //this is used to hide the error returned from pay-api, which includes info about the url etc.
  private val thirdPartyResponseErrorToResult: ThirdPartyResponseError => Result = {
    case e @ ThirdPartyResponseErrors.UnexpectedError                   => InternalServerError(createErrorResponse(e))
    case e @ ThirdPartyResponseErrors.UpstreamError                     => InternalServerError(createErrorResponse(e))
    case e @ ThirdPartyResponseErrors.NonJsonBodyError                  => BadRequest(createErrorResponse(e))
    case e @ ThirdPartyResponseErrors.FriendlyNameTooLongError          => BadRequest(createErrorResponse(e))
    case e @ ThirdPartyResponseErrors.FriendlyNameInvalidCharacterError => BadRequest(createErrorResponse(e))
  }

  private val createErrorResponse: ThirdPartyResponseError => JsObject = e => Json.obj("error" -> e.errorMessage)

  private val errorMessageKeyToThirdPartyResponseErrors: String => ThirdPartyResponseError = {
    case "friendlyName.error.invalidCharacters" => ThirdPartyResponseErrors.FriendlyNameInvalidCharacterError
    case "friendlyName.error.maxLength"         => ThirdPartyResponseErrors.FriendlyNameTooLongError
    case _                                      => ThirdPartyResponseErrors.UnexpectedError
  }

  private def jsErrorToMessages(jsError: JsError): Seq[String] = jsError.errors.flatMap(_._2).flatMap(_.messages).toSeq

}
