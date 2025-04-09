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

import org.scalatest.prop.TableDrivenPropertyChecks
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.OpenBankingStub
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.testdata.TestData.clientJourneyId

class FindPaymentControllerSpec extends ItSpec with TableDrivenPropertyChecks {

  private val findPaymentController = app.injector.instanceOf[FindPaymentController]

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/status")

  "GET /status" - {
    "return 200 OK when findPaymentService returns the payment" in {
      OpenBankingStub.stubForFindJourneyByClientId(clientJourneyId)

      val result = findPaymentController.status(clientJourneyId)(fakeRequest)

      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.parse(
        """{
          |  "clientJourneyId" : "aef0f31b-3c0f-454b-9d1f-07d549987a96",
          |  "taxRegime" : "TaxRegime",
          |  "amountInPence" : 1234,
          |  "paymentStatus" : "InProgress"
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

  "GET /status when config allows external test" - {
    def applicationBuilder: GuiceApplicationBuilder = super.applicationBuilder().configure(configMap ++ Map("external-test.testOnly-headers.enabled" -> true))
    val app2 = applicationBuilder.build()
    val findPaymentController = app2.injector.instanceOf[FindPaymentController]

    "should return test data when a valid value is provided for the header \"Gov-Test-Scenario\"" - {
      val testCases = Table(
        ("header", "expectedReturnStatus"),
        ("IN_PROGRESS", "InProgress"),
        ("COMPLETED", "Completed"),
        ("FAILED", "Failed")
      )

      forAll(testCases) { (header, expectedReturnStatus) =>
        s"return 200 OK with and a status of $expectedReturnStatus when endpoint receives $header in the headers" in {
          val result = findPaymentController.status(clientJourneyId)(fakeRequest.withHeaders(("Gov-Test-Scenario", header)))

          status(result) shouldBe Status.OK
          contentAsJson(result) shouldBe Json.parse(
            s"""{
               |  "clientJourneyId" : "aef0f31b-3c0f-454b-9d1f-07d549987a96",
               |  "taxRegime" : "taxRegime",
               |  "amountInPence" : 123456,
               |  "paymentStatus" : "$expectedReturnStatus"
               |}""".stripMargin
          )
        }
      }

      "should return a exception when the clientJourneyId is not found" in {
        val result = findPaymentController.status(clientJourneyId)(fakeRequest.withHeaders(("Gov-Test-Scenario", "xyz")))

        result.failed.futureValue shouldBe a[Exception]
      }

      "should return 500 InternalServerError when an exception is thrown" in {
        val result = findPaymentController.status(clientJourneyId)(fakeRequest.withHeaders(("Gov-Test-Scenario", "UPSTREAM_ERROR")))

        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

}
