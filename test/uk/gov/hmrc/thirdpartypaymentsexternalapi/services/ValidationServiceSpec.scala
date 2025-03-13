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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import play.api.libs.json._
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime.SelfAssessment
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models._
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{ThirdPartyPayRequest, ThirdPartyResponseError, ThirdPartyResponseErrors}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

class ValidationServiceSpec extends UnitSpec {

  val service: ValidationService = new ValidationService

  "validateThirdPartyRequest" - {

    "return a Right[ThirdPartyPayRequest] when jsValue is valid" in {
      val result = service.validateThirdPartyRequest(Json.parse("""{"taxRegime":"SelfAssessment","reference":"1234567895","amountInPence":123,"friendlyName":"Test Company","backURL":"https://valid-url.com"}"""))
      result shouldBe Right(ThirdPartyPayRequest(SelfAssessment, Reference("1234567895"), AmountInPence(123), Some(FriendlyName("Test Company")), Some(URL("https://valid-url.com"))))
    }

    "return a Left[Seq[ThirdPartyResponseError]] when jsValue is not valid" in {
      val result = service.validateThirdPartyRequest(Json.parse("""{"invalid":"json"}"""))
      result shouldBe Left(Seq[ThirdPartyResponseError](ThirdPartyResponseErrors.AmountInPenceMissingError, ThirdPartyResponseErrors.TaxRegimeMissingError, ThirdPartyResponseErrors.ReferenceMissingError))
    }
  }

  "jsErrorToMessagesBetter" - {
    "should take the json key and prepend it to a relevant json validation error key" in {
      val input: collection.Seq[(JsPath, collection.Seq[JsonValidationError])] = collection.Seq[(JsPath, collection.Seq[JsonValidationError])](
        JsPath(List[PathNode](KeyPathNode("/something"))) -> collection.Seq(JsonValidationError("some.error.message"))
      )
      val result = service.jsErrorToMessagesBetter(input)
      result shouldBe Seq("something.some.error.message")
    }

    "strip the / at the start of the key" in {
      val input: collection.Seq[(JsPath, collection.Seq[JsonValidationError])] = collection.Seq[(JsPath, collection.Seq[JsonValidationError])](
        JsPath(List[PathNode](KeyPathNode("/something"))) -> collection.Seq(JsonValidationError(""))
      )
      val result = service.jsErrorToMessagesBetter(input)
      result shouldBe Seq("something.")
    }

    "should take the json key and prepend it to all relevant json validation error keys" in {
      val input: collection.Seq[(JsPath, collection.Seq[JsonValidationError])] = collection.Seq[(JsPath, collection.Seq[JsonValidationError])](
        JsPath(List[PathNode](KeyPathNode("/something"))) -> collection.Seq(JsonValidationError("some.error.message"), JsonValidationError("some.other.error.message"), JsonValidationError("yet.another.error.message"))
      )
      val result = service.jsErrorToMessagesBetter(input)
      result shouldBe Seq("something.some.error.message", "something.some.other.error.message", "something.yet.another.error.message")
    }
  }

  "errorMessageKeyToThirdPartyResponseErrors" - {
    "return UnexpectedError with the custom string in when the key can't be found" in {
      service.errorMessageKeyToThirdPartyResponseErrors("i-don't-exist") shouldBe ThirdPartyResponseErrors.UnexpectedError("i-don't-exist")
    }
    "return the correct ThirdPartyResponse for given keys" in {
      service.errorMessageKeyToThirdPartyResponseErrors("friendlyName.error.invalidCharacters") shouldBe ThirdPartyResponseErrors.FriendlyNameInvalidCharacterError
      service.errorMessageKeyToThirdPartyResponseErrors("friendlyName.error.maxLength") shouldBe ThirdPartyResponseErrors.FriendlyNameTooLongError
      service.errorMessageKeyToThirdPartyResponseErrors("taxRegime.error.expected.validenumvalue") shouldBe ThirdPartyResponseErrors.TaxRegimeInvalidError
      service.errorMessageKeyToThirdPartyResponseErrors("taxRegime.error.path.missing") shouldBe ThirdPartyResponseErrors.TaxRegimeMissingError
      service.errorMessageKeyToThirdPartyResponseErrors("reference.error.minLength") shouldBe ThirdPartyResponseErrors.ReferenceInvalidError
      service.errorMessageKeyToThirdPartyResponseErrors("reference.error.path.missing") shouldBe ThirdPartyResponseErrors.ReferenceMissingError
      service.errorMessageKeyToThirdPartyResponseErrors("amountInPence.error.path.missing") shouldBe ThirdPartyResponseErrors.AmountInPenceMissingError
      service.errorMessageKeyToThirdPartyResponseErrors("amountInPence.error.minimumValue") shouldBe ThirdPartyResponseErrors.AmountInPenceInvalidError
      service.errorMessageKeyToThirdPartyResponseErrors("backURL.error.invalidUrl") shouldBe ThirdPartyResponseErrors.BackUrlInvalidError
    }
  }

}
