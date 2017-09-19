package com.cn.crawler.core;

/**
 * Created by burhan on 9/13/17.
 */
public class InvalidLinkException extends Exception {
    public InvalidLinkException(String link, String message) {
        super("Invalid link: " + link + ", " + message);
    }

    public InvalidLinkException(String link, String message, Throwable cause) {
        super("Invalid link: " + link + ", " + message, cause);
    }
}
