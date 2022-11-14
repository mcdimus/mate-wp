package ee.mcdimus.matewp.usecase

import ee.mcdimus.matewp.model.WallpaperMetadata
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataCommand
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataResult
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataResult.Failure
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataResult.Success
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.temporal.ChronoUnit

class FetchWallpaperMetadata(
  private val httpClient: HttpClient
) : UseCase<FetchWallpaperMetadataCommand, FetchWallpaperMetadataResult> {

  companion object {
    @JvmStatic
    private val LOG = LoggerFactory.getLogger(this::class.java.enclosingClass)

    private const val BING_PHOTO_OF_THE_DAY_URL = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US"
    private const val OK = 200
  }

  private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

  override fun execute(command: FetchWallpaperMetadataCommand): FetchWallpaperMetadataResult {
    LOG.info("FetchWallpaperMetadata")
    val request: HttpRequest = HttpRequest.newBuilder()
      .uri(URI(BING_PHOTO_OF_THE_DAY_URL))
      .timeout(Duration.of(1, ChronoUnit.SECONDS))
      .GET()
      .build()

    return runCatching { httpClient.send(request, HttpResponse.BodyHandlers.ofString()) }
      .mapCatching { if (it.statusCode() == OK) it.body() else error("status ${it.statusCode()}") }
      .mapCatching { json.parseToJsonElement(it) }
      .mapCatching { it.jsonObject["images"]?.jsonArray?.get(0) ?: throw IllegalArgumentException("unexpected JSON structure:\n${json.encodeToString(it)}") }
      .mapCatching { json.decodeFromJsonElement<WallpaperMetadata>(it) }
      .fold(
        onSuccess = { Success(wallpaperMetadata = it) },
        onFailure = { Failure(message = "failed to fetch: ${it.message}") }
      ).also { LOG.info(it.toString()) }
  }

  object FetchWallpaperMetadataCommand

  sealed class FetchWallpaperMetadataResult {
    data class Success(val wallpaperMetadata: WallpaperMetadata) : FetchWallpaperMetadataResult() {
      fun toPrettyString() = """
        [-] Title:     ${wallpaperMetadata.title}
        [-] Copyright: ${wallpaperMetadata.copyright}
        [-] URL:       ${wallpaperMetadata.url}
        [-] Quiz URL:  ${wallpaperMetadata.quizUrl}
      """.trimIndent()
    }

    data class Failure(val message: String) : FetchWallpaperMetadataResult()
  }

}
