package com.cn.crawler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Created by burhan on 7/7/17.
 */
@Component
@Configuration
@ConfigurationProperties
public class Config {

    private final Database database = new Database();
    private final Fetcher fetcher = new Fetcher();

    public Database getDatabase() {
        return database;
    }

    public Fetcher getFetcher() {
        return fetcher;
    }

    public static class Database{
        private String uri;
        private String collection;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }
    }

    public static class Fetcher{
        private int pool;
        private int timeout;
        private int delay;
        private int perqueue;

        public int getPool() {
            return pool;
        }

        public void setPool(int pool) {
            this.pool = pool;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public int getDelay() {
            return delay;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public int getPerqueue() {
            return perqueue;
        }

        public void setPerqueue(int perqueue) {
            this.perqueue = perqueue;
        }
    }
}
