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

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

final case class FriendlyName(value: String) extends AnyVal

object FriendlyName {
  private val invalidCharacterReads: Reads[FriendlyName] = JsPath.read[FriendlyName](pattern("""^[0-9a-zA-Z&@£$€¥#.,:;\s-]+$""".r, "friendlyName.error.invalidCharacters").map(FriendlyName(_)))
  private val tooLongReads: Reads[String] = JsPath.read[String](filterNot[String](JsonValidationError("friendlyName.error.maxLength"))(_.length > 40))

  val reads: Reads[FriendlyName] = invalidCharacterReads <~ tooLongReads
  val writes: Writes[FriendlyName] = Json.valueWrites[FriendlyName]
  implicit val friendlyNameFormat: Format[FriendlyName] = Format[FriendlyName](reads, writes)
}
