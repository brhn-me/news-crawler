package com.cn.crawler.core;

/**
 * Created by burhan on 9/13/17.
 */
public class ParseException extends Exception {
    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
