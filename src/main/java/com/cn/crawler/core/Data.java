package com.cn.crawler.core;

import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import com.cn.crawler.repositories.LinkRepository;
import com.cn.crawler.repositories.NewsRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by burhan on 9/13/17.
 */
@Service
public class Data {
    @Inject
    private NewsRepository newsRepository;
    @Inject
    private LinkRepository linkRepository;

    public void saveNews(News news) {
        newsRepository.save(news);
    }

    public List<Link> getLinksByDomain(String domain){
        return linkRepository.findAllByHost(domain);
    }

    public void saveLinks(List<Link> links){
        linkRepository.save(links);
    }
}
