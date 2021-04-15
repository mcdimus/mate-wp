package ee.mcdimus.matewp.service

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Dmitri Maksimov
 */
class LinuxMateService : OpSysService {

  private enum class PictureOptions {
    WALLPAPER, CENTERED, SCALED, STRETCHED, ZOOM, SPANNED;

    override fun toString() = name.toLowerCase()
  }

  override fun setAsWallpaper(filePath: Path) {
    val scriptBody = "#!/bin/bash\n\n" +
        "PID=\$(pgrep mate-panel)\n" +
        "export DBUS_SESSION_BUS_ADDRESS=\$(grep -z DBUS_SESSION_BUS_ADDRESS /proc/\$PID/environ|cut -d= -f2-)\n\n" +
        "gsettings set org.mate.background picture-filename '${filePath.toAbsolutePath()}'\n" +
        "gsettings set org.mate.background picture-options '${PictureOptions.STRETCHED}'\n"

    FileSystemService().doWithTempScript(scriptBody) {
      execCommand("/bin/sh", it.toAbsolutePath().toString())
    }
  }

  override fun getCurrentWallpaper(): Path {
    val scriptBody = "#!/bin/bash\n\n" +
        "PID=\$(pgrep mate-panel)\n" +
        "export DBUS_SESSION_BUS_ADDRESS=\$(grep -z DBUS_SESSION_BUS_ADDRESS /proc/\$PID/environ|cut -d= -f2-)\n\n" +
        "gsettings get org.mate.background picture-filename\n"

    return FileSystemService().doWithTempScript(scriptBody) {
      Paths.get(execCommand("/bin/sh", it.toAbsolutePath().toString())?.trim('\'')).toAbsolutePath()
    }
  }

  private fun execCommand(vararg args: String): String? {
    val processBuilder = ProcessBuilder(*args)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    var value: String?
    BufferedReader(InputStreamReader(process.inputStream)).use {
      value = it.readLine()
    }
    process.waitFor()
    return value
  }

}
