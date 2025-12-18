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
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.{SpjRequest3psCorporationTax, SpjRequest3psEmployersPayAsYouEarn, SpjRequest3psSa, SpjRequest3psVat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{AmountInPence, ClientJourneyId, FriendlyName, Reference, TaxRegime, URL}

final case class ThirdPartyPayRequest(
  taxRegime:     TaxRegime,
  reference:     Reference,
  amountInPence: AmountInPence,
  friendlyName:  Option[FriendlyName],
  backURL:       Option[URL]
) derives CanEqual {

  def asSaSpjRequest(clientJourneyId: ClientJourneyId): SpjRequest3psSa                                       = SpjRequest3psSa(
    utr = reference,
    amountInPence = amountInPence,
    clientJourneyId = clientJourneyId,
    friendlyName = friendlyName,
    returnUrl = backURL,
    backUrl = backURL
  )
  def asVatSpjRequest(clientJourneyId: ClientJourneyId): SpjRequest3psVat                                     = SpjRequest3psVat(
    vrn = reference,
    amountInPence = amountInPence,
    clientJourneyId = clientJourneyId,
    friendlyName = friendlyName,
    returnUrl = backURL,
    backUrl = backURL
  )
  def asCorporationTaxSpjRequest(clientJourneyId: ClientJourneyId): SpjRequest3psCorporationTax               =
    SpjRequest3psCorporationTax(
      vrn = reference,
      amountInPence = amountInPence,
      clientJourneyId = clientJourneyId,
      friendlyName = friendlyName,
      returnUrl = backURL,
      backUrl = backURL
    )
  def asEmployersPayAsYouEarnSpjRequest(clientJourneyId: ClientJourneyId): SpjRequest3psEmployersPayAsYouEarn =
    SpjRequest3psEmployersPayAsYouEarn(
      vrn = reference,
      amountInPence = amountInPence,
      clientJourneyId = clientJourneyId,
      friendlyName = friendlyName,
      returnUrl = backURL,
      backUrl = backURL
    )
}

object ThirdPartyPayRequest {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[ThirdPartyPayRequest] = Json.format[ThirdPartyPayRequest]
}
