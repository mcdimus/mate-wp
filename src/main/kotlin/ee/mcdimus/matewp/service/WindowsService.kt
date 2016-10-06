package ee.mcdimus.matewp.service

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Dmitri Maksimov
 */
class WindowsService : OpSysService {

  override fun setAsWallpaper(filePath: Path) {
    execCommand("cmd", "/c", "REG", "ADD", "\"HKCU\\Control Panel\\Desktop\"", "/v", "Wallpaper", "/t",  "REG_SZ", "/d",  filePath.toAbsolutePath().toString(), "/f")
    // Make the changes effective immediately
    execCommand("cmd", "/c", "RUNDLL32.EXE", "user32.dll,",  "UpdatePerUserSystemParameters")
  }

  override fun getCurrentWallpaper(): Path {
    val pathString = execCommand("cmd", "/c", "REG", "QUERY", "\"HKCU\\Control Panel\\Desktop\"", "/v", "Wallpaper")
        ?.split(Regex("\\s+"))
        ?.last { it.isNotBlank() }

    return Paths.get(pathString)
  }

  private fun execCommand(vararg args: String): String? {
    val processBuilder = ProcessBuilder(*args)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    var output: String? = null
    BufferedReader(InputStreamReader(process.inputStream)).use {
      output = it.readText()
    }
    process.waitFor()
    return output
  }

}