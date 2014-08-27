package ee.mcdimus.matewp;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Dmitri Maksimov
 */
public class Main {

  // Constatnts for the Bing API
  private static final String BING_PHOTO_OF_THE_DAY_URL = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
  private static final String BING_HOST = "http://www.bing.com";
  private static final String DIMENSION = "1920x1200";
  private static final String EXTENSION = ".jpg";

  // Constants for the MATE
  private static final String GSETTINGS = "gsettings";
  private static final String SET_CMD = "set";
  private static final String GET_CMD = "get";
  private static final String SCHEMA = "org.mate.background";
  private static final String KEY = "picture-filename";

  public static void main(String[] args) {
    try {
      String fullURL = getImageURL();
      System.out.println(fullURL);
      String imageFullPath = downloadImage(fullURL);
      System.out.println(imageFullPath);

      // get previous setting
      Process getProcess = Runtime.getRuntime().exec(GSETTINGS, new String[]{GET_CMD, SCHEMA, KEY});
      String value = "";
      try (BufferedReader in = new BufferedReader(new InputStreamReader(getProcess.getInputStream()))) {
        String line;
        while ((line = in.readLine()) != null) {
          value += line;
        }
      }
      getProcess.waitFor();

      System.out.println(value);
      System.exit(0);
// execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
      Process exec = Runtime.getRuntime().exec(GSETTINGS, new String[]{SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFullPath)});
      exec.waitFor();
    } catch (IOException | ParseException | InterruptedException ex) {
      System.err.println(ex.getMessage());
    }
  }

  private static String downloadImage(String fullURL) throws IOException {
    BufferedImage image = ImageIO.read(new URL(fullURL));
    File homeDir = new File(System.getenv().getOrDefault("HOME", "./"));
    File imagesDir = new File(homeDir, "Pictures/mate-wp");
    if (!imagesDir.exists()) {
      imagesDir.mkdirs();
    }
    File imageFile = new File(imagesDir, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + EXTENSION);
    ImageIO.write(image, "jpg", imageFile);
    String imageFullPath = imageFile.getAbsolutePath();
    return imageFullPath;
  }

  private static String getImageURL() throws MalformedURLException, IOException, ParseException {
    URL url = new URL(BING_PHOTO_OF_THE_DAY_URL);
    URLConnection conn = url.openConnection();
    JSONObject json;
    try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      json = (JSONObject) JSONValue.parseWithException(in);
    }
    JSONArray images = (JSONArray) json.get("images");
    JSONObject image = (JSONObject) images.get(0);
    String imageBaseURL = (String) image.get("urlbase");

    return String.format("%s%s_%s%s", BING_HOST, imageBaseURL, DIMENSION, EXTENSION);
  }

}
