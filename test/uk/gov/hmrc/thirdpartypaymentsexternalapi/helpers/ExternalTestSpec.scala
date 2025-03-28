package uk.gov.hmrc.thirdpartypaymentsexternalapi.helpers

import org.scalatest.prop.TableDrivenPropertyChecks
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.ThirdPartySoftwareFindByClientIdResponse
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.ItSpec
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.testdata.TestData.clientJourneyId

class ExternalTestSpec extends ItSpec with TableDrivenPropertyChecks {

  "paymentJourney" - {
    "when upstream error occurs" - {
      "should return a exception" in {
        val result = ExternalTest.newPaymentJourney(clientJourneyId, "xyz")

        result.failed.futureValue shouldBe a[Exception]
      }

      "should return an exception" in {
        val result = ExternalTest.newPaymentJourney(clientJourneyId, "UPSTREAM_ERROR")

        result.failed.futureValue shouldBe a[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }

    "should return a valid response" - {
        def expectedResponse(paymentStatus: String) =
          ThirdPartySoftwareFindByClientIdResponse(
            clientJourneyId = clientJourneyId,
            taxRegime       = "taxRegime",
            amountInPence   = 123456L,
            paymentStatus   = paymentStatus
          )

      val testCases = Table(
        ("header", "expectedStatus"),
        ("IN_PROGRESS", "InProgress"),
        ("COMPLETED", "Completed"),
        ("FAILED", "Failed"),
      )

      forAll(testCases) { (header, expectedStatus) =>
        s"when header is $header" in {
          val result = await(ExternalTest.newPaymentJourney(clientJourneyId, header))

          result shouldBe expectedResponse(expectedStatus)
        }
      }
    }
  }
}
