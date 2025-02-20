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

import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.OpenBankingStub

import java.util.UUID

class FindPaymentControllerSpec extends ItSpec {

  private val findPaymentController = app.injector.instanceOf[FindPaymentController]
  private val clientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/status")

  "GET /status" - {

    "return 200 OK when findPaymentService returns the payment" in {
      OpenBankingStub.stubForFindJourneyByClientId(clientJourneyId)

      val result = findPaymentController.status(clientJourneyId)(fakeRequest)

      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.parse(
        """{
          |  "clientJourneyId" : "aef0f31b-3c0f-454b-9d1f-07d549987a96",
          |  "paymentReference" : "paymentRef",
          |  "taxRegime" : "TaxRegime",
          |  "amountInPence" : 1234,
          |  "paymentJourneyStatus" : "InProgress"
          |}""".stripMargin
      )

    }

    "return 500 InternalServerError when OpenBanking returns 500" in {
      OpenBankingStub.stubForFindJourneyByClientId5xx()

      val result = findPaymentController.status(clientJourneyId)(fakeRequest)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR

    }

    "return 404 Not Found when OpenBanking returns 404" in {
      OpenBankingStub.stubForFindJourneyByClientId404()

      val result = findPaymentController.status(clientJourneyId)(fakeRequest)

      status(result) shouldBe Status.NOT_FOUND
    }

  }

}
