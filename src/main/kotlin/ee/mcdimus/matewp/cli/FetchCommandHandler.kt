package ee.mcdimus.matewp.cli

import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataCommand
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataResult.Failure
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataResult.Success

class FetchCommandHandler(
  private val fetchWallpaperMetadata: FetchWallpaperMetadata
) : CommandHandler {

  companion object {
    const val KEY = "FETCH"
  }

  override fun handle() {
    when (val res = fetchWallpaperMetadata.execute(FetchWallpaperMetadataCommand)) {
      is Success -> {
        println("Photo of the day (${res.wallpaperMetadata.startDate})")
        println("==========================================")
        with(res.wallpaperMetadata) {
          println("\t$title")
          println("\t$copyright")
          println("\tWallpaper: $url")
          println("\tQuiz: $quizUrl")
        }
      }
      is Failure -> {
        println("Command failed:")
        println("\t${res.message}")
      }
    }
  }

}
