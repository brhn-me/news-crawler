package com.cn.crawler.core;

/**
 * Created by burhan on 9/13/17.
 */
public class ParseException extends Exception {
    public ParseException(String link, String message) {
        super("Failed to parse: " + link + ", " + message);
    }

    public ParseException(String link, String message, Throwable cause) {
        super("Failed to parse: " + link + ", " + message, cause);
    }


}
