package com.fakedevelopers.bidderbidder.domain;

/**
 * The type Constants.
 */
public class Constants {

  private Constants() {
    throw new IllegalStateException("Utility State");
  }

  public static final String INIT_NICKNAME = "Bidder";

  public static final int APP_RESIZE_SIZE = 100;
  public static final int WEB_RESIZE_SIZE = 200;
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
  public static final String DATE_FORMAT_FOR_REDIS = "yyyyMMdd";
  public static final String SEARCH_WORD_REDIS = "searchWord:";

}
