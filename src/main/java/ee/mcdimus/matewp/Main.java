package ee.mcdimus.matewp;

import ee.mcdimus.matewp.model.ImageData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    System.out.println(imageData); // TODO: delete this

    // try to load ${startdate}.properties
    File homeDir = new File(System.getenv().getOrDefault("HOME", "./"));
    File imagesDir = new File(homeDir, "Pictures/mate-wp");
    if (!imagesDir.exists()) {
      if (!imagesDir.mkdirs()) {
        System.err.println("Could not create directory: " + imagesDir.getAbsolutePath());
        System.exit(2);
      }
    }

    File imagePropsFile = new File(imagesDir, imageData.getStartDate() + ".properties");

    Properties imageProps = new Properties();
    if (!imagePropsFile.exists()) {
      // if not exists: create new
      //  store image data
      imageProps.setProperty("startDate", imageData.getStartDate());
      imageProps.setProperty("urlBase", imageData.getUrlBase());
      imageProps.setProperty("copyright", imageData.getCopyright());
      try {
        imageProps.store(new FileWriter(imagePropsFile), null);
      } catch (IOException e) {
        System.err.println("Could not create image data: " + imagePropsFile.getAbsolutePath());
        System.exit(3);
      }

      //  download image
      File imageFile = null;
      try {
        System.out.println("Donwload URL: " + imageData.getDownloadURL());
        URL url = new URL(imageData.getDownloadURL());
        URLConnection urlConnection = url.openConnection();
        long contentLength = urlConnection.getContentLengthLong();
        System.out.println("\t [-] image size: " + contentLength + " bytes");
        BufferedImage image = ImageIO.read(url);

        imageFile = new File(imagesDir, imageData.getFilename());
        ImageIO.write(image, "jpg", imageFile);
      } catch (IOException e) {
        System.err.println("Could not create image file: " + imageFile != null ? imageFile.getAbsolutePath() : e.getMessage());
        System.exit(4);
      }

      //  change wallpaper
      try {
        // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
        execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFile.getAbsoluteFile()));
      } catch (IOException | InterruptedException ex) {
        System.err.println(ex.getMessage());
      }
    } else {
      // currently do nothing
    }

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

}
