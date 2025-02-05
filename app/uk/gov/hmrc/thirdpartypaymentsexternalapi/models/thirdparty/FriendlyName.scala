package uk.gov.hmrc.thirdpartypaymentsexternalapi.models.thirdparty

import play.api.libs.json.{Format, Json}

final case class FriendlyName(value: String)

object FriendlyName {

  private val familyNameRegex: String = "^[A-Za-z0-9&@£$€¥#.,:;\\s-]{1,40}$"

  implicit val format: Format[FriendlyName] = Json.valueFormat[FriendlyName]

  def isValid(friendlyName: String): Either[String, FriendlyName] = {
    if (friendlyName.matches(familyNameRegex)) Right(FriendlyName(friendlyName))
    else Left(s"FriendlyName $friendlyName did not pass regex check")
  }

}

