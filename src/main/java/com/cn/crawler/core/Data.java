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


    public void migrate() {
        log.info("Running migration...");
        log.info("    - Loading links...");
        List<Link> links = linkRepository.findAll();
        log.info("    - Loading newses...");
        List<News> newses = newsRepository.findAll();
        HashMap<String, News> map = new HashMap<>();
        for (News news : newses) {
            map.put(news.getId(), news);
        }

        for (Link link : links) {
            try {
                AbstractParser parser = null;
                if ("anandabazar.com".equals(link.getHost())) {
                    parser = new AnandaBazarParser();
                }
                Link newLink = Utils.createLink(link.getUrl(), link.getDepth(), parser);

                if(newLink != null) {
                    log.info("    - Processing : " + link.getUrl() + " --> " + newLink.getUrl());
                    News news = map.get(link.getId());
                    if (news != null) {
                        news.setId(newLink.getId());
                        news.setUrl(newLink.getUrl());
                        newsRepository.save(news);
                    }
                    if (link.getStatus() == Status.V) {
                        newLink.setStatus(Status.V);
                    }

                    newLink.setPriority(link.getPriority());
                    newLink.setNews(link.isNews());

                    if (!link.getId().equals(newLink.getId())) {
                        linkRepository.delete(link);
                    }
                    linkRepository.save(newLink);
                } else {
                    log.error("Failed link : " + link.toString());
                }
            } catch (InvalidLinkException e) {
                e.printStackTrace();
            }
        }
        log.info("    - Saving...");

        log.info("Migration complete...");
    }
}
