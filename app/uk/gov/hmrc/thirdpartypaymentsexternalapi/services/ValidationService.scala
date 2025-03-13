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

import cats.syntax.all._
import jakarta.inject.Singleton
import play.api.libs.json.{JsPath, JsValue, JsonValidationError}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{ThirdPartyPayRequest, ThirdPartyResponseError, ThirdPartyResponseErrors}

@Singleton
final class ValidationService {

  def validateThirdPartyRequest(jsValue: JsValue): Either[Seq[ThirdPartyResponseError], ThirdPartyPayRequest] = {
    jsValue
      .validate[ThirdPartyPayRequest]
      .asEither
      .leftMap(errors => jsErrorToMessagesBetter(errors).map(errorMessageKeyToThirdPartyResponseErrors))
  }

  private[services] def jsErrorToMessagesBetter(errors: collection.Seq[(JsPath, collection.Seq[JsonValidationError])]): Seq[String] = {
    errors.flatMap((e: (JsPath, collection.Seq[JsonValidationError])) => {
      //i.e. the json key value, such as amountInPence
      val path: String = e._1.toString().replaceAll("/", "")
      //i.e. the list of errors associated with this jserror, such as amountInPence.error.path.missing
      val errors: collection.Seq[String] = e._2.map(_.message)
      errors.map((messages: String) => s"$path.$messages")
    }).toSeq
  }

  private[services] def errorMessageKeyToThirdPartyResponseErrors: String => ThirdPartyResponseError = {
    case "friendlyName.error.invalidCharacters"    => ThirdPartyResponseErrors.FriendlyNameInvalidCharacterError
    case "friendlyName.error.maxLength"            => ThirdPartyResponseErrors.FriendlyNameTooLongError
    case "taxRegime.error.expected.validenumvalue" => ThirdPartyResponseErrors.TaxRegimeInvalidError
    case "taxRegime.error.path.missing"            => ThirdPartyResponseErrors.TaxRegimeMissingError
    case "reference.error.minLength"               => ThirdPartyResponseErrors.ReferenceInvalidError
    case "reference.error.path.missing"            => ThirdPartyResponseErrors.ReferenceMissingError
    case "amountInPence.error.path.missing"        => ThirdPartyResponseErrors.AmountInPenceMissingError
    case "amountInPence.error.minimumValue"        => ThirdPartyResponseErrors.AmountInPenceInvalidError
    case "backURL.error.invalidUrl"                => ThirdPartyResponseErrors.BackUrlInvalidError
    case s                                         => ThirdPartyResponseErrors.UnexpectedError(s)
  }

}
