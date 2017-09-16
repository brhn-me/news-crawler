package com.cn.crawler.entities;

import com.cn.crawler.core.Status;
import org.springframework.data.annotation.Id;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Created by burhan on 5/30/17.
 */
public class Link {
    @Id
    private String url;
    private Status status;
    private String domain;
    private int depth;
    private Date date;
    private String hash;

    public Link() {
    }

    public Link(String url, int depth) throws MalformedURLException, UnsupportedEncodingException {
        URL u = new URL(url);
        this.url = u.toString();
        String domain = u.getHost();
        this.domain = domain.startsWith("www.") ? domain.substring(4) : domain;
        this.status = Status.Q;
        this.depth = depth;
        this.date = new Date();
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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
                ", domain='" + domain + '\'' +
                ", depth=" + depth +
                ", date=" + date +
                ", hash='" + hash + '\'' +
                '}';
    }
}
