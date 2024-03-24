package eu.maksimov.matewp.cli

import com.github.ajalt.clikt.core.CliktCommand
import eu.maksimov.matewp.core.usecase.DownloadWallpaper
import eu.maksimov.matewp.core.usecase.FetchWallpaperMetadata
import eu.maksimov.matewp.core.usecase.InstallWallpaper
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

class UpdateCommand : CliktCommand() {

  companion object {
    @JvmStatic
    private val LOG = LoggerFactory.getLogger(this::class.java.enclosingClass)
  }

  private val fetchWallpaperMetadata: FetchWallpaperMetadata by di()
  private val downloadWallpaper: DownloadWallpaper by di()
  private val installWallpaper: InstallWallpaper by di()

  override fun run() {
    val measureTimeMillis = measureTimeMillis {
      println("Update Wallpaper...")
      println("=======================")
      println("* Fetch metadata...")
      when (val fetchWallpaperMetadataResult = fetchWallpaperMetadata.execute(FetchWallpaperMetadata.FetchWallpaperMetadataCommand)) {
        is FetchWallpaperMetadata.FetchWallpaperMetadataResult.Failure -> {
          echo("ERROR: ${fetchWallpaperMetadataResult.message}")
          LOG.error(fetchWallpaperMetadataResult.message, fetchWallpaperMetadataResult.cause)
        }

        is FetchWallpaperMetadata.FetchWallpaperMetadataResult.Success -> {
          println(fetchWallpaperMetadataResult.toPrettyString())

          println()
          println("* Downloading...")
          when (val downloadWallpaperResult =
            downloadWallpaper.execute(DownloadWallpaper.DownloadWallpaperCommand(fetchWallpaperMetadataResult.wallpaperMetadata))) {
            is DownloadWallpaper.DownloadWallpaperResult.Failure -> println("ERROR: ${downloadWallpaperResult}")
            is DownloadWallpaper.DownloadWallpaperResult.Success -> {
              println("[-] ${downloadWallpaperResult.wallpaperPath}")
              println("[-] ${downloadWallpaperResult.metadataPath}")

              println()
              println("* Installing...")
              val installWallpaperResult = installWallpaper.execute(InstallWallpaper.InstallWallpaperCommand(downloadWallpaperResult.wallpaperPath))
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
