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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty

sealed trait ThirdPartyResponseError {
  val errorMessage: String
}

object ThirdPartyResponseErrors {
  case object UpstreamError extends ThirdPartyResponseError {
    val errorMessage = "Error from upstream."
  }
  case object FriendlyNameInvalidCharacterError extends ThirdPartyResponseError {
    val errorMessage = "friendlyName field contains invalid character."
  }
  case object FriendlyNameTooLongError extends ThirdPartyResponseError {
    val errorMessage = "friendlyName field too long."
  }
  case object TaxRegimeMissingError extends ThirdPartyResponseError {
    val errorMessage: String = "Mandatory taxRegime field missing."
  }
  case object TaxRegimeInvalidError extends ThirdPartyResponseError {
    val errorMessage: String = "Mandatory taxRegime is not in list of acceptable values."
  }
  case object ReferenceMissingError extends ThirdPartyResponseError {
    val errorMessage: String = "Mandatory reference field missing."
  }
  case object ReferenceInvalidError extends ThirdPartyResponseError {
    val errorMessage: String = "Mandatory reference field invalid."
  }
  case object AmountInPenceMissingError extends ThirdPartyResponseError {
    val errorMessage: String = "Mandatory amountInPence field missing."
  }
  case object AmountInPenceInvalidError extends ThirdPartyResponseError {
    val errorMessage: String = "Mandatory amountInPence field must be greater than or equal to 0."
  }
  case object BackUrlInvalidError extends ThirdPartyResponseError {
    val errorMessage: String = "backURL field must be a valid url if provided."
  }
  case object NonJsonBodyError extends ThirdPartyResponseError {
    val errorMessage: String = "Request body was not json"
  }
  final case class UnexpectedError(extraReason: String) extends ThirdPartyResponseError {
    val errorMessage: String = s"An unexpected error occurred: $extraReason"
  }
}

