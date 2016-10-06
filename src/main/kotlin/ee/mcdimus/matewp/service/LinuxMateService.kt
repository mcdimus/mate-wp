package ee.mcdimus.matewp.service

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Dmitri Maksimov
 */
class LinuxMateService : OpSysService {

  companion object {
    private const val GSETTINGS = "gsettings"
    private const val SET_CMD = "set"
    private const val GET_CMD = "get"
    private const val SCHEMA = "org.mate.background"
    private const val KEY_PICTURE_FILENAME = "picture-filename"
    private const val KEY_PICTURE_OPTIONS = "picture-options"

    enum class PictureOptions {
      WALLPAPER, CENTERED, SCALED, STRETCHED, ZOOM, SPANNED;

      override fun toString() = name.toLowerCase()
    }
  }

  override fun setAsWallpaper(filePath: Path) {
    execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY_PICTURE_FILENAME, "'${filePath.toAbsolutePath()}'")
    execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY_PICTURE_OPTIONS, "'${PictureOptions.STRETCHED}'")
  }

  override fun getCurrentWallpaper(): Path {
    return Paths.get(execCommand(GSETTINGS, GET_CMD, SCHEMA, KEY_PICTURE_FILENAME)!!.trim('\'')).toAbsolutePath()
  }

  private fun execCommand(vararg args: String): String? {
    val processBuilder = ProcessBuilder(*args)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    var value: String? = null
    BufferedReader(InputStreamReader(process.inputStream)).use {
      value = it.readLine()
    }
    process.waitFor()
    return value
  }

}
