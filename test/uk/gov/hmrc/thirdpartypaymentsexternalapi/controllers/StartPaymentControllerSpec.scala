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

package uk.gov.hmrc.thirdpartypaymentsexternalapi.controllers

import play.api.libs.json.{JsObject, Json}
import play.api.mvc.AnyContentAsJson
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import play.mvc.Http.Status
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.TaxRegime.{CorporationTax, EmployersPayAsYouEarn, SelfAssessment, Vat}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.{RedirectUrl, ThirdPartyPayRequest, ThirdPartyPayResponse}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.{AmountInPence, ClientJourneyId, FriendlyName, Reference, TaxRegime, URL}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.stubs.{AuditConnectorStub, PayApiStub}

import java.util.UUID

class StartPaymentControllerSpec extends ItSpec {

  private val startPaymentController = app.injector.instanceOf[StartPaymentController]

  private def testThirdPartyRequest(taxRegime: TaxRegime, reference: Reference, amountInPence: AmountInPence, friendlyName: Option[FriendlyName], backURL: Option[URL]): ThirdPartyPayRequest = ThirdPartyPayRequest(
    taxRegime     = taxRegime,
    reference     = reference,
    amountInPence = amountInPence,
    friendlyName  = friendlyName,
    backURL       = backURL
  )

  private def auditJson(taxRegime: String, clientJourneyId: Option[ClientJourneyId], errorMessages: Option[String]): JsObject = {

    val maybeClientJourneyId: String = clientJourneyId.fold("")(id => s""", "clientJourneyId": "${id.value.toString}" """)
    val maybeErrorMessages: String = errorMessages.fold("")(message => s""", "errorMessages": ["$message"] """)
    val isSuccessful: String = errorMessages.isEmpty.toString

    Json.parse(
      s"""
        |{
        |  "outcome" : {
        |      "isSuccessful" : ${isSuccessful}
        |      ${maybeErrorMessages}
        |    },
        |    "taxRegime" : "${taxRegime}",
        |    "paymentReference" : "1234567895",
        |    "amount" : 1.23
        |    ${maybeClientJourneyId}
        |}
        |""".stripMargin
    ).as[JsObject]
  }

  private val clientJourneyId = ClientJourneyId(UUID.fromString("aef0f31b-3c0f-454b-9d1f-07d549987a96"))
  private val expectedTestThirdPartyPayResponse = ThirdPartyPayResponse(clientJourneyId, RedirectUrl("https://somenext-url.co.uk"))

  private def fakeRequest(
      taxRegime:     TaxRegime,
      reference:     Reference            = Reference("1234567895"),
      amountInPence: AmountInPence        = AmountInPence(123),
      friendlyName:  Option[FriendlyName] = None,
      backURL:       Option[URL]          = None
  ): FakeRequest[AnyContentAsJson] =
    FakeRequest("POST", "/pay").withJsonBody(Json.toJson(testThirdPartyRequest(taxRegime, reference, amountInPence, friendlyName, backURL)))

  "POST /pay" - {

    "return 201 Created when pay-api returns SpjResponse" - {

      "for Self Assessment" in {
        PayApiStub.stubForStartJourneySelfAssessment()
        val result = startPaymentController.pay()(fakeRequest(SelfAssessment))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        AuditConnectorStub.verifyEventAudited("InitiateJourney", auditJson("SelfAssessment", Some(clientJourneyId), None))
        PayApiStub.verifyStartJourneySelfAssessment(count = 1)
      }

      "for Vat" in {
        PayApiStub.stubForStartJourneyVat()
        val result = startPaymentController.pay()(fakeRequest(Vat))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        AuditConnectorStub.verifyEventAudited("InitiateJourney", auditJson("Vat", Some(clientJourneyId), None))
        PayApiStub.verifyStartJourneyVat(count = 1)
      }

      "for Corporation Tax" in {
        PayApiStub.stubForStartJourneyCorporationTax()
        val result = startPaymentController.pay()(fakeRequest(CorporationTax))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        AuditConnectorStub.verifyEventAudited("InitiateJourney", auditJson("CorporationTax", Some(clientJourneyId), None))
        PayApiStub.verifyStartJourneyCorporationTax(count = 1)
      }

      "for Employers Pay As You Earn" in {
        PayApiStub.stubForStartJourneyEmployersPayAsYouEarn()
        val result = startPaymentController.pay()(fakeRequest(EmployersPayAsYouEarn))
        status(result) shouldBe Status.CREATED
        contentAsJson(result) shouldBe Json.toJson(expectedTestThirdPartyPayResponse)
        AuditConnectorStub.verifyEventAudited("InitiateJourney", auditJson("EmployersPayAsYouEarn", Some(clientJourneyId), None))
        PayApiStub.verifyStartJourneyEmployersPayAsYouEarn(count = 1)
      }
    }

    "return an InternalServerError with UpstreamError message when pay-api returns an error" - {

      "for Self Assessment" in {
        PayApiStub.stubForStartJourneySelfAssessment5xx()
        val result = startPaymentController.pay()(fakeRequest(SelfAssessment))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"errors":["Error from upstream."]}""")
        AuditConnectorStub.verifyEventAudited("InitiateJourney", auditJson("SelfAssessment", None, Some("Error from upstream.")))
        PayApiStub.verifyStartJourneySelfAssessment(count = 1)
      }

      "for Vat" in {
        PayApiStub.stubForStartJourneyVat5xx()
        val result = startPaymentController.pay()(fakeRequest(Vat))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"errors":["Error from upstream."]}""")
        AuditConnectorStub.verifyEventAudited("InitiateJourney", auditJson("Vat", None, Some("Error from upstream.")))
        PayApiStub.verifyStartJourneyVat(count = 1)
      }

