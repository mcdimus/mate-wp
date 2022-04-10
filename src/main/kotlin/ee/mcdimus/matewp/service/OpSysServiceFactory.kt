package ee.mcdimus.matewp.service

/**
 * @author Dmitri Maksimov
 */
object OpSysServiceFactory {

  private enum class OS {
    LINUX, WINDOWS
  }

  fun get() = when (detectOS()) {
    OS.LINUX -> LinuxMateService()
    OS.WINDOWS -> WindowsService()
  }

  private fun detectOS() = when (val osName = System.getProperty("os.name")) {
    "Windows 7" -> OS.WINDOWS
    "Linux" -> OS.LINUX
    else -> makeGuess(osName)
  }

  private fun makeGuess(osName: String) = when {
    osName.contains("windows", ignoreCase = true) -> OS.WINDOWS
    osName.contains("linux", ignoreCase = true) -> OS.LINUX
    else -> throw IllegalStateException("unsupported operating system: $osName")
  }

}
