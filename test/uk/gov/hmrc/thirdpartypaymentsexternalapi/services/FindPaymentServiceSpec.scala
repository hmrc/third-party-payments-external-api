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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartySoftwareFindByClientIdResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.OpenBankingStub

import java.util.UUID

class FindPaymentServiceSpec extends ItSpec {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val findPaymentService: FindPaymentService = app.injector.instanceOf[FindPaymentService]

  "FindPaymentService" - {
    val clientId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))

    "findJourneyByClientId" - {
      "should return a ThirdPartySoftwareFindByClientIdResponse given open-banking call succeeds with taxRegime in PascalCase" in {
        //TaxRegime is converted to PascalCase
        val expectedResponse = ThirdPartySoftwareFindByClientIdResponse(clientId, "paymentRef", "TaxRegime", 1234, "InProgress")
        OpenBankingStub.stubForFindJourneyByClientId(clientId)

        val result = findPaymentService.findJourneyByClientId(clientId)

        result.futureValue shouldBe expectedResponse
      }

      "should propagate a UpstreamError when open-banking returns a 5xx" in {
        OpenBankingStub.stubForFindJourneyByClientId5xx()

        val error = intercept[Exception](findPaymentService.findJourneyByClientId(clientId).futureValue)

        error.getCause.getMessage should include(s"GET of 'http://localhost:${wireMockPort.toString}/open-banking/payment/search/third-party-software/${clientId.value.toString}' returned 503.")
      }
    }

  }
}
