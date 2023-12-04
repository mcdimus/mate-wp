package ee.mcdimus.matewp.model

import java.time.LocalDate
import java.time.LocalDateTime

interface PhotoOfTheDay {
  val startDate: LocalDate
  val fullStartDate: LocalDateTime
  val endDate: LocalDate
  val url: String
  val urlBase: String
  val copyright: String
  val copyrightLink: String
  val title: String
  val quiz: String
}
