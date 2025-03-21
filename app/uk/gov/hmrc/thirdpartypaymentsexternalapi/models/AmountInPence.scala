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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.models

import play.api.libs.json.Reads._
import play.api.libs.json._

final case class AmountInPence(value: Long) extends AnyVal

object AmountInPence {
  private val negativeAmountReads: Reads[AmountInPence] = JsPath.read[AmountInPence](filterNot[Long](JsonValidationError("error.minimumValue"))(_ < 0L).map(AmountInPence(_)))

  val reads: Reads[AmountInPence] = negativeAmountReads
  val writes: Writes[AmountInPence] = Json.valueWrites[AmountInPence]
  implicit val amountInPenceFormat: Format[AmountInPence] = Format[AmountInPence](reads, writes)
}
