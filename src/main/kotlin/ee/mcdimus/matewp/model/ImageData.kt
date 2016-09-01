package ee.mcdimus.matewp.model

/**
 * Example:
 * "startdate":"20140829"
 * "urlbase":"/az/hprichbg/rb/FloatingMarket_EN-US10075355698"
 * "copyright":"Floating market vendor near Bangkok, Thailand (© Art Wolfe/Mint Images)",
 *
 * @author Dmitri Maksimov
 */
data class ImageData(val startDate: String, val urlBase: String, val copyright: String) {

  companion object {
    private const val BING_HOST = "http://www.bing.com"
    private const val DIMENSION = "1920x1080"
    private const val EXTENSION = ".jpg"
  }

  val name: String
    get() = urlBase.substring(urlBase.lastIndexOf('/') + 1)

  val downloadURL: String
    get() = "$BING_HOST${urlBase}_$DIMENSION$EXTENSION"

  val filename: String
    get() = startDate + EXTENSION

  override fun toString(): String {
    return "ImageData(\n" +
        "\tstartDate=$startDate\n" +
        "\tname=$name\n" +
        "\tcopyright=$copyright\n" +
        "\tdownloadURL=$downloadURL\n" +
        ")"
  }

}
