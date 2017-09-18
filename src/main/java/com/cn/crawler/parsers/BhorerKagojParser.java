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
public class BhorerKagojParser extends AbstractParser {
    public BhorerKagojParser() {
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (doc.select("article.post").size() > 0) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select("article time").first().attr("datetime");
        String title = doc.select("article h1.entry-title").text();
        Elements paras = doc.select("article div.td-post-content p");
        StringBuilder content = new StringBuilder();
        for(Element para : paras){
            content.append(para.text());
            content.append("\r\n\r\n");
        }
        Set<String> categories = new HashSet<>();
        categories.add(doc.select(".entry-crumbs .entry-crumb").last().text());

        Set<String> images = new HashSet<>();
        Elements elements = doc.select("article .td-post-featured-image img");
        for (Element img : elements) {
            images.add(img.attr("abs:src"));
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
