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
import uk.gov.hmrc.thirdpartypaymentsexternalapi.services.{AuditService, PayApiService, ValidationService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class StartPaymentController @Inject() (auditService: AuditService, cc: ControllerComponents, payApiService: PayApiService, validationService: ValidationService)(implicit executionContext: ExecutionContext)
  extends BackendController(cc) {

  def pay(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val requestOrErrors: Either[Seq[ThirdPartyResponseError], ThirdPartyPayRequest] =
      request.body.asJson
        .fold[Either[Seq[ThirdPartyResponseError], ThirdPartyPayRequest]](
          ifEmpty = Left(Seq(ThirdPartyResponseErrors.NonJsonBodyError))
        )(
            f = validationService.validateThirdPartyRequest
          )

    requestOrErrors match {
      case Left(value) =>
        auditService.auditInitiateJourneyResult(isSuccessful         = false, maybeErrors = Some(value.map(_.errorMessage)), rawJson = request.body.asJson, maybeClientJourneyId = None)
        Future.successful(thirdPartyResponseErrorToResult(value))
      case Right(value) =>
        payApiService
          .startPaymentJourney(value)
          .map {
            case Left(error) =>
              auditService.auditInitiateJourneyResult(isSuccessful         = false, maybeErrors = Some(Seq(error.errorMessage)), rawJson = request.body.asJson, maybeClientJourneyId = None)
              thirdPartyResponseErrorToResult(Seq(error))
            case Right(thirdPartyPayResponse) =>
              auditService.auditInitiateJourneyResult(isSuccessful         = true, maybeErrors = None, rawJson = request.body.asJson, maybeClientJourneyId = Some(thirdPartyPayResponse.clientJourneyId))
              Created(Json.toJson(thirdPartyPayResponse))
          }
    }
  }

  //Just return an Internal server error if any of the errors are related to that, else BadRequest with list of errors
  def thirdPartyResponseErrorToResult(errors: Seq[ThirdPartyResponseError]): Result = {
    val maybeInternalServerError: Seq[ThirdPartyResponseError] = errors.collect {
      case e @ ThirdPartyResponseErrors.UnexpectedError(_) => e
      case e @ ThirdPartyResponseErrors.UpstreamError      => e
    }
    if (maybeInternalServerError.nonEmpty)
      InternalServerError(createErrorResponses(errors))
    else
      BadRequest(createErrorResponses(errors))
  }

  private def createErrorResponses: Seq[ThirdPartyResponseError] => JsObject = e => Json.obj("errors" -> Json.toJson(e.map(_.errorMessage)))

}
