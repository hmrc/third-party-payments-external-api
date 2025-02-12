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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{ClientJourneyId, TaxRegime}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime.{CorporationTax, EmployersPayAsYouEarn, SelfAssessment, Vat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{FriendlyName, RedirectUrl, ThirdPartyPayRequest, ThirdPartyPayResponse, ThirdPartyResponseErrors}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.PayApiStub

import java.util.UUID

class PayApiServiceSpec extends ItSpec {

  val payApiService: PayApiService = app.injector.instanceOf[PayApiService]

  implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  def thirdPartyPayRequest(taxRegime: TaxRegime): ThirdPartyPayRequest = ThirdPartyPayRequest(
    taxRegime     = taxRegime,
    reference     = "someReference",
    amountInPence = 123,
    friendlyName  = Some(FriendlyName("Test Company")),
    backURL       = "some-back-url"
  )

  val testThirdPartyPayResponse: ThirdPartyPayResponse = ThirdPartyPayResponse(
    clientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96")),
    redirectURL     = RedirectUrl("https://somenext-url.co.uk")
  )

  "PayApiService" - {

    "startPaymentJourney" - {

      "return a Right[ThirdPartyPayResponse] when pay-api call succeeds" - {

        "for Self Assessment" in {
          PayApiStub.stubForStartJourneySelfAssessment()
          val result = payApiService.startPaymentJourney(thirdPartyPayRequest(SelfAssessment))
          result.futureValue shouldBe Right(testThirdPartyPayResponse)
          PayApiStub.verifyStartJourneySelfAssessment(count = 1)
        }

        "for Vat" in {
          PayApiStub.stubForStartJourneyVat()
          val result = payApiService.startPaymentJourney(thirdPartyPayRequest(Vat))
          result.futureValue shouldBe Right(testThirdPartyPayResponse)
          PayApiStub.verifyStartJourneyVat(count = 1)
        }

        "for Corporation Tax" in {
          PayApiStub.stubForStartJourneyCorporationTax()
          val result = payApiService.startPaymentJourney(thirdPartyPayRequest(CorporationTax))
          result.futureValue shouldBe Right(testThirdPartyPayResponse)
          PayApiStub.verifyStartJourneyCorporationTax(count = 1)
        }

        "for Employers Pay As You Earn" in {
          PayApiStub.stubForStartJourneyEmployersPayAsYouEarn()
          val result = payApiService.startPaymentJourney(thirdPartyPayRequest(EmployersPayAsYouEarn))
          result.futureValue shouldBe Right(testThirdPartyPayResponse)
          PayApiStub.verifyStartJourneyEmployersPayAsYouEarn(count = 1)
        }
      }

      "return a Left[UpstreamError] when pay-api call fails" - {
        "for Self Assessment" in {
          PayApiStub.stubForStartJourneySelfAssessment5xx()
          val result = payApiService.startPaymentJourney(thirdPartyPayRequest(SelfAssessment))
          result.futureValue shouldBe Left(ThirdPartyResponseErrors.UpstreamError)
          PayApiStub.verifyStartJourneySelfAssessment(count = 1)
        }

        "for Vat" in {
          PayApiStub.stubForStartJourneyVat5xx()
          val result = payApiService.startPaymentJourney(thirdPartyPayRequest(Vat))
          result.futureValue shouldBe Left(ThirdPartyResponseErrors.UpstreamError)
          PayApiStub.verifyStartJourneyVat(count = 1)
        }

        "for Corporation Tax" in {
          PayApiStub.stubForStartJourneyCorporationTax5xx()
          val result = payApiService.startPaymentJourney(thirdPartyPayRequest(CorporationTax))
          result.futureValue shouldBe Left(ThirdPartyResponseErrors.UpstreamError)
          PayApiStub.verifyStartJourneyCorporationTax(count = 1)
        }

        "for Employers Pay As You Earn" in {
          PayApiStub.stubForStartJourneyEmployersPayAsYouEarn5xx()
          val result = payApiService.startPaymentJourney(thirdPartyPayRequest(EmployersPayAsYouEarn))
          result.futureValue shouldBe Left(ThirdPartyResponseErrors.UpstreamError)
          PayApiStub.verifyStartJourneyEmployersPayAsYouEarn(count = 1)
        }
      }
    }
  }

}
