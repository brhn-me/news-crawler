package com.cn.crawler;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import com.cn.crawler.utils.ValidDirectory;
import org.springframework.stereotype.Service;

/**
 * Created by burhan on 9/13/17.
 */
@Service
public class Params {
    @Parameter(names = {"--seed", "-s"}, description = "Directory of seed files", required = true)
    private String seedPath = "seed";
    @Parameter(names = {"--crawl", "-c"}, description = "Path to save crawled contents", validateWith = ValidDirectory.class)
    private String crawlPath = null;
    @Parameter(names = {"--depth", "-d"}, description = "Maximum depth of web to explore", validateWith = PositiveInteger.class)
    private Integer depth = 1;

    @Parameter(names = {"--update-links", "-ul"}, description = "Explore visited urls", arity = 1)
    private boolean updateLinks = false;

    public String getSeedPath() {
        return seedPath;
    }

    public String getCrawlPath() {
        return crawlPath;
    }

    public Integer getDepth() {
        return depth;
    }

    public boolean getUpdateLinks() {
        return updateLinks;
    }
}

