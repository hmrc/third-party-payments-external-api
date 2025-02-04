package uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty

import play.api.libs.json.{Format, Json}

final case class FriendlyName(value: String) {


}

object FriendlyName {

  implicit val format: Format[FriendlyName] = Json.valueFormat[FriendlyName]

  private val familyNameRegEx: String = "^[A-Za-z0-9&@£$€¥#.,:;\\s-]{1,40}$"

  def createValid(input: String): Either[String, FriendlyName] = {
    if (input.matches(familyNameRegEx)) Right(FriendlyName(input))
    else Left(s"FriendlyName $input did not pass regex check")
  }

}
