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
import play.api.mvc.AnyContentAsJson
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.mvc.Http.Status
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime.{CorporationTax, EmployersPayAsYouEarn, SelfAssessment, Vat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{RedirectUrl, ThirdPartyPayRequest, ThirdPartyPayResponse}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{ClientJourneyId, FriendlyName, TaxRegime}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.PayApiStub

import java.util.UUID

class StartPaymentControllerSpec extends ItSpec {

  private val startPaymentController = app.injector.instanceOf[StartPaymentController]

  private def testThirdPartyRequest(taxRegime: TaxRegime, friendlyName: Option[FriendlyName]): ThirdPartyPayRequest = ThirdPartyPayRequest(
    taxRegime     = taxRegime,
    reference     = "1234567895",
    amountInPence = 123,
    friendlyName  = friendlyName,
    backURL       = Some("https://www.someBackUrl.com")
  )

  private val clientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))
  private val expectedTestThirdPartyPayResponse = ThirdPartyPayResponse(clientJourneyId, RedirectUrl("https://somenext-url.co.uk"))

  private def fakeRequest(taxRegime: TaxRegime, friendlyName: Option[FriendlyName]): FakeRequest[AnyContentAsJson] =
    FakeRequest("POST", "/pay").withJsonBody(Json.toJson(testThirdPartyRequest(taxRegime, friendlyName)))

  "POST /pay" - {

    "return 201 Created when pay-api returns SpjResponse" - {

      "for Self Assessment" in {
        PayApiStub.stubForStartJourneySelfAssessment()
        val result = startPaymentController.pay()(fakeRequest(SelfAssessment, Some(FriendlyName("Test Company"))))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        PayApiStub.verifyStartJourneySelfAssessment(count = 1)
      }

      "for Vat" in {
        PayApiStub.stubForStartJourneyVat()
        val result = startPaymentController.pay()(fakeRequest(Vat, Some(FriendlyName("Test Company"))))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        PayApiStub.verifyStartJourneyVat(count = 1)
      }

      "for Corporation Tax" in {
        PayApiStub.stubForStartJourneyCorporationTax()
        val result = startPaymentController.pay()(fakeRequest(CorporationTax, Some(FriendlyName("Test Company"))))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        PayApiStub.verifyStartJourneyCorporationTax(count = 1)
      }

      "for Employers Pay As You Earn" in {
        PayApiStub.stubForStartJourneyEmployersPayAsYouEarn()
        val result = startPaymentController.pay()(fakeRequest(EmployersPayAsYouEarn, Some(FriendlyName("Test Company"))))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        PayApiStub.verifyStartJourneyEmployersPayAsYouEarn(count = 1)
      }
    }

    "return an InternalServerError with UpstreamError message when pay-api returns an error" - {

      "for Self Assessment" in {
        PayApiStub.stubForStartJourneySelfAssessment5xx()
        val result = startPaymentController.pay()(fakeRequest(SelfAssessment, Some(FriendlyName("Test Company"))))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"error":"Error from upstream."}""")
        PayApiStub.verifyStartJourneySelfAssessment(count = 1)
      }

      "for Vat" in {
        PayApiStub.stubForStartJourneyVat5xx()
        val result = startPaymentController.pay()(fakeRequest(Vat, Some(FriendlyName("Test Company"))))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"error":"Error from upstream."}""")
        PayApiStub.verifyStartJourneyVat(count = 1)
      }

      "for Corporation Tax" in {
        PayApiStub.stubForStartJourneyCorporationTax5xx()
        val result = startPaymentController.pay()(fakeRequest(CorporationTax, Some(FriendlyName("Test Company"))))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"error":"Error from upstream."}""")
        PayApiStub.verifyStartJourneyCorporationTax(count = 1)
      }

      "for Employers Pay As You Earn" in {
        PayApiStub.stubForStartJourneyEmployersPayAsYouEarn5xx()
        val result = startPaymentController.pay()(fakeRequest(EmployersPayAsYouEarn, Some(FriendlyName("Test Company"))))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"error":"Error from upstream."}""")
        PayApiStub.verifyStartJourneyEmployersPayAsYouEarn(count = 1)
      }
    }

    "return a BadRequest with NonJsonBodyError message when body is not valid json" in {
      val result = startPaymentController.pay()(FakeRequest().withBody("somestringthatisn'tjson"))
      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe Json.parse("""{"error":"Request body was not json"}""")
    }

    "return an InternalServerError with UnexpectedError message when parsing results in JsError and key is not recognised" in {
      val result = startPaymentController.pay()(FakeRequest().withJsonBody(Json.parse("""{"IamValidJson":"butnotmatchingthemodel"}""")))
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      contentAsJson(result) shouldBe Json.parse("""{"error":"An unexpected error occurred."}""")
    }

    "return a BadRequest with relevant error message when friendly name" - {

      "is too long (more than 40 characters)" in {
        val stringMoreThan40Characters = "IamMoreThan40Characters123456789123456789"
        val result = startPaymentController.pay()(fakeRequest(SelfAssessment, Some(FriendlyName(stringMoreThan40Characters))))
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.parse("""{"error":"Friendly name too long."}""")
      }

      "contains invalid characters " in {
        val stringContainingInvalidCharacter = "invalidcharinthisstring%"
        val result = startPaymentController.pay()(fakeRequest(SelfAssessment, Some(FriendlyName(stringContainingInvalidCharacter))))
        status(result) shouldBe Status.BAD_REQUEST
        contentAsJson(result) shouldBe Json.parse("""{"error":"Friendly name contains invalid character."}""")
      }
    }

  }

}