      "for Corporation Tax" in {
        PayApiStub.stubForStartJourneyCorporationTax5xx()
        val result = startPaymentController.pay()(fakeRequest(CorporationTax))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"errors":["Error from upstream."]}""")
        AuditConnectorStub.verifyEventAudited("InitiateJourney", auditJson("CorporationTax", None, Some("Error from upstream.")))
        PayApiStub.verifyStartJourneyCorporationTax(count = 1)
      }

      "for Employers Pay As You Earn" in {
        PayApiStub.stubForStartJourneyEmployersPayAsYouEarn5xx()
        val result = startPaymentController.pay()(fakeRequest(EmployersPayAsYouEarn))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        contentAsJson(result) shouldBe Json.parse("""{"errors":["Error from upstream."]}""")
        AuditConnectorStub.verifyEventAudited("InitiateJourney", auditJson("EmployersPayAsYouEarn", None, Some("Error from upstream.")))
        PayApiStub.verifyStartJourneyEmployersPayAsYouEarn(count = 1)
      }
    }

    "return a BadRequest with NonJsonBodyError message when body is not valid json" in {
      val result = startPaymentController.pay()(FakeRequest().withBody("somestringthatisn'tjson"))
      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe Json.parse("""{"errors":["Request body was not json"]}""")
    }

    "return an BadRequest with all three mandatory fields in error message when they aren't provided" in {
      val result = startPaymentController.pay()(FakeRequest().withJsonBody(Json.parse("""{"IamValidJson":"butnotmatchingthemodel"}""")))
      // maybeString - List
      status(result) shouldBe Status.BAD_REQUEST
      contentAsJson(result) shouldBe Json.parse("""{
                                                  |  "errors": [
                                                  |    "Mandatory amountInPence field missing.",
                                                  |    "Mandatory taxRegime field missing.",
                                                  |    "Mandatory reference field missing."
                                                  |  ]
                                                  |}""".stripMargin)
    }

    "return a BadRequest with relevant error message when" - {
      "friendly name" - {
        "is too long (more than 40 characters)" in {
          val stringMoreThan40Characters = "IamMoreThan40Characters123456789123456789"
          val result = startPaymentController.pay()(fakeRequest(SelfAssessment, friendlyName = Some(FriendlyName(stringMoreThan40Characters))))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["friendlyName field too long."]}""")
        }

        "contains invalid characters " in {
          val stringContainingInvalidCharacter = "invalidcharinthisstring%"
          val result = startPaymentController.pay()(fakeRequest(SelfAssessment, friendlyName = Some(FriendlyName(stringContainingInvalidCharacter))))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["friendlyName field contains invalid character."]}""")
        }
      }

      "taxRegime" - {
        "is not in the enum" in {
          val regimeNotInEnum = "IamNotARealRegime"
          val result = startPaymentController.pay()(FakeRequest().withJsonBody(Json.parse(s"""{"taxRegime":"$regimeNotInEnum","reference":"someReference","amountInPence":123,"friendlyName":"Test Company","backURL":"https://valid-url.com"}""")))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["Mandatory taxRegime is not in list of acceptable values."]}""")
        }

        "is missing" in {
          val result = startPaymentController.pay()(FakeRequest().withJsonBody(Json.parse(s"""{"reference":"someReference","amountInPence":123,"friendlyName":"Test Company","backURL":"https://valid-url.com"}""")))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["Mandatory taxRegime field missing."]}""")
        }
      }

      "reference" - {
        "is provided, but not enough characters" in {
          val emptyReference = Reference("")
          val result = startPaymentController.pay()(fakeRequest(SelfAssessment, reference = emptyReference))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["Mandatory reference field invalid."]}""")
        }

        "is missing" in {
          val result = startPaymentController.pay()(FakeRequest().withJsonBody(Json.parse(s"""{"taxRegime":"SelfAssessment","amountInPence":123,"friendlyName":"Test Company","backURL":"https://valid-url.com"}""")))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["Mandatory reference field missing."]}""")
        }
      }

      "amountInPence" - {
        "is missing" in {
          val result = startPaymentController.pay()(FakeRequest().withJsonBody(Json.parse(s"""{"taxRegime":"SelfAssessment","reference":"1234567895","friendlyName":"Test Company","backURL":"https://valid-url.com"}""")))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["Mandatory amountInPence field missing."]}""")
        }

        "is less than the minimum allowed value (less than 0)" in {
          val negativeAmount = AmountInPence(-1)
          val result = startPaymentController.pay()(fakeRequest(SelfAssessment, amountInPence = negativeAmount))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["Mandatory amountInPence field must be greater than or equal to 0."]}""")
        }
      }

      "backURL" - {
        "is not a valid url" in {
          val invalidUrl = URL("notavalidurl")
          val result = startPaymentController.pay()(fakeRequest(SelfAssessment, backURL = Some(invalidUrl)))
          status(result) shouldBe Status.BAD_REQUEST
          contentAsJson(result) shouldBe Json.parse("""{"errors":["backURL field must be a valid url if provided."]}""")
        }
      }
    }

  }

}
