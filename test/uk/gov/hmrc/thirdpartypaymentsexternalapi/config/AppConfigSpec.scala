package uk.gov.hmrc.thirdpartypaymentsexternalapi.config

import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.wordspec.AnyWordSpecLike
import play.api.Configuration

class AppConfigSpec extends AnyWordSpecLike {

  "externalTestEnabled" should {
    "return false when the config value is not set" in {
      new AppConfig(Configuration()).externalTestEnabled mustBe false
    }

    "return true when the config value is set to true" in {
      new AppConfig(Configuration("external-test.enabled" -> true)).externalTestEnabled mustBe true
    }

    "return false when the config value is set to false" in {
      new AppConfig(Configuration("external-test.enabled" -> false)).externalTestEnabled mustBe false
    }
  }
}
