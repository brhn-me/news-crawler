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


    public void migrate(HashMap<String, Class> parsers) {
        log.info("Running migration...");
        log.info("    - Loading links...");
        List<Link> links = linkRepository.findAll();
//        log.info("    - Loading newses...");
//        List<News> newses = newsRepository.findAll();
//        HashMap<String, News> map = new HashMap<>();
//        for (News news : newses) {
//            map.put(news.getId(), news);
//        }

        for (Link link : links) {
            Class type = parsers.get(link.getHost());
            AbstractParser parser = null;
            try {
                parser = (AbstractParser) type.newInstance();
            } catch (InstantiationException ex) {
                log.error("Failed to instantiate parser for host : " + link.getHost());
                continue;
            } catch (IllegalAccessException ex) {
                log.error("Failed to instantiate parser for host : " + link.getHost());
                continue;
            }
            int priority = Math.abs(Fetcher.MAX_DEPTH - link.getDepth()) * Fetcher.DEPTH_WEIGHT;
            if(parser != null){
                priority += parser.getPriority(link);
            }
            log.info("Calculated depth : " + priority + " --> " + link.toString());
            linkRepository.save(link);
        }
        linkRepository.save(links);

        log.info("Migration complete...");
    }
}
