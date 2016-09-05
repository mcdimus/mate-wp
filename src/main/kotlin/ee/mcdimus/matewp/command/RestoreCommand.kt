package ee.mcdimus.matewp.command

import ee.mcdimus.matewp.service.FileSystemService
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

class RestoreCommand(val wallpaperName: String) : Command {

  companion object {
    private const val GSETTINGS = "gsettings"
    private const val SET_CMD = "set"
    private const val GET_CMD = "get"
    private const val SCHEMA = "org.mate.background"
    private const val KEY = "picture-filename"
  }

  private val fileSystemService: FileSystemService by lazy { FileSystemService() }

  override fun execute() {
    val configsDirectory = fileSystemService.getConfigsDirectory()
    val configFilePath = configsDirectory.resolve("$wallpaperName.properties")

    if (Files.notExists(configFilePath)) {
      println("wallpaper '$wallpaperName' was not found")
      return
    }

    val properties = fileSystemService.loadProperties(configFilePath)

    val imageFilePath = Paths.get(properties.getProperty("matewp.system.background").trim('\''))

    // change wallpaper
    try {
      // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
      execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFilePath.toAbsolutePath()))
    } catch (ex: IOException) {
      System.err.println(ex.message)
    } catch (ex: InterruptedException) {
      System.err.println(ex.message)
    }
  }

  @Throws(IOException::class, InterruptedException::class)
  private fun execCommand(vararg args: String): String? {
    val processBuilder = ProcessBuilder(*args)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    var value: String? = null
    BufferedReader(InputStreamReader(process.inputStream)).use { `in` ->
      value = `in`.readLine()
      //        while ((line = in.readLine()) != null) {
      //          value += line + "\n";
      //        }
    }
    process.waitFor()
    return value
  }

}
