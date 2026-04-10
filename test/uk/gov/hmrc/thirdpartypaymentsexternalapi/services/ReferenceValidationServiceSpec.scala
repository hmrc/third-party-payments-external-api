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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import play.api.libs.json.{JsPath, JsonValidationError}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.Reference
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime.*
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartyPayRequest
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.testdata.TestData

class ReferenceValidationServiceSpec extends UnitSpec {

  val service = new ReferenceValidationService

  "validateReference" - {

    "return a Right[ThirdPartyPayRequest]" - {

      "for SelfAssessment" - {
        "when sa utr is valid" in {
          val thirdPartyPayRequest = ThirdPartyPayRequest(SelfAssessment, TestData.saUtr, TestData.testAmountInPence, None, None)
          val result               = service.validateReference(thirdPartyPayRequest)
          result shouldBe Right(thirdPartyPayRequest)
        }
        "when sa utr is valid (with K at the beginning" in {
          val thirdPartyPayRequest = ThirdPartyPayRequest(SelfAssessment, TestData.saUtrPrependedK, TestData.testAmountInPence, None, None)
          val result               = service.validateReference(thirdPartyPayRequest)
          result shouldBe Right(thirdPartyPayRequest)
        }
        "when sa utr is valid (with K at the end)" in {
          val thirdPartyPayRequest = ThirdPartyPayRequest(SelfAssessment, TestData.saUtrAppendedK, TestData.testAmountInPence, None, None)
          val result               = service.validateReference(thirdPartyPayRequest)
          result shouldBe Right(thirdPartyPayRequest)
        }
      }

      "for Vat" in {
        val thirdPartyPayRequest = ThirdPartyPayRequest(Vat, TestData.vrn, TestData.testAmountInPence, None, None)
        val result               = service.validateReference(thirdPartyPayRequest)
        result shouldBe Right(thirdPartyPayRequest)
      }

      "for CorporationTax" in {
        val thirdPartyPayRequest = ThirdPartyPayRequest(CorporationTax, TestData.ctReference, TestData.testAmountInPence, None, None)
        val result               = service.validateReference(thirdPartyPayRequest)
        result shouldBe Right(thirdPartyPayRequest)
      }

      "for EmployersPayAsYouEarn" in {
        val thirdPartyPayRequest = ThirdPartyPayRequest(EmployersPayAsYouEarn, TestData.epayeReference, TestData.testAmountInPence, None, None)
        val result               = service.validateReference(thirdPartyPayRequest)
        result shouldBe Right(thirdPartyPayRequest)
      }
    }

    "return a Left[collection.Seq[(JsPath, collection.Seq[JsonValidationError])]] when reference validation fails" - {

      given CanEqual[
        Either[collection.Seq[(JsPath, collection.Seq[JsonValidationError])], ThirdPartyPayRequest],
        Either[collection.Seq[(JsPath, collection.Seq[JsonValidationError])], ThirdPartyPayRequest]
      ] = CanEqual.derived

      "for SelfAssessment" in {
        val thirdPartyPayRequest = ThirdPartyPayRequest(SelfAssessment, Reference("1234567890"), TestData.testAmountInPence, None, None)
        val result               = service.validateReference(thirdPartyPayRequest)
        result shouldBe Left(collection.Seq((JsPath \ "reference", collection.Seq(JsonValidationError(Seq("error.saReference.invalid"))))))
      }

      "for Vat" in {
        val thirdPartyPayRequest = ThirdPartyPayRequest(Vat, Reference("1234567890"), TestData.testAmountInPence, None, None)
        val result               = service.validateReference(thirdPartyPayRequest)
        result shouldBe Left(collection.Seq((JsPath \ "reference", collection.Seq(JsonValidationError(Seq("error.vatReference.invalid"))))))
      }

      "for CorporationTax" in {
        val thirdPartyPayRequest = ThirdPartyPayRequest(CorporationTax, Reference("1234567890"), TestData.testAmountInPence, None, None)
        val result               = service.validateReference(thirdPartyPayRequest)
        result shouldBe Left(collection.Seq((JsPath \ "reference", collection.Seq(JsonValidationError(Seq("error.ctReference.invalid"))))))
      }

      "for EmployersPayAsYouEarn" in {
        val thirdPartyPayRequest = ThirdPartyPayRequest(EmployersPayAsYouEarn, Reference("1234567890"), TestData.testAmountInPence, None, None)
        val result               = service.validateReference(thirdPartyPayRequest)
        result shouldBe Left(collection.Seq((JsPath \ "reference", collection.Seq(JsonValidationError(Seq("error.epayeReference.invalid"))))))
      }
    }
  }

}
