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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId

final case class ThirdPartyPayResponse(clientJourneyId: ClientJourneyId, redirectURL: RedirectUrl)

object ThirdPartyPayResponse {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[ThirdPartyPayResponse] = Json.format[ThirdPartyPayResponse]
}

sealed trait ThirdPartyResponseError {
  val errorMessage: String
}

object ThirdPartyResponseErrors {
  case object UpstreamError extends ThirdPartyResponseError {
    val errorMessage = "Error from upstream."
  }
  case object FriendlyNameInvalidCharacterError extends ThirdPartyResponseError {
    val errorMessage = "Friendly name contains invalid character."
  }
  case object FriendlyNameTooLongError extends ThirdPartyResponseError {
    val errorMessage = "Friendly name too long."
  }
  case object NonJsonBodyError extends ThirdPartyResponseError {
    val errorMessage: String = "Request body was not json"
  }
  case object UnexpectedError extends ThirdPartyResponseError {
    val errorMessage: String = "An unexpected error occurred."
  }
}
