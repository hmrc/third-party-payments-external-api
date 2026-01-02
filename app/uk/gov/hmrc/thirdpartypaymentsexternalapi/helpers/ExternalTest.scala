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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.helpers

import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartySoftwareFindByClientIdResponse

import scala.concurrent.Future

object ExternalTest {

  def newPaymentJourney(
    clientJourneyId: ClientJourneyId,
    header:          String
  ): Future[ThirdPartySoftwareFindByClientIdResponse] = {
    def expectedResponse(paymentStatus: String) =
      ThirdPartySoftwareFindByClientIdResponse(
        clientJourneyId = clientJourneyId,
        taxRegime = "taxRegime",
        amountInPence = 123456L,
        paymentStatus = paymentStatus
      )

    header match {
      case "IN_PROGRESS"    => Future.successful(expectedResponse("InProgress"))
      case "COMPLETED"      => Future.successful(expectedResponse("Completed"))
      case "FAILED"         => Future.successful(expectedResponse("Failed"))
      case "UPSTREAM_ERROR" => Future.failed(UpstreamErrorResponse("upstream error: could be anything", 500))
      case _                => Future.failed(new Exception("Invalid header"))
    }

  }

}
