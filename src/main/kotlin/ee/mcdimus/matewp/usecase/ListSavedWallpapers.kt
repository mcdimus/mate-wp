package ee.mcdimus.matewp.usecase

import ee.mcdimus.matewp.model.Wallpaper
import ee.mcdimus.matewp.service.FileSystemService
import ee.mcdimus.matewp.usecase.ListSavedWallpapers.ListSavedWallpapersCommand
import ee.mcdimus.matewp.usecase.ListSavedWallpapers.ListSavedWallpapersResult
import ee.mcdimus.matewp.usecase.ListSavedWallpapers.ListSavedWallpapersResult.Success
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Properties
import java.util.stream.Collectors.toList
import kotlin.io.path.bufferedReader

class ListSavedWallpapers(
  private val fileSystemService: FileSystemService
) : UseCase<ListSavedWallpapersCommand, ListSavedWallpapersResult> {

  override fun execute(command: ListSavedWallpapersCommand): ListSavedWallpapersResult {
    val configsDirectory = fileSystemService.getConfigsDirectory()

    println("Currently saved wallpapers:")

    val wallpapers = Files.list(configsDirectory)
      .sorted { a, b -> Files.getLastModifiedTime(a).compareTo(Files.getLastModifiedTime(b)) }
      .map { it to it.bufferedReader().use { buf -> Properties().also { properties -> properties.load(buf) } } }
      .map { (file, properties) -> properties.toWallpaper(file) }
      .collect(toList())
    return Success(wallpapers)
  }

  private fun Properties.toWallpaper(file: Path) = Wallpaper(
    name = this["urlBase"].toString().substringAfter('='),
    lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(file).toInstant(), ZoneId.systemDefault()),
    path = file.toAbsolutePath().toString(),
    metadataPath = file.toAbsolutePath().toString(),
    startDate = LocalDate.parse(this["startDate"].toString()),
    fullStartDate = LocalDateTime.parse(this["fullStartDate"].toString()),
    endDate = LocalDate.parse(this["endDate"].toString()),
    url = this["url"].toString(),
    urlBase = this["urlBase"].toString(),
    copyright = this["copyright"].toString(),
    copyrightLink = this["copyrightLink"].toString(),
    title = this["title"].toString(),
    quiz = this["quiz"].toString()
  )

  object ListSavedWallpapersCommand

  sealed class ListSavedWallpapersResult {
    data class Success(val wallpapers: List<Wallpaper>) : ListSavedWallpapersResult()
  }

}



