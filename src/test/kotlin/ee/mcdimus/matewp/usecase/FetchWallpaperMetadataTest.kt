package ee.mcdimus.matewp.usecase

import ee.mcdimus.matewp.model.WallpaperMetadata
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataCommand
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataResult
import ee.mcdimus.matewp.usecase.FetchWallpaperMetadata.FetchWallpaperMetadataResult.Failure
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import org.intellij.lang.annotations.Language
import java.net.http.HttpClient
import java.net.http.HttpResponse
import java.net.http.HttpTimeoutException

class FetchWallpaperMetadataTest : BehaviorSpec({

  val httpClient = mockk<HttpClient>()
  val useCase = FetchWallpaperMetadata(httpClient)

  Given("FetchWallpaperMetadata use-case") {
    val command = FetchWallpaperMetadataCommand

    When("is executed and resource is not found") {
      every { httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockk<HttpResponse<String>> {
        every { statusCode() } returns 404
      }
      val result = useCase.execute(command)

      Then("failure is returned") {
        result shouldBe Failure(message = "failed to fetch data: status 404")
      }
    }

    When("is executed and timeout is reached") {
      every { httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } throws HttpTimeoutException("timeout reached")
      val result = useCase.execute(command)

      Then("failure is returned") {
        result shouldBe Failure(message = "failed to fetch data: timeout reached")
      }
    }

    When("is executed and valid response received") {
      every { httpClient.send(any(), any<HttpResponse.BodyHandler<String>>()) } returns mockk<HttpResponse<String>> {
        every { statusCode() } returns 200
        @Language("json")
        val json = """
          {
            "images": [
              {
                "startdate": "20220422",
                "fullstartdate": "202204220700",
                "enddate": "20220423",
                "url": "/th?id=OHR.EarthDay2022_EN-US1806321261_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp",
                "urlbase": "/th?id=OHR.EarthDay2022_EN-US1806321261",
                "copyright": "Small lake in Karula National Park, Valgamaa County, Estonia (© Sven Zacek/Minden Pictures)",
                "copyrightlink": "https://www.bing.com/search?q=earth+day&form=hpcapt&filters=HpDate%3a%2220220422_0700%22",
                "title": "Eye of the world",
                "quiz": "/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20220422_EarthDay2022%22&FORM=HPQUIZ",
                "wp": true,
                "hsh": "9f2b97b4c21e6361a251e316b224ed99",
                "drk": 1,
                "top": 1,
                "bot": 1,
                "hs": []
              }
            ],
            "tooltips": {
              "loading": "Trwa ładowanie...",
              "previous": "Poprzedni obraz",
              "next": "Następny obraz",
              "walle": "Tego obrazu nie można pobrać jako tapety.",
              "walls": "Pobierz ten obraz. Ten obraz może być używany tylko jako tapeta."
            }
          }
        """
        every { body() } returns json
      }

      val result = useCase.execute(command)

      Then("success with wallpaper metadata is returned") {
        result shouldBe FetchWallpaperMetadataResult.Success(
            wallpaperMetadata = WallpaperMetadata(
              startDate = LocalDate(2022, Month.APRIL, 22),
              fullStartDate = LocalDateTime(2022, Month.APRIL, 22, 7, 0),
              endDate = LocalDate(2022, Month.APRIL, 23),
              urlPath = "/th?id=OHR.EarthDay2022_EN-US1806321261_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp",
              urlBase = "/th?id=OHR.EarthDay2022_EN-US1806321261",
              copyright = "Small lake in Karula National Park, Valgamaa County, Estonia (© Sven Zacek/Minden Pictures)",
              copyrightLink = "https://www.bing.com/search?q=earth+day&form=hpcapt&filters=HpDate%3a%2220220422_0700%22",
              title = "Eye of the world",
              quiz = "/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20220422_EarthDay2022%22&FORM=HPQUIZ",
            ),
        )
      }
    }

  }

})
