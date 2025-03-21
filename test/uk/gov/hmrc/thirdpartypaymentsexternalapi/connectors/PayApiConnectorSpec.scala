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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.connectors

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{AmountInPence, ClientJourneyId, FriendlyName, Reference}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.{SpjRequest3psCorporationTax, SpjRequest3psEmployersPayAsYouEarn, SpjRequest3psSa, SpjRequest3psVat, SpjResponse}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.PayApiStub
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.testdata.PayApiTestData

import java.util.UUID
import scala.concurrent.Future

class PayApiConnectorSpec extends ItSpec {

  val payApiConnector: PayApiConnector = app.injector.instanceOf[PayApiConnector]
  val testReference: Reference = Reference("1234567895")
  val testAmountInPence: AmountInPence = AmountInPence(123)
  val testClientJourneyId: ClientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))
  val testFriendlyName: FriendlyName = FriendlyName("Test Company")

  "payApiConnector" - {

    "startSelfAssessmentJourney" - {

      val testSpjRequest3psSa: SpjRequest3psSa = SpjRequest3psSa(testReference, testAmountInPence, testClientJourneyId, Some(testFriendlyName), None, None)

      "should return an SpjResponse given pay-api call succeeds" in {
        PayApiStub.stubForStartJourneySelfAssessment()

        val expectedResult: SpjResponse = PayApiTestData.spjResponse
        val result: Future[SpjResponse] = payApiConnector.startSelfAssessmentJourney(testSpjRequest3psSa)(HeaderCarrier())

        result.futureValue shouldBe expectedResult
      }

      "propagate a 5xx error when pay-api returns a 5xx" in {
        PayApiStub.stubForStartJourneySelfAssessment5xx()
        val error: Exception = intercept[Exception](payApiConnector.startSelfAssessmentJourney(testSpjRequest3psSa)(HeaderCarrier()).futureValue)
        error.getCause.getMessage should include(s"POST of 'http://localhost:${wireMockPort.toString}/pay-api/third-party-software/self-assessment/journey/start' returned 503.")
      }
    }

    "startVatJourney" - {

      val testSpjRequest3psVat: SpjRequest3psVat = SpjRequest3psVat(testReference, testAmountInPence, testClientJourneyId, Some(testFriendlyName), None, None)

      "should return an SpjResponse given pay-api call succeeds" in {
        PayApiStub.stubForStartJourneyVat()

        val expectedResult: SpjResponse = PayApiTestData.spjResponse
        val result: Future[SpjResponse] = payApiConnector.startVatJourney(testSpjRequest3psVat)(HeaderCarrier())

        result.futureValue shouldBe expectedResult
      }

      "propagate a 5xx error when pay-api returns a 5xx" in {
        PayApiStub.stubForStartJourneyVat5xx()
        val error: Exception = intercept[Exception](payApiConnector.startVatJourney(testSpjRequest3psVat)(HeaderCarrier()).futureValue)
        error.getCause.getMessage should include(s"POST of 'http://localhost:${wireMockPort.toString}/pay-api/third-party-software/vat/journey/start' returned 503.")
      }
    }

    "startCorporationTaxJourney" - {

      val testSpjRequest3psCorporationTax: SpjRequest3psCorporationTax = SpjRequest3psCorporationTax(testReference, testAmountInPence, testClientJourneyId, Some(testFriendlyName), None, None)

      "should return an SpjResponse given pay-api call succeeds" in {
        PayApiStub.stubForStartJourneyCorporationTax()

        val expectedResult: SpjResponse = PayApiTestData.spjResponse
        val result: Future[SpjResponse] = payApiConnector.startCorporationTaxJourney(testSpjRequest3psCorporationTax)(HeaderCarrier())

        result.futureValue shouldBe expectedResult
      }

      "propagate a 5xx error when pay-api returns a 5xx" in {
        PayApiStub.stubForStartJourneyCorporationTax5xx()
        val error: Exception = intercept[Exception](payApiConnector.startCorporationTaxJourney(testSpjRequest3psCorporationTax)(HeaderCarrier()).futureValue)
        error.getCause.getMessage should include(s"POST of 'http://localhost:${wireMockPort.toString}/pay-api/third-party-software/corporation-tax/journey/start' returned 503.")
      }
    }

    "startEmployersPayAsYouEarnJourney" - {

      val testSpjRequest3psEmployersPayAsYouEarn: SpjRequest3psEmployersPayAsYouEarn = SpjRequest3psEmployersPayAsYouEarn(testReference, testAmountInPence, testClientJourneyId, Some(testFriendlyName), None, None)

      "should return an SpjResponse given pay-api call succeeds" in {
        PayApiStub.stubForStartJourneyEmployersPayAsYouEarn()

        val expectedResult: SpjResponse = PayApiTestData.spjResponse
        val result: Future[SpjResponse] = payApiConnector.startEmployersPayAsYouEarnJourney(testSpjRequest3psEmployersPayAsYouEarn)(HeaderCarrier())

        result.futureValue shouldBe expectedResult
      }

      "propagate a 5xx error when pay-api returns a 5xx" in {
        PayApiStub.stubForStartJourneyEmployersPayAsYouEarn5xx()
        val error: Exception = intercept[Exception](payApiConnector.startEmployersPayAsYouEarnJourney(testSpjRequest3psEmployersPayAsYouEarn)(HeaderCarrier()).futureValue)
        error.getCause.getMessage should include(s"POST of 'http://localhost:${wireMockPort.toString}/pay-api/third-party-software/employers-pay-as-you-earn/journey/start' returned 503.")
      }
    }
  }
}
