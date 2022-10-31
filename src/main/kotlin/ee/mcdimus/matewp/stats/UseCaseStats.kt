package ee.mcdimus.matewp.stats

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class UseCaseStats(
  private val startTimestamp: Instant? = null,
  private val stopTimestamp: Instant? = null,
  private val params: Map<String, Any> = emptyMap()
) {

  companion object {
    val EMPTY = UseCaseStats()
  }

  override fun toString() = buildString {
    append(params.entries.joinToString(separator = "\n", prefix = "\t[-] ", transform = { it -> "${it.key}: ${it.value}" }))
    if (startTimestamp != null && stopTimestamp != null) {
      append("\t[-] time elapsed: ")
      append(stopTimestamp!!.minus(startTimestamp!!).inWholeMilliseconds)
      appendLine("ms")
    }
  }

}