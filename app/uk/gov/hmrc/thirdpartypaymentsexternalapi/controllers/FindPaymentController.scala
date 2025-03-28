/*
 * Copyright 2025 HM Revenue & Customs
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
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.thirdpartypaymentsexternalapi.helpers.ExternalTest
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartySoftwareFindByClientIdResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.services.FindPaymentService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class FindPaymentController @Inject() (cc: ControllerComponents, findPaymentService: FindPaymentService)(implicit executionContext: ExecutionContext)
  extends BackendController(cc) {

  def status(clientJourneyId: ClientJourneyId): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
      def getStatus(request: Request[AnyContent])(implicit hc: HeaderCarrier): Future[ThirdPartySoftwareFindByClientIdResponse] = {
        if (request.headers.get("Gov-Test-Scenario").isDefined) {
          ExternalTest.newPaymentJourney(clientJourneyId, request.headers("Gov-Test-Scenario"))
        } else findPaymentService.findJourneyByClientId(clientJourneyId)
      }

    getStatus(request).map {
      response => Ok(Json.toJson(response))
    }.recover {
      case e: UpstreamErrorResponse => e.statusCode match {
        case 404 => NotFound
        case _   => InternalServerError
      }
    }
  }

}
