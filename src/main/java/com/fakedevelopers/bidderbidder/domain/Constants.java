package com.fakedevelopers.bidderbidder.domain;

/**
 * The type Constants.
 */
public class Constants {

    public static final String UTILITY_CLASS = "Utility Class";
    public static final int APP_RESIZE_SIZE = 100;
    public static final int WEB_RESIZE_SIZE = 200;
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_FOR_REDIS = "yyyyMMdd";
    public static final String SEARCH_WORD_REDIS = "searchWord:";
    public static final int SEARCH_WORD_REMAIN_DAYS = 3;
    public static final String REPLACE_FUNCTION = "function('replace', {0}, {1}, {2})";
    public static final int SEARCH_TITLE = 0;
    public static final int SEARCH_CONTENT = 1;
    public static final int SEARCH_TITLE_AND_CONTENT = 2;
    public static final String INIT_NICKNAME = "Bidder";
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
    public static final String REQUIRED = "required";
    public static final String OPTIONAL = "optional";

    public static final int MIN_USERNAME_SIZE = 6;
    public static final int MAX_USERNAME_SIZE = 64;


    public static final int MIN_PASSWORD_SIZE = 10;
    public static final int MAX_PASSWORD_SIZE = 64;

    private Constants() {
        throw new IllegalStateException("Utility State");
    }
}
