package com.cn.crawler.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by burhan on 7/10/17.
 */
public class Agent {
    private static final Logger log = LoggerFactory.getLogger(Agent.class);
    private final Crawler crawler;
    private final Queue queue;
    private final AbstractParser parser;
    private final List<Fetcher> fetchers = new ArrayList<>();

    public Agent(Crawler crawler, Queue queue, AbstractParser parser) {
        this.queue = queue;
        this.crawler = crawler;
        this.parser = parser;
    }

    public void createFetcher(ExecutorService executor) {
        Fetcher fetcher = new Fetcher(crawler, this, queue);
        fetchers.add(fetcher);
        executor.submit(fetcher);
    }

    public Queue getQueue() {
        return queue;
    }

    public AbstractParser getParser() {
        return parser;
    }

    public void saveState() {
        queue.saveState();
    }
}
