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

import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{ThirdPartyPayRequest, ThirdPartyPayResponse, ThirdPartyResponseError, ThirdPartyResponseErrors}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.services.PayApiService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class StartPaymentController @Inject() (cc: ControllerComponents, payApiService: PayApiService)(implicit executionContext: ExecutionContext)
  extends BackendController(cc) {

  def pay(): Action[ThirdPartyPayRequest] = Action.async(parse.json[ThirdPartyPayRequest]) { implicit request: Request[ThirdPartyPayRequest] =>
    payApiService
      .startPaymentJourney(request.body)
      .map {
        case Left(error)        => thirdPartyResponseErrorToResult(error)
        case Right(spjResponse) => Created(Json.toJson(ThirdPartyPayResponse(spjResponse.nextUrl)))
      }
  }

  //this is used to hide the error returned from pay-api, which includes info about the url etc.
  private val thirdPartyResponseErrorToResult: ThirdPartyResponseError => Result = {
    case e @ ThirdPartyResponseErrors.UpstreamError => InternalServerError(Json.obj("error" -> e.errorMessage))
  }
}
