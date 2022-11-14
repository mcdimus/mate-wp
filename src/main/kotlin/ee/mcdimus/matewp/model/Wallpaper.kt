package ee.mcdimus.matewp.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Wallpaper(
  val name: String,
  val lastModified: LocalDateTime,
  val path: String,
  val metadataPath: String,
  override val startDate: LocalDate,
  override val fullStartDate: LocalDateTime,
  override val endDate: LocalDate,
  override val url: String,
  override val urlBase: String,
  override val copyright: String,
  override val copyrightLink: String,
  override val title: String,
  override val quiz: String
) : PhotoOfTheDay
