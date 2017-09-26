package com.cn.crawler.core;

import com.cn.crawler.Params;
import com.cn.crawler.entities.Link;
import com.cn.crawler.parsers.*;
import com.cn.crawler.rules.AbstractExploreRule;
import com.cn.crawler.rules.BBCBanglaExploreRule;
import com.cn.crawler.rules.RoarBanglaExploreRule;
import com.cn.crawler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
    private HashMap<String, Class> rules = new HashMap<>();
    ExecutorService executor = Executors.newFixedThreadPool(500);
    private boolean shuttingDown = false;


    public Crawler() {

    }

    public void boostrap(Params params) {
        this.setParams(params);
        this.registerParsers();
        this.registerRules();
        //data.migrate(parsers);
        this.loadSeeds(params.getSeedPath());
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

    public void registerParsers() {
        // news sites
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
        parsers.put("mzamin.com", ManabZaminParser.class);
        parsers.put("bhorerkagoj.net", BhorerKagojParser.class);
        parsers.put("dailyjanakantha.us", JanakanthaParser.class);
        parsers.put("jugantor.com", JugantarParser.class);
        parsers.put("anandabazar.com", AnandaBazarParser.class);
        parsers.put("bbc.com", BBCBanglaParser.class);
        parsers.put("amritabazar.com", AmritaBazarParser.class);
        parsers.put("dailyinqilab.com", InqilabParser.class);
        parsers.put("dailysangram.com", SangramParser.class);
        parsers.put("gonokantho.com", GonoKanthoParser.class);

        // blogs
        parsers.put("roar.media", RoarBanglaParser.class);
    }

    public void registerRules() {
        rules.put("bbc.com", BBCBanglaExploreRule.class);
        rules.put("roar.media", RoarBanglaExploreRule.class);
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
                        if (url.startsWith("#")) {
                            continue;
                        }
                        Link link = null;
                        try {
                            link = Utils.createLink(url, 0);
                            link.setPriority(10000);
                            links.add(link);
                        } catch (InvalidLinkException e) {
                            log.error(e.getMessage());
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

            if (!parsers.containsKey(host)) {
                log.error("Parser not found for host '" + host + "'. Seed url '" + link.getUrl() + "' would be ignored");
                continue;
            }
            Class type = parsers.get(host);
            AbstractParser parser = null;
            try {
                parser = (AbstractParser) type.newInstance();
            } catch (InstantiationException ex) {
                log.error("Failed to instantiate parser for host : " + host + ". Seed url '" + link.getUrl() + "' would be ignored");
                continue;
            } catch (IllegalAccessException ex) {
                log.error("Failed to instantiate parser for host : " + host + ". Seed url '" + link.getUrl() + "' would be ignored");
                continue;
            }

            if (parser == null) {
                log.error("Parser not found for host : " + host + ". Seed url '" + link.getUrl() + "' would be ignored");
                continue;
            }

            if (!agents.containsKey(host)) {
                Class ruleClass = rules.get(host);
                AbstractExploreRule rule = null;
                if (ruleClass != null) {
                    try {
                        rule = (AbstractExploreRule) ruleClass.newInstance();
                        log.info("Custom rules instantiated for : " + host);
                    } catch (InstantiationException e) {
                        log.error(e.getMessage());
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage());
                    }
                }
                Queue q = new Queue(data, host, rule);
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
        log.info("Starting crawler...[Update Mood: " + params.getUpdateLinks() + "]");

        for (int i = 0; i < config.getAgent().getFetchers(); i++) {
            for (String host : agents.keySet()) {
                Agent agent = agents.get(host);
                if(agent.getQueue().size() > 0) {
                    agent.createFetcher(executor);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }

    public boolean isShuttingDown() {
        return shuttingDown;
    }

    public void shutdown() {
        log.info("Shutting down crawler...");
        shuttingDown = true;
        for (String host : agents.keySet()) {
            Agent agent = agents.get(host);
            agent.saveState(true);
        }
    }


    @PreDestroy
    public void destroy() {
        shutdown();
    }
}
