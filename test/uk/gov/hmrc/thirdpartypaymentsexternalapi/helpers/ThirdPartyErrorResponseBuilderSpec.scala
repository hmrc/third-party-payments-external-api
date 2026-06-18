/*
 * Copyright 2026 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.mvc.Results
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartyResponseErrors
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec

import scala.concurrent.Future

class ThirdPartyErrorResponseBuilderSpec extends ItSpec with TableDrivenPropertyChecks {

  "fromThirdPartyErrors" - {
    "should return 500 and include all messages when errors contain an unexpected error" in {
      val result = ThirdPartyErrorResponseBuilder.fromThirdPartyErrors(
        Seq(
          ThirdPartyResponseErrors.UnexpectedError("some reason"),
          ThirdPartyResponseErrors.ReferenceMissingError
        )
      )

      result.header.status shouldBe Results.InternalServerError.header.status
      contentAsJson(Future.successful(result)) shouldBe Json.parse(
        """{
          |  "errors": [
          |    "An unexpected error occurred: some reason",
          |    "Mandatory reference field missing."
          |  ]
          |}""".stripMargin
      )
    }

    "should return 500 when errors contain an upstream error" in {
      val result = ThirdPartyErrorResponseBuilder.fromThirdPartyErrors(
        Seq(ThirdPartyResponseErrors.UpstreamError)
      )

      result.header.status shouldBe Results.InternalServerError.header.status
      contentAsJson(Future.successful(result)) shouldBe Json.parse("""{"errors":["Error from upstream."]}""")
    }

    "should return 400 and include all validation error messages" in {
      val result = ThirdPartyErrorResponseBuilder.fromThirdPartyErrors(
        Seq(
          ThirdPartyResponseErrors.AmountInPenceMissingError,
          ThirdPartyResponseErrors.ReferenceMissingError,
          ThirdPartyResponseErrors.TaxRegimeMissingError
        )
      )

      result.header.status shouldBe Results.BadRequest.header.status
      contentAsJson(Future.successful(result)) shouldBe Json.parse(
        """{
          |  "errors": [
          |    "Mandatory amountInPence field missing.",
          |    "Mandatory reference field missing.",
          |    "Mandatory taxRegime field missing."
          |  ]
          |}""".stripMargin
      )
    }
  }

  "fromUpstreamError" - {
    val testCases = Table(
      ("statusCode", "expectedStatus"),
      (404, Results.NotFound.header.status),
      (500, Results.InternalServerError.header.status),
      (400, Results.InternalServerError.header.status)
    )

    forAll(testCases) { (statusCode, expectedStatus) =>
      s"should return $expectedStatus when upstream returns $statusCode" in {
        val upstreamError = UpstreamErrorResponse("some upstream message", statusCode, statusCode)

        val result = ThirdPartyErrorResponseBuilder.fromUpstreamError(upstreamError)

        result.header.status shouldBe expectedStatus
        contentAsJson(Future.successful(result)) shouldBe Json.parse(
          """{"error":"some upstream message"}"""
        )
      }
    }
  }
}

