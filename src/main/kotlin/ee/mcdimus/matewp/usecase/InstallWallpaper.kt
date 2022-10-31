package ee.mcdimus.matewp.usecase

import ee.mcdimus.matewp.service.FileSystemService
import org.intellij.lang.annotations.Language
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path

class InstallWallpaper : UseCase<InstallWallpaper.InstallWallpaperCommand, InstallWallpaper.InstallWallpaperResult> {

  override fun execute(command: InstallWallpaperCommand): InstallWallpaperResult {
    @Language("Shell Script")
    val scriptBody = """
      #!/bin/bash
    
      PID=$(pgrep mate-panel)
      export DBUS_SESSION_BUS_ADDRESS=$(grep -z DBUS_SESSION_BUS_ADDRESS /proc/${"$"}PID/environ|cut -d= -f2-)
      
      gsettings set org.mate.background picture-filename '${command.wallpaperPath.toAbsolutePath()}'
      gsettings set org.mate.background picture-options 'STRETCHED'
    """.trimIndent()

    FileSystemService().doWithTempScript(scriptBody) {
      execCommand("/bin/sh", it.toAbsolutePath().toString())
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
    object Success : InstallWallpaperResult()
  }
}