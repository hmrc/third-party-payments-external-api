package uk.gov.hmrc.thirdpartypaymentsexternalapi.models

import org.scalatest.AppendedClues.convertToClueful
import play.api.libs.json.{JsString, JsSuccess, Json}
import uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty.FriendlyName
import uk.gov.hmrc.thirdpartypaymentsexternalapi.testsupport.UnitSpec

class FriendlyNameSpec extends UnitSpec {

  "The FriendlyName JSON format" - {

    "serialize and deserialize correctly using a plain string JSON format" in {
      val friendlyName = FriendlyName("Test Company")
      val jsonString = Json.toJson(friendlyName.value)
      jsonString shouldEqual JsString("Test Company")

      val json = Json.parse("\"Test Company\"")
      val deserializedName = json.validate[FriendlyName]
      deserializedName shouldEqual JsSuccess(friendlyName)
    }
  }

  "The FriendlyName creation method" - {
    val validFriendlyNames = Seq(
      "Test Company", "TestCompany", "Test Comp@ny",
      "Test Comp&ny", "T£st Company", "Test Comp$ny",
      "T€st Company", "Test Compan¥", "Test Comp#ny",
      "Test.Company", "Test, Company", "Test: Company",
      "Test; Company"
    )
    val invalidFriendlyNames = Seq[(String, String)](
      ("!est ?ompany", "Contains invalid characters")
    )

    for (input <- validFriendlyNames)
      s"should return a valid FriendlyName when input:[$input] matches the regex" in {
        FriendlyName.createValid(input) shouldBe Right(FriendlyName(input))
      }

    for (input <- invalidFriendlyNames)
      s"should return an error message when input:[$input] does not match the regex" in {
        FriendlyName.createValid(input._1) shouldBe Left(s"FriendlyName ${input._1} did not pass regex check") withClue input._2
      }
  }

}
