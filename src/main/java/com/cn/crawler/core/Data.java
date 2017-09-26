package com.cn.crawler.core;

import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import com.cn.crawler.parsers.AnandaBazarParser;
import com.cn.crawler.repositories.LinkRepository;
import com.cn.crawler.repositories.NewsRepository;
import com.cn.crawler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by burhan on 9/13/17.
 */
@Service
public class Data {
    private static final Logger log = LoggerFactory.getLogger(Data.class);
    @Inject
    private NewsRepository newsRepository;
    @Inject
    private LinkRepository linkRepository;

    public void saveNews(News news) {
        newsRepository.save(news);
    }

    public List<Link> getLinksByHost(String host) {
        return linkRepository.findAllByHost(host);
    }

    public void saveLinks(List<Link> links) {
        linkRepository.save(links);
    }


    public void migrate(HashMap<String, Class> parsers) {
        log.info("Running migration...");
        log.info("    - Loading links...");
        List<Link> links = linkRepository.findAllByHost("anandabazar.com");

        AbstractParser parser = new AnandaBazarParser();
        for (Link link : links) {
            try {
                Link newLink = Utils.createLink(link.getUrl(), link.getDepth(), parser);
                newLink.setStatus(link.getStatus());
                linkRepository.delete(link);
                linkRepository.save(newLink);
                log.info("Processing : " + link.getUrl() + " --> " + newLink.getUrl());
            } catch (InvalidLinkException e) {
                e.printStackTrace();
            }
        }
        log.info("Migration complete...");
    }
}
