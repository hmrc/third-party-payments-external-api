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
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.{SpjRequest3psCorporationTax, SpjRequest3psEmployersPayAsYouEarn, SpjRequest3psSa, SpjRequest3psVat}

import java.time.LocalDate
import java.util.UUID

final case class ThirdPartyPayRequest(
    taxRegime:       TaxRegime,
    reference:       String,
    amountInPence:   Int,
    clientJourneyId: UUID,
    backURL:         String,
    dueDate:         Option[LocalDate]
//TODO: we should introduce some witness types too, not now though.
) {
  def asSaSpjRequest(): SpjRequest3psSa = SpjRequest3psSa(
    utr             = reference,
    amountInPence   = amountInPence,
    clientJourneyId = clientJourneyId,
    returnUrl       = Some(backURL),
    backUrl         = Some(backURL),
    dueDate         = dueDate
  )
  def asVatSpjRequest(): SpjRequest3psVat = SpjRequest3psVat(
    vrn             = reference,
    amountInPence   = amountInPence,
    clientJourneyId = clientJourneyId,
    returnUrl       = Some(backURL),
    backUrl         = Some(backURL),
    dueDate         = dueDate
  )
  def asCorporationTaxSpjRequest(): SpjRequest3psCorporationTax = SpjRequest3psCorporationTax(
    vrn             = reference,
    amountInPence   = amountInPence,
    clientJourneyId = clientJourneyId,
    returnUrl       = Some(backURL),
    backUrl         = Some(backURL),
    dueDate         = dueDate
  )
  def asEmployersPayAsYouEarnSpjRequest(): SpjRequest3psEmployersPayAsYouEarn = SpjRequest3psEmployersPayAsYouEarn(
    vrn             = reference,
    amountInPence   = amountInPence,
    clientJourneyId = clientJourneyId,
    returnUrl       = Some(backURL),
    backUrl         = Some(backURL),
    dueDate         = dueDate
  )
}

object ThirdPartyPayRequest {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[ThirdPartyPayRequest] = Json.format[ThirdPartyPayRequest]
}
