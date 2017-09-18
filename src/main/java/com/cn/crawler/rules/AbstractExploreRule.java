package com.cn.crawler.rules;

import com.cn.crawler.entities.Link;

import java.net.MalformedURLException;

/**
 * Created by burhan on 9/18/17.
 */
public abstract class AbstractExploreRule {

    public boolean isExplorable(Link link){
        try {
            if(ruleHandler(link)){
                return true;
            }
        } catch (MalformedURLException e) {
            return false;
        }
        return false;
    }

    abstract boolean ruleHandler(Link link) throws MalformedURLException;
}
