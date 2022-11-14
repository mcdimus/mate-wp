package ee.mcdimus.matewp.cli

import ee.mcdimus.matewp.usecase.DownloadWallpaper
import ee.mcdimus.matewp.usecase.DownloadWallpaper.DownloadWallpaperCommand
import ee.mcdimus.matewp.usecase.DownloadWallpaper.DownloadWallpaperResult
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataCommand
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataResult
import ee.mcdimus.matewp.usecase.InstallWallpaper
import ee.mcdimus.matewp.usecase.InstallWallpaper.InstallWallpaperCommand
import kotlin.system.measureTimeMillis

class UpdateCommandHandler(
  private val fetchWallpaperMetadata: FetchWallpaperMetadata,
  private val downloadWallpaper: DownloadWallpaper,
  private val installWallpaper: InstallWallpaper
) : CommandHandler {

  companion object {
    const val KEY = "UPDATE"
  }

  override fun handle() {
    val measureTimeMillis = measureTimeMillis {
      println("Update Wallpaper...")
      println("=======================")
      println("* Fetch metadata...")
      when (val fetchWallpaperMetadataResult = fetchWallpaperMetadata.execute(FetchWallpaperMetadataCommand)) {
        is FetchWallpaperMetadataResult.Failure -> println("ERROR: ${fetchWallpaperMetadataResult.message}")
        is FetchWallpaperMetadataResult.Success -> {
          println(fetchWallpaperMetadataResult.toPrettyString())

          println()
          println("* Downloading...")
          when (val downloadWallpaperResult = downloadWallpaper.execute(DownloadWallpaperCommand(fetchWallpaperMetadataResult.wallpaperMetadata))) {
            is DownloadWallpaperResult.Failure -> println("ERROR: ${downloadWallpaperResult}")
            is DownloadWallpaperResult.Success -> {
              println("[-] ${downloadWallpaperResult.wallpaperPath}")
              println("[-] ${downloadWallpaperResult.metadataPath}")

              println()
              println("* Installing...")
              val installWallpaperResult = installWallpaper.execute(InstallWallpaperCommand(downloadWallpaperResult.wallpaperPath))
              when (installWallpaperResult) {
                InstallWallpaper.InstallWallpaperResult.Success -> println("$installWallpaperResult")
              }
            }
          }
        }
      }
    }

    println("-----------------------")
    println("Total time elapsed: ${measureTimeMillis}ms")
    println("=======================")
  }

}
