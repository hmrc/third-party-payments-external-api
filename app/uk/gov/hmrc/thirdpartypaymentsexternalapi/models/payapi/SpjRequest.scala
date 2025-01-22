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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId

import java.time.LocalDate

sealed trait SpjRequest

final case class SpjRequest3psSa(
    utr:             String,
    amountInPence:   Int,
    clientJourneyId: ClientJourneyId,
    returnUrl:       Option[String],
    backUrl:         Option[String],
    dueDate:         Option[LocalDate]
) extends SpjRequest

object SpjRequest3psSa {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[SpjRequest3psSa] = Json.format[SpjRequest3psSa]
}

final case class SpjRequest3psVat(
    vrn:             String,
    amountInPence:   Int,
    clientJourneyId: ClientJourneyId,
    returnUrl:       Option[String],
    backUrl:         Option[String],
    dueDate:         Option[LocalDate]
) extends SpjRequest

object SpjRequest3psVat {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[SpjRequest3psVat] = Json.format[SpjRequest3psVat]
}

//todo jake update when we do that ticket.
final case class SpjRequest3psCorporationTax(
    vrn:             String,
    amountInPence:   Int,
    clientJourneyId: ClientJourneyId,
    returnUrl:       Option[String],
    backUrl:         Option[String],
    dueDate:         Option[LocalDate]
) extends SpjRequest

object SpjRequest3psCorporationTax {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[SpjRequest3psCorporationTax] = Json.format[SpjRequest3psCorporationTax]
}

//todo jake update when we do that ticket.
final case class SpjRequest3psEmployersPayAsYouEarn(
    vrn:             String,
    amountInPence:   Int,
    clientJourneyId: ClientJourneyId,
    returnUrl:       Option[String],
    backUrl:         Option[String],
    dueDate:         Option[LocalDate]
) extends SpjRequest

object SpjRequest3psEmployersPayAsYouEarn {
  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  implicit val format: OFormat[SpjRequest3psEmployersPayAsYouEarn] = Json.format[SpjRequest3psEmployersPayAsYouEarn]
}
