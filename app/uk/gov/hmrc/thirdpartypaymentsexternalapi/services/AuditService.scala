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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.services

import play.api.libs.json._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.audit.{InitiateJourneyAuditDetail, Outcome}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AuditService @Inject() (auditConnector: AuditConnector)(implicit ec: ExecutionContext) {

  def auditInitiateJourneyResult(
    isSuccessful:         Boolean,
    maybeErrors:          Option[Seq[String]],
    rawJson:              Option[JsValue],
    maybeClientJourneyId: Option[ClientJourneyId]
  )(implicit hc: HeaderCarrier): Unit = {
    val auditDetails = toInitiateAuditDetail(
      isSuccessful = isSuccessful,
      maybeErrors = maybeErrors,
      rawJson = rawJson,
      maybeClientJourneyId = maybeClientJourneyId
    )
    auditConnector.sendExplicitAudit[InitiateJourneyAuditDetail](auditDetails.auditType, auditDetails)
  }

  private def toInitiateAuditDetail(
    isSuccessful:         Boolean,
    maybeErrors:          Option[Seq[String]],
    rawJson:              Option[JsValue],
    maybeClientJourneyId: Option[ClientJourneyId]
  ): InitiateJourneyAuditDetail = {

    val referenceJsPath: JsPath     = __ \ "reference"
    val amountInPenceJsPath: JsPath = __ \ "amountInPence"
    val friendlyNameJsPath: JsPath  = __ \ "friendlyName"
    val taxRegimeJsPath: JsPath     = __ \ "taxRegime"

    val maybeTaxRegime        = rawJson.flatMap(json => maybeValueFromJson[JsString](json, taxRegimeJsPath).map(_.value))
    val maybePaymentReference =
      rawJson.flatMap(json => maybeValueFromJson[JsString](json, referenceJsPath).map(_.value))
    val maybeAmount           = rawJson.flatMap(json => maybeValueFromJson[JsNumber](json, amountInPenceJsPath).map(_.value))
    val maybeOriginOfRequest  =
      rawJson.flatMap(json => maybeValueFromJson[JsString](json, friendlyNameJsPath).map(_.value))

    InitiateJourneyAuditDetail(
      outcome = Outcome(isSuccessful, maybeErrors),
      taxRegime = maybeTaxRegime,
      paymentReference = maybePaymentReference,
      amount = maybeAmount.map(_./(100)),
      originOfRequest = maybeOriginOfRequest,
      clientJourneyId = maybeClientJourneyId.map(_.value.toString)
    )

  }

  private def maybeValueFromJson[A <: JsValue](jsValue: JsValue, pathExtractor: JsPath)(using reads: Reads[A]): Option[A] =
    jsValue.validate(using pathExtractor.json.pick[A]).asOpt

}
