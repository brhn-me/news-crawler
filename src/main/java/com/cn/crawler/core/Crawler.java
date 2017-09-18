package com.cn.crawler.core;

import com.cn.crawler.Params;
import com.cn.crawler.entities.Link;
import com.cn.crawler.parsers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by burhan on 5/30/17.
 */
@Service
public class Crawler {
    private static final Logger log = LoggerFactory.getLogger(Crawler.class);
    @Inject
    private Config config;
    private Params params;
    @Inject
    private Data data;

    private Set<Link> seeds = new HashSet<>();
    private HashMap<String, Agent> agents = new HashMap<>();
    private HashMap<String, Class> parsers = new HashMap<>();
    ExecutorService executor;


    public Crawler() {

    }

    public void boostrap(Params params){
        executor = Executors.newFixedThreadPool(config.getFetcher().getPool());

        this.setParams(params);
        this.loadSeeds(params.getSeedPath());
        this.registerParsers();
        this.createAgents();
        this.start();
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

    public Config getConfig() {
        return config;
    }

    public Data getData() {
        return data;
    }

    public void registerParsers(){
        parsers.put("prothom-alo.com", ProthomAloParser.class);
        parsers.put("bangla.bdnews24.com", BDNews24BanglaParser.class);
        parsers.put("kalerkantho.com", KalerKanthoParser.class);
        parsers.put("samakal.com", SamakalParser.class);
        parsers.put("banglanews24.com", BanglaNews24Parser.class);
        parsers.put("ittefaq.com.bd", IttefaqParser.class);
        parsers.put("bd-pratidin.com", BangladeshPratidinParser.class);
        parsers.put("dailynayadiganta.com", NayaDigantaParser.class);
        parsers.put("bonikbarta.net", BonikBartaParser.class);
        parsers.put("banglatribune.com", BanglaTribuneParser.class);
    }

    public void loadSeeds(String seedPath) {
        log.info("Loading seed files...");
        File folder = new File(seedPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException("Seed directory : '" + seedPath + "' does not exits");
        }
        File[] files = folder.listFiles();
        Set<Link> links = new HashSet<>();
        for (File file : files) {
            try {
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String url = scanner.nextLine();
                        // ignore commented
                        if(url.startsWith("#")){
                            continue;
                        }
                        try {
                            Link link = new Link(url, 0);
                            links.add(link);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    scanner.close();

                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Seed file : '" + file.getAbsolutePath() + "' does not exits");
            }
        }
        this.seeds = links;
    }

    public void createAgents() {
        log.info("Creating agents...");
        for (Link link : this.seeds) {
            String host = link.getHost();

            if(!parsers.containsKey(host)){
                log.error("Parser not found for host '"+ host + "'. Seed url '"+ link.getUrl() + "' would be ignored");
                continue;
            }
            Class type = parsers.get(host);
            AbstractParser parser = null;
            try {
                parser = (AbstractParser) type.newInstance();
            } catch (InstantiationException ex){
                log.error("Failed to instantiate parser for host : "+ host + ". Seed url '"+ link.getUrl() + "' would be ignored");
                continue;
            } catch (IllegalAccessException ex){
                log.error("Failed to instantiate parser for host : "+ host + ". Seed url '"+ link.getUrl() + "' would be ignored");
                continue;
            }

            if(parser == null){
                log.error("Parser not found for host : "+ host + ". Seed url '"+ link.getUrl() + "' would be ignored");
                continue;
            }

            if (!agents.containsKey(host)) {
                Queue q = new Queue(data, host);
                q.add(link);
                q.loadState(params.getUpdateLinks());

                Agent agent = new Agent(this, q, parser);
                agents.put(host, agent);
            } else {
                Agent agent = agents.get(host);
                agent.getQueue().add(link);
            }
        }
    }

    public void start() {
        log.info("Starting crawler...[Update Mood: "+ params.getUpdateLinks()+ "]");
        for (String domain : agents.keySet()) {
            Agent agent = agents.get(domain);
            agent.start(executor);
        }
        executor.shutdown();
    }

    public void shutdown(){
        log.info("Shutting down crawler...");
        for (String domain : agents.keySet()) {
            Agent agent = agents.get(domain);
            agent.saveState();
        }
    }


    @PreDestroy
    public void destroy(){
        shutdown();
    }
}
