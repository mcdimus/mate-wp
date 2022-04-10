package ee.mcdimus.matewp.command

import ee.mcdimus.matewp.service.FileSystemService
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * @author Dmitri Maksimov
 */
class ListCommand : Command {

  private val fileSystemService: FileSystemService by lazy { FileSystemService() }

  override fun execute() {
    val configsDirectory = fileSystemService.getConfigsDirectory()

    println("Currently saved wallpapers:")

    Files.list(configsDirectory)
      .sorted { a, b -> Files.getLastModifiedTime(a).compareTo(Files.getLastModifiedTime(b)) }
      .map { String.format(Locale.US, "%-20s saved at %s", "'${it.fileName.toString().removeSuffix(".properties")}'", getLastModifiedTime(it)) }
      .forEach { println("\t[*] $it") }
  }

  private fun getLastModifiedTime(it: Path) = SimpleDateFormat("dd.MM.yyyy HH:mm").format(Files.getLastModifiedTime(it).toMillis())

}
