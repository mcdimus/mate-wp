package ee.mcdimus.matewp.service

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Dmitri Maksimov
 */
class OperationSystemService {

  companion object {
    private const val GSETTINGS = "gsettings"
    private const val SET_CMD = "set"
    private const val GET_CMD = "get"
    private const val SCHEMA = "org.mate.background"
    private const val KEY = "picture-filename"
  }

  fun setAsWallpaper(filePath: Path) {
    // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/___.jpg''
    execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, "'${filePath.toAbsolutePath()}'")
  }

  fun getCurrentWallpaper(): Path {
    return Paths.get(execCommand(GSETTINGS, GET_CMD, SCHEMA, KEY)!!.trim('\'')).toAbsolutePath()
  }

  private fun execCommand(vararg args: String): String? {
    val processBuilder = ProcessBuilder(*args)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    var value: String? = null
    BufferedReader(InputStreamReader(process.inputStream)).use { `in` ->
      value = `in`.readLine()
    }
    process.waitFor()
    return value
  }

}
