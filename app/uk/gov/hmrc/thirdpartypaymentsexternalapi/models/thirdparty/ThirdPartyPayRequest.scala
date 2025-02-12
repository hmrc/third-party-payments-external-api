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

import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.payapi.{SpjRequest3psCorporationTax, SpjRequest3psEmployersPayAsYouEarn, SpjRequest3psSa, SpjRequest3psVat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{ClientJourneyId, TaxRegime}

final case class ThirdPartyPayRequest(
    taxRegime:     TaxRegime,
    reference:     String,
    amountInPence: Int,
    friendlyName:  Option[FriendlyName],
    backURL:       String
) {

  def asSaSpjRequest(clientJourneyId: ClientJourneyId): SpjRequest3psSa = SpjRequest3psSa(
    utr             = reference,
    amountInPence   = amountInPence,
    clientJourneyId = clientJourneyId,
    friendlyName    = friendlyName.map(_.value),
    returnUrl       = Some(backURL),
    backUrl         = Some(backURL)
  )
  def asVatSpjRequest(clientJourneyId: ClientJourneyId): SpjRequest3psVat = SpjRequest3psVat(
    vrn             = reference,
    amountInPence   = amountInPence,
    clientJourneyId = clientJourneyId,
    friendlyName    = friendlyName.map(_.value),
    returnUrl       = Some(backURL),
    backUrl         = Some(backURL)
  )
  def asCorporationTaxSpjRequest(clientJourneyId: ClientJourneyId): SpjRequest3psCorporationTax = SpjRequest3psCorporationTax(
    vrn             = reference,
    amountInPence   = amountInPence,
    clientJourneyId = clientJourneyId,
    returnUrl       = Some(backURL),
    backUrl         = Some(backURL)
  )
  def asEmployersPayAsYouEarnSpjRequest(clientJourneyId: ClientJourneyId): SpjRequest3psEmployersPayAsYouEarn = SpjRequest3psEmployersPayAsYouEarn(
    vrn             = reference,
    amountInPence   = amountInPence,
    clientJourneyId = clientJourneyId,
    returnUrl       = Some(backURL),
    backUrl         = Some(backURL)
  )
}

object ThirdPartyPayRequest {

  //  import play.api.libs.functional.syntax._
  //  import play.api.libs.json.Reads._
  import play.api.libs.json._

  //  private val friendlyNameLengthReads: Reads[Option[String]] = (JsPath \ "friendlyName").readNullable[String](
  //    filterNot[String](JsonValidationError("friendly-name.too-long", ThirdPartyResponseErrors.FriendlyNameTooLongError.errorMessage))(_.length > 40)
  //  //    filterNot[String](JsonValidationError(ThirdPartyResponseErrors.FriendlyNameTooLongError.errorMessage))(_.length > 40)
  //  )
  //
  //  private val friendlyNameCharacterReads: Reads[Option[String]] = (JsPath \ "friendlyName").readNullable[String](
  //    pattern("""^[0-9a-zA-Z&@£$€¥#.,:;\s-]+$""".r, ThirdPartyResponseErrors.FriendlyNameInvalidCharacterError.errorMessage)
  //  )

  //  private val friendlyNameCustomReads: Reads[Option[String]] = friendlyNameLengthReads andKeep friendlyNameCharacterReads

  //  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  //  private val customReads: Reads[ThirdPartyPayRequest] = (
  //    (JsPath \ "taxRegime").read[TaxRegime] and
  //    (JsPath \ "reference").read[String] and
  //    (JsPath \ "amountInPence").read[Int] and
  //    friendlyNameCustomReads and
  //    (JsPath \ "backURL").read[String]
  //  )(ThirdPartyPayRequest.apply _)

  //  private val writes: OWrites[ThirdPartyPayRequest] = Json.writes[ThirdPartyPayRequest]

  @SuppressWarnings(Array("org.wartremover.warts.Any")) //  implicit val format: OFormat[ThirdPartyPayRequest] = OFormat[ThirdPartyPayRequest](customReads, writes)
  implicit val format: OFormat[ThirdPartyPayRequest] = Json.format[ThirdPartyPayRequest]
}

final case class FriendlyName(value: String)
object FriendlyName {
  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  val invalidCharacterReads: Reads[FriendlyName] = JsPath.read[FriendlyName](pattern("""^[0-9a-zA-Z&@£$€¥#.,:;\s-]+$""".r, ThirdPartyResponseErrors.FriendlyNameInvalidCharacterError.errorMessage).map(FriendlyName(_)))
  val tooLongReads: Reads[String] = JsPath.read[String](filterNot[String](JsonValidationError(ThirdPartyResponseErrors.FriendlyNameTooLongError.errorMessage))((x: String) => x.length > 40))
  val reads: Reads[FriendlyName] = invalidCharacterReads <~ tooLongReads
  val writes: Writes[FriendlyName] = Json.valueWrites[FriendlyName]
  implicit val friendlyNameFormat: Format[FriendlyName] = Format[FriendlyName](reads, writes)
}
