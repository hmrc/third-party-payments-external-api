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
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.ClientJourneyId
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.AuditConnectorStub

import java.util.UUID

class AuditServiceSpec extends ItSpec {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val auditService: AuditService = app.injector.instanceOf[AuditService]

  "AuditService" - {

    "should successfully send audit when the journey is not successful and Null values" in {
      val isSuccessful                                  = false
      val maybeErrors: Option[Seq[String]]              = Some(Seq("I am an error"))
      val rawJson: Option[JsValue]                      = Some(Json.obj())
      val maybeClientJourneyId: Option[ClientJourneyId] = None

      auditService.auditInitiateJourneyResult(isSuccessful, maybeErrors, rawJson, maybeClientJourneyId)

      AuditConnectorStub.verifyEventAudited(
        "InitiateJourney",
        Json
          .parse(
            s"""
             |{
             |  "outcome" : {
             |      "isSuccessful" : ${isSuccessful.toString},
             |      "errorMessages": ["I am an error"]
             |    }
             |}
             |""".stripMargin
          )
          .as[JsObject]
      )
    }

    "should successfully send audit when the journey is successful" in {
      val isSuccessful                                  = true
      val maybeErrors: Option[Seq[String]]              = None
      val rawJson: Option[JsValue]                      = Some(
        Json.obj(
          "reference"     -> "abcd",
          "amountInPence" -> 123,
          "friendlyName"  -> "Test Company",
          "taxRegime"     -> "SelfAssessment"
        )
      )
      val maybeClientJourneyId: Option[ClientJourneyId] =
        Some(ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96")))

      auditService.auditInitiateJourneyResult(isSuccessful, maybeErrors, rawJson, maybeClientJourneyId)

      AuditConnectorStub.verifyEventAudited(
        "InitiateJourney",
        Json
          .parse(
            s"""
             |{
             |  "outcome" : {
             |      "isSuccessful" : ${isSuccessful.toString}
             |    },
             |    "paymentReference" : "abcd",
             |    "amount" : 1.23,
             |    "taxRegime" : "SelfAssessment",
             |    "originOfRequest" : "Test Company",
             |    "clientJourneyId" : "aef0f31b-3c0f-454b-9d1f-07d549987a96"
             |}
             |""".stripMargin
          )
          .as[JsObject]
      )

    }

  }

}
