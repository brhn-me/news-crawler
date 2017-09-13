package com.cn.crawler.core;

import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import com.cn.crawler.utils.Utils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;

/**
 * Created by burhan on 5/19/17.
 */
public abstract class AbstractParser {
    private static final Logger log = LoggerFactory.getLogger(Fetcher.class);
    protected final String baseUrl;

    public AbstractParser(String baseUrl){
        this.baseUrl = baseUrl;
    }

    protected abstract boolean isParsable(Link link, Document doc) throws ParseException;

    protected abstract News parseHandler(Link link, Document doc) throws ParseException;

    public News parse(Link link, Document doc) throws ParseException{
        if(isParsable(link, doc)){
            try {
                log.debug(" - Parsing: " + link.getUrl());
                News news = parseHandler(link, doc);
                if(news != null) {
                    try {
                        news.setId(Utils.md5(news.getUrl()));
                        news.setHash(Utils.md5(news.getContent()));
                    } catch (NoSuchAlgorithmException e) {
                        throw new ParseException("Failed to generate (id, hash) from (url, content) for link : " + link
                                .toString(), e);
                    }
                }
                return news;
            } catch (Exception ex){
                throw  new ParseException("Failed to parse : " + link.toString(), ex);
            }
        }
        return null;
    }
}
