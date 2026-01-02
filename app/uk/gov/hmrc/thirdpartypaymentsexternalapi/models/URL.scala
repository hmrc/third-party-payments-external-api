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

import scala.util.Try

final case class URL(value: String) extends AnyVal

object URL {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private val validUrlReads: Reads[URL] =
    JsPath
      .read[String](
        using filterNot[String](JsonValidationError("error.invalidUrl")) { (url: String) => Try(new java.net.URI(url).toURL).isFailure }
      )
      .map(URL(_))

  val reads: Reads[URL]               = validUrlReads
  val writes: Writes[URL]             = Json.valueWrites[URL]
  given urlFormat: Format[URL] = Format[URL](reads, writes)
}
