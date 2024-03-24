package eu.maksimov.matewp.core.model

import eu.maksimov.matewp.core.serialization.BingLocalDateSerializer
import eu.maksimov.matewp.core.serialization.BingLocalDateTimeSerializer
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
  private val urlPath: String,
  val copyright: String,
  @SerialName("copyrightlink")
  val copyrightLink: String,
  val title: String,
  private val quiz: String,
) {

  companion object {
    private const val BING_HOST = "https://www.bing.com"
  }

  val url by lazy { BING_HOST + urlPath }
  val quizUrl by lazy { BING_HOST + quiz }

}
