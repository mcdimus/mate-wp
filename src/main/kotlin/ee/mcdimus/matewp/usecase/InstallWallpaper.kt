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
    
      PID=$(pgrep mate-panel)
      export DBUS_SESSION_BUS_ADDRESS=$(grep -z DBUS_SESSION_BUS_ADDRESS /proc/${"$"}PID/environ|cut -d= -f2-)
      
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

  data class InstallWallpaperCommand(val wallpaperPath: Path) {

  }

  sealed class InstallWallpaperResult {
    data object Success : InstallWallpaperResult()
  }
}