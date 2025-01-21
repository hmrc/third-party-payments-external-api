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
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.mvc.Http.Status
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime.{CorporationTax, EmployersPayAsYouEarn, SelfAssessment, Vat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{ThirdPartyPayRequest, ThirdPartyPayResponse}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{ClientJourneyId, TaxRegime}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.PayApiStub

import java.time.LocalDate
import java.util.UUID

class StartPaymentControllerSpec extends ItSpec {

  private val startPaymentController = app.injector.instanceOf[StartPaymentController]

  private def testThirdPartyRequest(taxRegime: TaxRegime): ThirdPartyPayRequest = ThirdPartyPayRequest(
    taxRegime     = taxRegime,
    reference     = "1234567895",
    amountInPence = 123,
    backURL       = "https://www.someBackUrl.com",
    dueDate       = Some(LocalDate.of(2025, 1, 31))
  )

  private val clientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))
  private val expectedTestThirdPartyPayResponse = ThirdPartyPayResponse(clientJourneyId, "https://somenext-url.co.uk")

  private def fakeRequest(taxRegime: TaxRegime): FakeRequest[ThirdPartyPayRequest] =
    FakeRequest("POST", "/pay").withBody[ThirdPartyPayRequest](testThirdPartyRequest(taxRegime))

  "POST /pay" - {

    "return 201 Created when pay-api returns SpjResponse" - {

      "for Self Assessment" in {
        PayApiStub.stubForStartJourneySelfAssessment()
        val result = startPaymentController.pay()(fakeRequest(SelfAssessment))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        PayApiStub.verifyStartJourneySelfAssessment(count = 1)
      }

      "for Vat" in {
        PayApiStub.stubForStartJourneyVat()
        val result = startPaymentController.pay()(fakeRequest(Vat))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        PayApiStub.verifyStartJourneyVat(count = 1)
      }

      "for Corporation Tax" in {
        PayApiStub.stubForStartJourneyCorporationTax()
        val result = startPaymentController.pay()(fakeRequest(CorporationTax))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        PayApiStub.verifyStartJourneyCorporationTax(count = 1)
      }

      "for Employers Pay As You Earn" in {
        PayApiStub.stubForStartJourneyEmployersPayAsYouEarn()
        val result = startPaymentController.pay()(fakeRequest(EmployersPayAsYouEarn))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        PayApiStub.verifyStartJourneyEmployersPayAsYouEarn(count = 1)
      }
    }

    "return an error when pay-api returns an error" - {

      "for Self Assessment" in {
        PayApiStub.stubForStartJourneySelfAssessment5xx()
        val result = startPaymentController.pay()(fakeRequest(SelfAssessment))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"error":"Error from upstream"}""")
        PayApiStub.verifyStartJourneySelfAssessment(count = 1)
      }

      "for Vat" in {
        PayApiStub.stubForStartJourneyVat5xx()
        val result = startPaymentController.pay()(fakeRequest(Vat))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"error":"Error from upstream"}""")
        PayApiStub.verifyStartJourneyVat(count = 1)
      }

      "for Corporation Tax" in {
        PayApiStub.stubForStartJourneyCorporationTax5xx()
        val result = startPaymentController.pay()(fakeRequest(CorporationTax))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"error":"Error from upstream"}""")
        PayApiStub.verifyStartJourneyCorporationTax(count = 1)
      }

      "for Employers Pay As You Earn" in {
        PayApiStub.stubForStartJourneyEmployersPayAsYouEarn5xx()
        val result = startPaymentController.pay()(fakeRequest(EmployersPayAsYouEarn))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"error":"Error from upstream"}""")
        PayApiStub.verifyStartJourneyEmployersPayAsYouEarn(count = 1)
      }
    }

  }

}
