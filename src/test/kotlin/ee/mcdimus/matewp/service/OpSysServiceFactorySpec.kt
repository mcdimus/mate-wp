package ee.mcdimus.matewp.service

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.jupiter.api.assertThrows
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

/**
 * @author Dmitri Maksimov
 */
object OpSysServiceFactorySpec : Spek({

    Feature("OpSysServiceFactory") {
        val subject by memoized { OpSysServiceFactory }

        Scenario("running on Linux") {
            Given("os.name == Linux") {
                System.setProperty("os.name", "Linux")
            }
            Then("should return instance of LinuxMateService") {
                assertTrue(subject.get() is LinuxMateService)
            }
        }

        Scenario("guessing that running on any Linux") {
            Given("os.name == Linux") {
                System.setProperty("os.name", "SomeLinuxDistro")
            }
            Then("should return instance of LinuxMateService") {
                assertTrue(subject.get() is LinuxMateService)
            }
        }

        Scenario("running on Windows 7") {
            Given("os.name == Windows 7") {
                System.setProperty("os.name", "Windows 7")
            }
            Then("should return instance of LinuxMateService") {
                assertTrue(subject.get() is WindowsService)
            }
        }

        Scenario("guessing that running on any Linux") {
            Given("os.name == Linux") {
                System.setProperty("os.name", "AnyOtherWindows")
            }
            Then("should return instance of WindowsService") {
                assertTrue(subject.get() is WindowsService)
            }
        }

        Scenario("running on unsupported OS") {
            Given("os.name == FreeBSD") {
                System.setProperty("os.name", "FreeBSD")
            }
            Then("should throw IllegalStateException") {
                assertThrows<IllegalStateException> { subject.get() }.also {
                    assertEquals("unsupported operating system: FreeBSD", it.message)
                }
            }
        }
    }

})
