package com.cn.crawler.repositories;

import com.cn.crawler.entities.News;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by burhan on 9/13/17.
 */
public interface NewsRepository extends MongoRepository<News, String> {
}
