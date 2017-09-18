package com.cn.crawler.entities;

import com.cn.crawler.core.Status;
import com.cn.crawler.utils.Utils;
import org.springframework.data.annotation.Id;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by burhan on 5/30/17.
 */
public class Link {
    @Id
    private String url;
    private Status status;
    private String host;
    private int depth;
    private Date date;
    private String hash;
    private boolean isNews = false;

    public Link() {
    }

    public Link(String url, int depth) throws MalformedURLException, UnsupportedEncodingException {
        URL u = new URL(url);
        String domain = u.getHost();

        this.url = Utils.getDecodedUrl(u.toString());
        this.host = domain.startsWith("www.") ? domain.substring(4) : domain;
        this.status = Status.Q;
        this.depth = depth;
        this.date = new Date();
        try {
            this.hash = Utils.hash(this.url);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
                "url='" + url + '\'' +
                ", status=" + status +
                ", host='" + host + '\'' +
                ", depth=" + depth +
                ", date=" + date +
                ", hash='" + hash + '\'' +
                ", isNews=" + isNews +
                '}';
    }
}
