package com.cn.crawler.repositories;

import com.cn.crawler.entities.Link;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by burhan on 9/13/17.
 */
public interface LinkRepository extends MongoRepository<Link, String> {
    public List<Link> findAllByDomain(String domain);
}
