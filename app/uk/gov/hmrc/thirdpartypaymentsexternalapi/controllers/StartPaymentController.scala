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
    val jsonBody: Either[String, ThirdPartyPayRequest] =
      request.body
        .asJson
        .fold[Either[String, ThirdPartyPayRequest]](Left("Not a valid json body")) {
          _.validate[ThirdPartyPayRequest] match {
            case JsSuccess(value, _) => Right(value)
            case JsError(errors: collection.Seq[(JsPath, collection.Seq[JsonValidationError])]) =>
              errors.collectFirst {
                case j: (JsPath, collection.Seq[JsonValidationError]) =>
                  println("jspath: " + j._1.toString())
                  println("validationErrors: " + j._2.toString())
                  Left(j._2.headOption.map(_.message).getOrElse("Unexpected error occurred"))
              }.getOrElse(Left("Unexpected error occurred"))
          }
        }

    jsonBody match {
      case Left(value) => Future.successful(BadRequest(Json.obj("error" -> value)))
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
    case e @ ThirdPartyResponseErrors.UpstreamError                     => InternalServerError(createErrorResponse(e))
    case e @ ThirdPartyResponseErrors.CatchallError                     => InternalServerError(createErrorResponse(e))
    case e @ ThirdPartyResponseErrors.FriendlyNameTooLongError          => BadRequest(createErrorResponse(e))
    case e @ ThirdPartyResponseErrors.FriendlyNameInvalidCharacterError => BadRequest(createErrorResponse(e))
  }

  private val createErrorResponse: ThirdPartyResponseError => JsObject = e => Json.obj("error" -> e.errorMessage)

  def errorMessageToSOmethingUseful(errorCode: String): String = errorCode match {
    case "error.pattern" => "Contains invalid character"
    case _               => "some other error not implemented yet"
  }

}
