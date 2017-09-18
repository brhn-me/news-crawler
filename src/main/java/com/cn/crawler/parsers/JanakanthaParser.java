package com.cn.crawler.parsers;

import com.cn.crawler.core.AbstractParser;
import com.cn.crawler.core.ParseException;
import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import com.cn.crawler.utils.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by burhan on 5/19/17.
 */
public class JanakanthaParser extends AbstractParser {
    public JanakanthaParser() {
    }

    @Override
    public int getPriority(Link link) {
        if(link.getUrl().contains("dailyjanakantha.us/details/article/")){
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (link.getUrl().contains("dailyjanakantha.us/details/article/") && doc.select(".details-article").size() > 0) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String title = doc.select(".details-article h1").text();

        Set<String> categories = new HashSet<>();
        categories.add(doc.select(".details-article .details-article-info.smp-c a").first().text());
        Elements categoryElements = doc.select(".details-article .tags-disp a");
        for(Element el : categoryElements){
            String cat = el.text();
            if(!Utils.isNullOrEmpty(cat)){
                cat = cat.trim();
                categories.add(cat);
            }
        }


        Set<String> images = new HashSet<>();
        Elements els = doc.select(".details-article > img.smp-w100p");
        if(els.size()> 0) {
            images.add(els.first().attr("abs:src"));
        }

        Elements paras = doc.select(".details-article > p");
        StringBuilder content = new StringBuilder();
        for(Element para : paras){
            content.append(para.text());
            content.append("\r\n\r\n");
        }



        News news = new News();
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content.toString().trim());
        news.setImages(images);

        return news;
    }
}
