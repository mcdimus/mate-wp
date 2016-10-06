package ee.mcdimus.matewp.service

/**
 * @author Dmitri Maksimov
 */
object OpSysServiceFactory {

  fun get(): OpSysService {
    return LinuxMateService()
  }

}
