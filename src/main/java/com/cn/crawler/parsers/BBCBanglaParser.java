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
public class BBCBanglaParser extends AbstractParser {
    public BBCBanglaParser() {
    }

    @Override
    public int getPriority(Link link) {
        if(link.getUrl().contains("/bengali/news-")){
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (link.getUrl().contains("/bengali/news-") && doc.select(".story-body").size() > 0) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select(".date.date--v2").first().attr("data-datetime");
        String title = doc.select(".story-body__h1").text();
        Set<String> categories = new HashSet<>();
        Elements categoryElements = doc.select(".tags-container .tags-list li a");
        for(Element li : categoryElements){
            String cat = li.text();
            if(cat != null || "".equals(cat)){
                cat = cat.trim();
                categories.add(cat);
            }
        }

        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".story-body img.js-image-replace");
        for (Element img : elements) {
            images.add(img.attr("abs:src"));
        }

        Elements paras = doc.select(".story-body .story-body__inner p");
        StringBuilder content = new StringBuilder();
        for(Element para : paras){
            content.append(para.text());
            content.append("\r\n\r\n");
        }


        News news = new News();
        news.setDate(date);
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content.toString());
        news.setImages(images);

        return news;
    }
}
