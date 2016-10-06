package ee.mcdimus.matewp.service

/**
 * @author Dmitri Maksimov
 */
object OpSysServiceFactory {

  enum class OS {
    LINUX, WINDOWS
  }

  fun get(): OpSysService = when (detectOS()) {
    OS.LINUX -> LinuxMateService()
    OS.WINDOWS -> WindowsService()
  }

  private fun detectOS() = when (System.getProperty("os.name")) {
    "Windows 7" -> OS.WINDOWS
    else -> OS.LINUX
  }

}
