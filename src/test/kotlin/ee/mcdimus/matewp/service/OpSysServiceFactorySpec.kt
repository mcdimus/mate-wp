package ee.mcdimus.matewp.service

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import junit.framework.Assert.fail
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * @author Dmitri Maksimov
 */
class OpSysServiceFactorySpec: SubjectSpek<OpSysServiceFactory>({

  subject { OpSysServiceFactory }

  describe("running on Linux") {
    it("should return instance of LinuxMateService") {
      // given
      System.setProperty("os.name", "Linux")

      // when
      val result = subject.get()

      // then
      assertTrue(result is LinuxMateService)
    }
  }

  describe("running on any Linux") {
    it("should return instance of LinuxMateService") {
      // given
      System.setProperty("os.name", "SomeLinuxDistro")

      // when
      val result = subject.get()

      // then
      assertTrue(result is LinuxMateService)
    }
  }

  describe("running on Windows 7") {
    it("should return instance of WindowsService") {
      // given
      System.setProperty("os.name", "Windows 7")

      // when
      val result = subject.get()

      // then
      assertTrue(result is WindowsService)
    }
  }

  describe("running on any other windows") {
    it("should return instance of WindowsService") {
      // given
      System.setProperty("os.name", "AnyOtherWindows")

      // when
      val result = subject.get()

      // then
      assertTrue(result is WindowsService)
    }
  }

  describe("running on unsupported OS") {
    it("should throw IllegalStateException") {
      // given
      System.setProperty("os.name", "FreeBSD")

      // when
      try {
        subject.get()
        fail("noe exception was thrown")
      } catch(e: Exception) {
        // then
        assertEquals("unsupported operating system: FreeBSD", e.message)
      }
    }
  }

})
