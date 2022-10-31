package ee.mcdimus.matewp.model

import java.time.LocalDate
import java.time.LocalDateTime

abstract class PhotoOfTheDay {
  abstract val startDate: LocalDate
  abstract val fullStartDate: LocalDateTime
  abstract val endDate: LocalDate
  abstract val url: String
  abstract val urlBase: String
  abstract val copyright: String
  abstract val copyrightLink: String
  abstract val title: String
  abstract val quiz: String
}