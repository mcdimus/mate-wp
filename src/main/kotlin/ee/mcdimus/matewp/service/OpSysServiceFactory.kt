package ee.mcdimus.matewp.service

/**
 * @author Dmitri Maksimov
 */
object OpSysServiceFactory {

  private enum class OS {
    LINUX, WINDOWS
  }

  fun get(): OpSysService {
    return when (detectOS()) {
      OS.LINUX -> LinuxMateService()
      OS.WINDOWS -> WindowsService()
    }
  }

  private fun detectOS(): OS {
    val osName = System.getProperty("os.name")
    return when (osName) {
      "Windows 7" -> OS.WINDOWS
      "Linux" -> OS.LINUX
      else -> makeGuess(osName)
    }
  }

  private fun  makeGuess(osName: String): OS {
    if (osName.contains("windows", ignoreCase = true)) {
      return OS.WINDOWS
    } else if (osName.contains("linux", ignoreCase = true)) {
      return OS.LINUX
    } else {
      throw IllegalStateException("unsupported operating system: $osName")
    }
  }

}
