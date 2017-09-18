package com.cn.crawler.rules;

import com.cn.crawler.entities.Link;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by burhan on 9/18/17.
 */
public class BBCBanglaExploreRule extends AbstractExploreRule {
    @Override
    boolean ruleHandler(Link link) throws MalformedURLException {
        URL url = new URL(link.getUrl());
        System.out.println(url.getPath());
        if(url.getPath().startsWith("bengali")){
            return true;
        }
        return false;
    }
}
