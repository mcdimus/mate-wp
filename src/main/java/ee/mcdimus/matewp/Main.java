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

  // Constants for the Bing API
  private static final String BING_PHOTO_OF_THE_DAY_URL = "http://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";

  // Constants for the MATE
  private static final String GSETTINGS = "gsettings";
  private static final String SET_CMD = "set";
  private static final String GET_CMD = "get";
  private static final String SCHEMA = "org.mate.background";
  private static final String KEY = "picture-filename";

  private static Properties props;

  public static void main(String[] args) {
    String arg0 = null;
    String arg1 = null;
    switch (args.length) {
      case 2:
        arg1 = args[1];
      case 1:
        arg0 = args[0];
        break;
      default:
        System.out.println("no args");
        System.exit(0);
    }

    switch (arg0) {
      case "save":
        saveCurrentWPconfig(arg1);
        break;
      case "restore":
        restoreWPconfig(arg1);
        break;
      case "update":
        update();
        break;
      default:
        System.out.println("Unknown command...");
        System.exit(0);
    }
  }

  private static void saveCurrentWPconfig(String id) {
    File homeDir = new File(System.getenv().getOrDefault("HOME", "./"));
    File imagesDir = new File(homeDir, "Pictures/mate-wp");
    File configDir = new File(imagesDir, "configs");
    try {
      if (!configDir.exists()) {
        if (!configDir.mkdirs()) {
          System.err.println("Could not create directory: " + configDir.getAbsolutePath());
          System.exit(2);
        }
      }
      Properties properties = new Properties();
      String result = execCommand(GSETTINGS, GET_CMD, SCHEMA, KEY);
      properties.setProperty("matewp.system.background", result);
      try (FileWriter writer = new FileWriter(configDir + File.separator + id + ".properties")) {
        properties.store(writer, null);
      }
    } catch (IOException | InterruptedException ex1) {
      System.err.println("Failed to create properties file at '" + configDir + File.separator + id + ".properties" + "'. Shutting down...");
      System.exit(1);
    }
  }

  private static void restoreWPconfig(String arg1) {
    File homeDir = new File(System.getenv().getOrDefault("HOME", "./"));
    File imagesDir = new File(homeDir, "Pictures/mate-wp");
    File configDir = new File(imagesDir, "configs");

    File config = new File(configDir, arg1 + ".properties");
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(config));
    } catch (IOException e) {
      e.printStackTrace();
    }

    File imageFile = new File(properties.getProperty("matewp.system.background").replace("'",""));
    // change wallpaper
    try {
      // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
      execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFile.getAbsoluteFile()));
    } catch (IOException | InterruptedException ex) {
      System.err.println(ex.getMessage());
    }

  }

  private static void update() {
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

      // change wallpaper
      try {
        // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
        execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFile.getAbsoluteFile()));
      } catch (IOException | InterruptedException ex) {
        System.err.println(ex.getMessage());
      }
    } else {
      // change wallpaper
      try {
        // execute command 'gsettings set org.mate.background picture-filename '/home/dmitri/Pictures/mate-wp/2014-08-27.jpg''
        File imageFile = new File(imagesDir, imageData.getFilename());
        execCommand(GSETTINGS, SET_CMD, SCHEMA, KEY, String.format("'%s'", imageFile.getAbsoluteFile()));
      } catch (IOException | InterruptedException ex) {
        System.err.println(ex.getMessage());
      }
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
