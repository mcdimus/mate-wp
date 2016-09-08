package ee.mcdimus.matewp.command

import ee.mcdimus.matewp.service.FileSystemService
import ee.mcdimus.matewp.service.OperationSystemService

class SaveCommand(val wallpaperName: String) : Command {

  private val fileSystemService: FileSystemService by lazy { FileSystemService() }
  private val opSystemService: OperationSystemService by lazy { OperationSystemService() }

  override fun execute() {
    val configsDirectory = fileSystemService.getConfigsDirectory()

    val configFilePath = configsDirectory.resolve("$wallpaperName.properties")

    val currentWallpaperPath = opSystemService.getCurrentWallpaper()

    fileSystemService.saveProperties(configFilePath, linkedMapOf(
        "matewp.system.background" to "'$currentWallpaperPath'"
    ))

    println("$currentWallpaperPath saved as '$wallpaperName'")
  }

}
