package ee.mcdimus.matewp;

import ee.mcdimus.matewp.model.ImageData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * @author Dmitri Maksimov
 */
public class Main {

  private static final String CONFIG_FILENAME = "config.properties";

  // Constatnts for the Bing API
  private static final String BING_PHOTO_OF_THE_DAY_URL = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";

  // Constants for the MATE
  private static final String GSETTINGS = "gsettings";
  private static final String SET_CMD = "set";
  private static final String GET_CMD = "get";
  private static final String SCHEMA = "org.mate.background";
  private static final String KEY = "picture-filename";

  private static Properties props;

  public static void main(String[] args) {
    // try to load 'config.properties', which is in the same folder with jar file
    String configFileLocation = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getPath();

    props = new Properties();
    System.out.println("[x] Loading config file from '" + configFileLocation + File.separator + CONFIG_FILENAME + "'...");
    try (Reader input = new FileReader(configFileLocation + File.separator + CONFIG_FILENAME)) {
      props.load(input);
    } catch (IOException ex) {
      // if not exists:
      //  create
      //  store system's current wallpaper
      System.out.println("\t[-] failed. Will create new one...");
      // get previous setting
      String result;
      try {
        result = execCommand(GSETTINGS, GET_CMD, SCHEMA, KEY);
        props.setProperty("matewp.system.background", result);
        props.store(new FileWriter(configFileLocation + File.separator + CONFIG_FILENAME), null);
      } catch (IOException | InterruptedException ex1) {
        System.err.println("Failed to create properties file at '" + configFileLocation + File.separator + CONFIG_FILENAME + "'. Shutting down...");
        System.exit(1);
      }
    }

    // get image data
    ImageData imageData = new ImageData();
    try {
      URL url = new URL(BING_PHOTO_OF_THE_DAY_URL);
      URLConnection conn = url.openConnection();
      JSONObject json;
      try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        json = (JSONObject) JSONValue.parseWithException(in);
      }
      if (json == null) {
        throw new IOException("json is null");
      }
      JSONArray images = (JSONArray) json.get("images");
      JSONObject image = (JSONObject) images.get(0);

      // retrieve image link, startdate, image name, and copyright
      imageData.setCopyright((String) image.get("copyright"));
      imageData.setStartDate((String) image.get("startdate"));
      imageData.setUrlBase((String) image.get("urlbase"));
    } catch (IOException | ParseException ex) {
      System.err.println("Unexpected error: " + ex.getMessage());
      System.exit(2);
    }

    System.out.println(imageData);
    // try to load ${startdate}.properties
    // if not exists: create new
    //  store image data
    //  download image
    //  change wallpaper
    // else:
    //  do nothing

//    try {
//      System.out.println("[x] Getting image URL...");
//      String fullURL = getImageURL();
//      System.out.println("\t [-] " + fullURL);
//      System.out.println("[x] Downloading image...");
//      String imageFullPath = downloadImage(fullURL);
//      System.out.println("\t [-] downloaded to " + imageFullPath);
//
//      Properties props = new Properties();
//      if (new File("props.properties").exists()) {
//        props.load(new FileInputStream("props.properties"));
//      }
//      props.store(new FileOutputStream("props.properties"), null);
//
//      // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
//      execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFullPath));
//    } catch (IOException | ParseException | InterruptedException ex) {
//      System.err.println(ex.getMessage());
//    }
  }

  private static String execCommand(String... args) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder(args);
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();
    String value;
    try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      value = in.readLine();
//        while ((line = in.readLine()) != null) {
//          value += line + "\n";
//        }
    }
    process.waitFor();
    return value;
  }

  /*  private static String downloadImage(String fullURL) throws IOException {
   URL url = new URL(fullURL);
   URLConnection urlConnection = url.openConnection();
   long contentLength = urlConnection.getContentLengthLong();
   System.out.println("\t [-] image size: " + contentLength + " bytes");
   BufferedImage image = ImageIO.read(url);
   File homeDir = new File(System.getenv().getOrDefault("HOME", "./"));
   File imagesDir = new File(homeDir, "Pictures/mate-wp");
   if (!imagesDir.exists()) {
   imagesDir.mkdirs();
   }
   File imageFile = new File(imagesDir, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + EXTENSION);
   ImageIO.write(image, "jpg", imageFile);

   return imageFile.getAbsolutePath();
   }

   private static String getImageURL() throws IOException, ParseException {
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
   }*/
}
