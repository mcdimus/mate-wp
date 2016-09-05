package ee.mcdimus.matewp.service

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * @author Dmitri Maksimov
 */
class FileSystemService {

  companion object {
    private const val HOME = "HOME"
  }

  fun getHomeDirectory(): Path {
    val homeDirectoryPath = System.getenv()
        .getOrElse(HOME, { throw IllegalStateException("environment variable $HOME is no defined") })
    return Paths.get(homeDirectoryPath)
  }

  fun getImagesDirectory(): Path {
    val imagesDirectory = getHomeDirectory().resolve("Pictures/mate-wp")
    if (Files.notExists(imagesDirectory)) {
      return Files.createDirectories(imagesDirectory)
    }
    return imagesDirectory
  }

  fun getConfigsDirectory(): Path {
    val configsDirectory = getImagesDirectory().resolve("configs")
    if (Files.notExists(configsDirectory)) {
      return Files.createDirectories(configsDirectory)
    }
    return configsDirectory
  }

  fun saveProperties(propertiesPath: Path, propertyMap: Map<String, String>): Path {
    val properties = Properties()
    for ((key, value) in propertyMap) {
      properties.setProperty(key, value)
    }

    Files.newOutputStream(propertiesPath).use {
      properties.store(it, null)
    }

    return propertiesPath
  }

}
