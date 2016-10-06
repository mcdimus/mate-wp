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
    private const val USER_HOME = "user.home"
  }

  fun getHomeDirectory(): Path {
    val homeDirectoryPath = System.getProperty(USER_HOME)
            ?: throw IllegalStateException("system property $USER_HOME is not defined")

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

  fun loadProperties(propertiesPath: Path): Properties {
    val properties = Properties()
    Files.newInputStream(propertiesPath).use {
      properties.load(it)
    }
    return properties
  }

}
