package ee.mcdimus.matewp.model

import ee.mcdimus.matewp.serialization.BingLocalDateSerializer
import ee.mcdimus.matewp.serialization.BingLocalDateTimeSerializer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WallpaperMetadata(
  @SerialName("startdate")
  @Serializable(BingLocalDateSerializer::class)
  val startDate: LocalDate,
  @SerialName("fullstartdate")
  @Serializable(BingLocalDateTimeSerializer::class)
  val fullStartDate: LocalDateTime,
  @SerialName("enddate")
  @Serializable(BingLocalDateSerializer::class)
  val endDate: LocalDate,
  @SerialName("url")
  private val _url: String,
  @SerialName("urlbase")
  private val urlBase: String,
  val copyright: String,
  @SerialName("copyrightlink")
  val copyrightLink: String,
  val title: String,
  @SerialName("quiz")
  private val _quiz: String,
) {

  companion object {
    private const val BING_HOST = "https://www.bing.com"
  }

  val url by lazy { BING_HOST + _url }
  val quizUrl by lazy { BING_HOST + _quiz }

}
