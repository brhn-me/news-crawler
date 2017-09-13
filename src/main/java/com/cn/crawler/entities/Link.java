package com.cn.crawler.entities;

import com.cn.crawler.core.Status;
import org.springframework.data.annotation.Id;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by burhan on 5/30/17.
 */
public class Link {
    @Id
    private String url;
    private Status status;
    private String domain;
    private int depth;

    public Link() {
    }

    public Link(String u, int depth) throws MalformedURLException {
        URL url = new URL(u);
        this.url = url.toString();
        String domain = url.getHost();
        this.domain = domain.startsWith("www.") ? domain.substring(4) : domain;
        this.status = Status.ENQUEUED;
        this.depth = depth;
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
                '}';
    }
}
