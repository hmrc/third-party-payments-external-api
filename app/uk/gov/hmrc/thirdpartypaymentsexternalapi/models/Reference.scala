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

final case class Reference(value: String) extends AnyVal

object Reference {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private val tooShortReads: Reads[Reference] = JsPath.read[Reference](
    using filterNot[String](JsonValidationError("error.minLength"))(s => s.isBlank || s.length < 1).map(Reference(_))
  )

  val reads: Reads[Reference]                     = tooShortReads
  val writes: Writes[Reference]                   = Json.valueWrites[Reference]
  implicit val referenceFormat: Format[Reference] = Format[Reference](reads, writes)
}
