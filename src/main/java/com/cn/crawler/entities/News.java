package com.cn.crawler.entities;

import java.io.Serializable;
import java.util.Set;
import org.springframework.data.annotation.Id;

/**
 * Created by burhan on 9/12/17.
 */
public class News implements Serializable {
    @Id
    private String id;
    private String url;
    private String date;
    private String title;
    private String content;
    private String hash;
    private Set<String> categories;
    private Set<String> images;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public Set<String> getImages() {
        return images;
    }

    public void setImages(Set<String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "News{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", date='" + date + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", hash='" + hash + '\'' +
                ", categories=" + categories +
                ", images=" + images +
                '}';
    }
}