package ee.mcdimus.matewp.service

import ee.mcdimus.matewp.model.ImageData
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import org.json.simple.parser.ParseException
import java.net.URL

/**
 * @author Dmitri Maksimov
 */
class BingPhotoOfTheDayService {

  companion object {
    private const val BING_PHOTO_OF_THE_DAY_URL = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US"
    private const val START_DATE = "startdate"
    private const val COPYRIGHT = "copyright"
    private const val URL_BASE = "urlbase"
    private const val IMAGES = "images"
  }

  fun getData(): ImageData {
    val url = URL(BING_PHOTO_OF_THE_DAY_URL)
    val conn = url.openConnection()

    conn.inputStream.bufferedReader().use {
      try {
        val jsonObject = JSONValue.parseWithException(it) as JSONObject
        validateProperties(jsonObject, listOf(IMAGES))

        val images = jsonObject[IMAGES] as JSONArray
        if (images.isEmpty()) {
          throw IllegalStateException("'$IMAGES' is empty")
        }

        val image = images[0] as JSONObject
        validateProperties(image, listOf(START_DATE, COPYRIGHT, URL_BASE))

        return ImageData(
            startDate = image[START_DATE] as String,
            copyright = image[COPYRIGHT] as String,
            urlBase = image[URL_BASE] as String
        )
      } catch(e: ParseException) {
        throw IllegalStateException(e.message)
      }
    }
  }

  private fun validateProperties(jsonObject: JSONObject, properties: List<String>) {
    for (property in properties) {
      if (!jsonObject.contains(property)) {
        throw IllegalStateException("${jsonObject.toString()} has no required property '$property'")
      }
    }
  }

}
