package ee.mcdimus.matewp.command

import ee.mcdimus.matewp.service.FileSystemService
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class SaveCommand(val wallpaperName: String) : Command {

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

          val result = execCommand(GSETTINGS, GET_CMD, SCHEMA, KEY)!!

    fileSystemService.saveProperties(configFilePath, linkedMapOf(
        Pair("matewp.system.background", result)
    ))

    println("$result saved as '$wallpaperName'")
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
