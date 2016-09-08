package ee.mcdimus.matewp.command

import ee.mcdimus.matewp.service.FileSystemService
import ee.mcdimus.matewp.service.OperationSystemService
import java.nio.file.Files
import java.nio.file.Paths

class RestoreCommand(val wallpaperName: String) : Command {

  private val fileSystemService: FileSystemService by lazy { FileSystemService() }
  private val opSystemService: OperationSystemService by lazy { OperationSystemService() }

  override fun execute() {
    val configsDirectory = fileSystemService.getConfigsDirectory()
    val configFilePath = configsDirectory.resolve("$wallpaperName.properties")

    if (Files.notExists(configFilePath)) {
      println("wallpaper '$wallpaperName' was not found")
      return
    }

    val properties = fileSystemService.loadProperties(configFilePath)

    val imageFilePath = Paths.get(properties.getProperty("matewp.system.background").trim('\''))

    opSystemService.setAsWallpaper(imageFilePath)
  }

}
