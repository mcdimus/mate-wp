package ee.mcdimus.matewp.usecase

import ee.mcdimus.matewp.service.FileSystemService
import org.intellij.lang.annotations.Language
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path

class InstallWallpaper : UseCase<InstallWallpaper.InstallWallpaperCommand, InstallWallpaper.InstallWallpaperResult> {

  companion object {
    @JvmStatic
    private val LOG = LoggerFactory.getLogger(this::class.java.enclosingClass)
  }

  override fun execute(command: InstallWallpaperCommand): InstallWallpaperResult {
    LOG.info("installing wallpaper")
    @Language("Shell Script")
    val scriptBody = """
      #!/bin/bash
      
      if [[ "${'$'}(whoami)" == "root" ]]; then
        for user in ${'$'}(cut -d: -f1,3 /etc/passwd | grep -E ':[0-9]{4}${'$'}' | cut -d: -f1); do
          cp '${command.wallpaperPath.toAbsolutePath()}' "/home/${'$'}user/Pictures/mate-wp"
          chown ${'$'}user:${'$'}user "/home/${'$'}user/Pictures/mate-wp/${command.wallpaperPath.fileName}"
          userId=${'$'}(id -u "${'$'}user")
          sudo -u "${'$'}user" DBUS_SESSION_BUS_ADDRESS="unix:path=/run/user/${'$'}userId/bus" \
            gsettings set org.mate.background picture-filename "/home/${'$'}user/Pictures/mate-wp/${command.wallpaperPath.fileName}"
          sudo -u "${'$'}user" DBUS_SESSION_BUS_ADDRESS="unix:path=/run/user/${'$'}userId/bus" \
            gsettings set org.mate.background picture-options 'stretched'
        done
      else
        PID=${'$'}(pgrep mate-panel)
        export DBUS_SESSION_BUS_ADDRESS=${'$'}(grep -z DBUS_SESSION_BUS_ADDRESS /proc/${'$'}PID/environ | cut -d= -f2-)
      
        gsettings set org.mate.background picture-filename '${command.wallpaperPath.toAbsolutePath()}'
        gsettings set org.mate.background picture-options 'stretched'
      fi
    """.trimIndent()

    FileSystemService().doWithTempScript(scriptBody) {
      execCommand("/bin/bash", it.toAbsolutePath().toString())?.also { output -> LOG.info("script execution output: \n{}", output) }
    }
    return InstallWallpaperResult.Success
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

  data class InstallWallpaperCommand(val wallpaperPath: Path)

  sealed class InstallWallpaperResult {
    data object Success : InstallWallpaperResult()
  }

}
