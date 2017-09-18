package com.cn.crawler.rules;

import com.cn.crawler.core.Queue;
import com.cn.crawler.entities.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * Created by burhan on 9/18/17.
 */
public abstract class AbstractExploreRule {
    private static final Logger log = LoggerFactory.getLogger(Queue.class);

    public boolean isExplorable(Link link){
        try {
            if(ruleHandler(link)){
                return true;
            }
        } catch (MalformedURLException e) {
            return false;
        }
        log.debug("Link excluded by custom rule for : " +link.toString());
        return false;
    }

    abstract boolean ruleHandler(Link link) throws MalformedURLException;
}
