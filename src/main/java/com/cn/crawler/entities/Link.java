package com.cn.crawler.entities;

import com.cn.crawler.core.Status;
import com.cn.crawler.utils.NormalizeURL;
import com.cn.crawler.utils.Utils;
import org.springframework.data.annotation.Id;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by burhan on 5/30/17.
 */
public class Link {
    @Id
    private String id;
    private String url;
    private String asciiUrl = null;
    private Status status;
    private String host;
    private int depth;
    private Date date;
    private String hash;
    private int priority = 0;
    private boolean isNews = false;

    public Link() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAsciiUrl() {
        return asciiUrl;
    }

    public void setAsciiUrl(String asciiUrl) {
        this.asciiUrl = asciiUrl;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isNews() {
        return isNews;
    }

    public void setNews(boolean news) {
        isNews = news;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link item = (Link) o;
        return url.equals(item.getUrl());
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return "Link{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", asciiUrl='" + asciiUrl + '\'' +
                ", status=" + status +
                ", host='" + host + '\'' +
                ", depth=" + depth +
                ", date=" + date +
                ", hash='" + hash + '\'' +
                ", priority=" + priority +
                ", isNews=" + isNews +
                '}';
    }
}
