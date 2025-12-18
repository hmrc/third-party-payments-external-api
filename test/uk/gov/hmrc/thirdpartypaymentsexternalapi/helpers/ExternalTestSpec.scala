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

import org.scalatest.prop.TableDrivenPropertyChecks
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartySoftwareFindByClientIdResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.testdata.TestData.clientJourneyId

class ExternalTestSpec extends ItSpec with TableDrivenPropertyChecks {

  "paymentJourney" - {
    "when upstream error occurs" - {
      "should return a exception" in {
        val result = ExternalTest.newPaymentJourney(clientJourneyId, "xyz")

        result.failed.futureValue shouldBe a[Exception]
      }

      "should return an exception" in {
        val result = ExternalTest.newPaymentJourney(clientJourneyId, "UPSTREAM_ERROR")

        result.failed.futureValue shouldBe a[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }

    "should return a valid response" - {
      def expectedResponse(paymentStatus: String): ThirdPartySoftwareFindByClientIdResponse =
        ThirdPartySoftwareFindByClientIdResponse(
          clientJourneyId = clientJourneyId,
          taxRegime = "taxRegime",
          amountInPence = 123456L,
          paymentStatus = paymentStatus
        )

      val testCases = Table(
        ("header", "expectedStatus"),
        ("IN_PROGRESS", "InProgress"),
        ("COMPLETED", "Completed"),
        ("FAILED", "Failed")
      )

      forAll(testCases) { (header, expectedStatus) =>
        s"when header is $header" in {
          val result = await(ExternalTest.newPaymentJourney(clientJourneyId, header))

          result shouldBe expectedResponse(expectedStatus)
        }
      }
    }
  }
}
