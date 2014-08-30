package ee.mcdimus.matewp.model;

import java.util.Objects;

/**
 *
 * @author dmitri
 */
public class ImageData {

  // Constatnts for the Bing API
  private static final String BING_HOST = "http://www.bing.com";
  private static final String DIMENSION = "1920x1200";
  private static final String EXTENSION = ".jpg";

  /**
   * "startdate":"20140829"
   */
  private String startDate;
  /**
   * "urlbase":"/az/hprichbg/rb/FloatingMarket_EN-US10075355698"
   */
  private String urlBase;
  /**
   * "copyright":"Floating market vendor near Bangkok, Thailand (© Art Wolfe/Mint Images)",
   */
  private String copyright;

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getUrlBase() {
    return urlBase;
  }

  public void setUrlBase(String urlBase) {
    this.urlBase = urlBase;
  }

  public String getCopyright() {
    return copyright;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

  public String getName() {
    return getUrlBase().substring(getUrlBase().lastIndexOf('/') + 1);
  }

  public String getDownloadURL() {
    return String.format("%s%s_%s%s", BING_HOST, getUrlBase(), DIMENSION, EXTENSION);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 43 * hash + Objects.hashCode(this.startDate);
    hash = 43 * hash + Objects.hashCode(this.urlBase);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ImageData other = (ImageData) obj;
    if (!Objects.equals(this.startDate, other.startDate)) {
      return false;
    }
    if (!Objects.equals(this.urlBase, other.urlBase)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "ImageData{\n"
            + "\tstartDate=" + startDate + "\n"
            + "\tname=" + getName() + "\n"
            + "\turlBase=" + urlBase + "\n"
            + "\tcopyright=" + copyright + "\n"
            + '}';
  }

}
