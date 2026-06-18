/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.helpers

import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Result, Results}
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{ThirdPartyResponseError, ThirdPartyResponseErrors}

object ThirdPartyErrorResponseBuilder {

  def fromThirdPartyErrors(errors: Seq[ThirdPartyResponseError]): Result = {
    if (containsInternalServerError(errors)) {
      Results.InternalServerError(createErrorResponse(errors.map(_.errorMessage)))
    } else {
      Results.BadRequest(createErrorResponse(errors.map(_.errorMessage)))
    }
  }

  def fromUpstreamError(error: UpstreamErrorResponse): Result = {
    error.statusCode match {
      case 404 =>
        Results.NotFound(createSingleErrorResponse(error.message))
      case _   =>
        Results.InternalServerError(createSingleErrorResponse(error.message))
    }
  }

  private def containsInternalServerError(errors: Seq[ThirdPartyResponseError]): Boolean =
    errors.exists {
      case ThirdPartyResponseErrors.UnexpectedError(_) => true
      case ThirdPartyResponseErrors.UpstreamError      => true
      case _                                           => false
    }

  private def createErrorResponse(messages: Seq[String]): JsObject =
    Json.obj("errors" -> Json.toJson(messages))

  private def createSingleErrorResponse(message: String): JsObject =
    Json.obj("error" -> message)
}
