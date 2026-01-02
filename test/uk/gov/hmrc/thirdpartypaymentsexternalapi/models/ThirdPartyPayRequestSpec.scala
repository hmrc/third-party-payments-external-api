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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.models

import play.api.libs.json.{JsError, JsNumber, JsResult, JsString, JsSuccess, Json, JsonValidationError}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime.{CorporationTax, EmployersPayAsYouEarn, SelfAssessment, Vat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.{SpjRequest, SpjRequest3psCorporationTax, SpjRequest3psEmployersPayAsYouEarn, SpjRequest3psSa, SpjRequest3psVat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartyPayRequest
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

import java.util.UUID

class ThirdPartyPayRequestSpec extends UnitSpec {

  given CanEqual[JsResult[?], JsError] = CanEqual.derived
  given CanEqual[JsResult[?], JsResult[?]] = CanEqual.derived

  "ThirdPartyPayRequest" - {

    val testClientJourneyId: ClientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))

    def thirdPartyPayRequest(taxRegime: TaxRegime): ThirdPartyPayRequest = ThirdPartyPayRequest(
      taxRegime = taxRegime,
      reference = Reference("someReference"),
      amountInPence = AmountInPence(123),
      friendlyName = Some(FriendlyName("Test Company")),
      backURL = Some(URL("https://valid-url.com"))
    )

    def jsValue(taxRegimeString: String) = Json.parse(
      s"""{"taxRegime":"$taxRegimeString","reference":"someReference","amountInPence":123,"friendlyName":"Test Company","backURL":"https://valid-url.com"}"""
    )

    "serialise to json" in {
      Json.toJson(thirdPartyPayRequest(SelfAssessment)) shouldBe jsValue("SelfAssessment")
    }

    "de serialise from json" in {
      Json.fromJson[ThirdPartyPayRequest](jsValue("SelfAssessment")).asEither shouldBe Right(
        thirdPartyPayRequest(SelfAssessment)
      )
    }

    "friendlyName.reads" - {

      "allow for special characters from a list we found on companies house website" in {
        val stringWithAllowedCharacters = "&@£$€¥#.,:; leftofthisisaspace0123456789"
        FriendlyName.reads.reads(JsString(stringWithAllowedCharacters)) shouldBe JsSuccess(
          FriendlyName(stringWithAllowedCharacters)
        )
      }

      "cause a JsError when friendly name string contains a special character not in the allowed list" in {
        val stringContainingInvalidCharacter = "invalidcharinthisstring%"
        FriendlyName.reads.reads(JsString(stringContainingInvalidCharacter)) shouldBe JsError(
          JsonValidationError(List("error.invalidCharacters"))
        )
      }

      "cause a JsError when friendly name string is too long (more than 40 characters)" in {
        val stringMoreThan40Characters = "IamMoreThan40Characters123456789123456789"
        FriendlyName.reads.reads(JsString(stringMoreThan40Characters)) shouldBe JsError(
          JsonValidationError(List("error.maxLength"))
        )
      }
    }

    "reference.reads" - {

      "return JsSuccess when valid reference string used" in {
        val validReference = "1234567895"
        Reference.reads.reads(JsString(validReference)) shouldBe JsSuccess(Reference(validReference))
      }

      "cause a JsError when reference string is an empty string" in {
        val emptyRef = ""
        Reference.reads.reads(JsString(emptyRef)) shouldBe JsError(JsonValidationError(List("error.minLength")))
      }

      "cause a JsError when reference string is not empty, but just spaces - so not valid" in {
        val refJustSpaces = "   "
        Reference.reads.reads(JsString(refJustSpaces)) shouldBe JsError(JsonValidationError(List("error.minLength")))
      }
    }

    "amountInPence.reads" - {

      "return JsSuccess when valid amount used - greater than 0" in {
        val validAmount = 1
        AmountInPence.reads.reads(JsNumber(validAmount)) shouldBe JsSuccess(AmountInPence(validAmount))
      }

      "return JsSuccess when valid amount used - equal to 0" in {
        val validAmount = 0
        AmountInPence.reads.reads(JsNumber(validAmount)) shouldBe JsSuccess(AmountInPence(validAmount))
      }

      "return a JsError when amount is less than 0" in {
        val negativeAmount = -1
        AmountInPence.reads.reads(JsNumber(negativeAmount)) shouldBe JsError(
          JsonValidationError(List("error.minimumValue"))
        )
      }
    }

    "taxRegime.reads" - {
      "return JsSuccess when string exists in enum" in {
        val validTaxRegime = "Vat"
        TaxRegime.keyReads.readKey(validTaxRegime) shouldBe JsSuccess(TaxRegime.Vat)
      }

      "return a JsError when amount is less than 0" in {
        val invalidTaxRegime = "SomeMadeUpRegime"
        TaxRegime.keyReads.readKey(invalidTaxRegime) shouldBe JsError(
          JsonValidationError(List("error.expected.validenumvalue"))
        )
      }

    }

    "URL.reads" - {

      "return JsSuccess when valid url is provided - https" in {
        val validUrl = "https://www.some-accounting-software.com"
        URL.reads.reads(JsString(validUrl)) shouldBe JsSuccess(URL(validUrl))
      }

      "return JsSuccess when valid url is provided - https and port" in {
        val validUrl = "https:443//www.some-accounting-software.com"
        URL.reads.reads(JsString(validUrl)) shouldBe JsSuccess(URL(validUrl))
      }

      "return JsSuccess when valid url is provided - http" in {
        val validUrl = "http://www.some-accounting-software.com"
        URL.reads.reads(JsString(validUrl)) shouldBe JsSuccess(URL(validUrl))
      }

      "return a JsError when url provided is not valid - missing protocol" in {
        val invalidUrl = "www.some-accounting-software.com"
        URL.reads.reads(JsString(invalidUrl)) shouldBe JsError(JsonValidationError(List("error.invalidUrl")))
      }
    }

    "asSaSpjRequest correctly creates SpjRequest3psSa" in {
      val spjRequest = SpjRequest3psSa(
        Reference("someReference"),
        AmountInPence(123),
        testClientJourneyId,
        Some(FriendlyName("Test Company")),
        Some(URL("https://valid-url.com")),
        Some(URL("https://valid-url.com"))
      )
      thirdPartyPayRequest(SelfAssessment).asSaSpjRequest(testClientJourneyId) shouldBe spjRequest
    }

    "asVatSpjRequest correctly creates SpjRequest3psVat" in {
      val spjRequest = SpjRequest3psVat(
        Reference("someReference"),
        AmountInPence(123),
        testClientJourneyId,
        Some(FriendlyName("Test Company")),
        Some(URL("https://valid-url.com")),
        Some(URL("https://valid-url.com"))
      )
      thirdPartyPayRequest(Vat).asVatSpjRequest(testClientJourneyId) shouldBe spjRequest
    }

    "asCorporationTaxSpjRequest correctly creates SpjRequest3psCorporationTax" in {
      val spjRequest = SpjRequest3psCorporationTax(
        Reference("someReference"),
        AmountInPence(123),
        testClientJourneyId,
        Some(FriendlyName("Test Company")),
        Some(URL("https://valid-url.com")),
        Some(URL("https://valid-url.com"))
      )
      thirdPartyPayRequest(CorporationTax).asCorporationTaxSpjRequest(testClientJourneyId) shouldBe spjRequest
    }

    "asEmployersPayAsYouEarnSpjRequest correctly creates SpjRequest3psEmployersPayAsYouEarn" in {
      val spjRequest = SpjRequest3psEmployersPayAsYouEarn(
        Reference("someReference"),
        AmountInPence(123),
        testClientJourneyId,
        Some(FriendlyName("Test Company")),
        Some(URL("https://valid-url.com")),
        Some(URL("https://valid-url.com"))
      )
      thirdPartyPayRequest(EmployersPayAsYouEarn).asEmployersPayAsYouEarnSpjRequest(
        testClientJourneyId
      ) shouldBe spjRequest
    }
  }
}
