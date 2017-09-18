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
public class RoarBanglaParser extends AbstractParser {
    public RoarBanglaParser() {
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (doc.select("article.post").size() > 0) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select("article.post time.entry-time").text();
        String title = doc.select("article.post h1.entry-title").text();
        String content = Utils.br2nl(doc.select("article.post section.entry-content").html());

        Set<String> categories = new HashSet<>();
        categories.add(doc.select("article.post p.category-title--article a").text());

        Set<String> images = new HashSet<>();
        Elements elements = doc.select("article.post section.entry-content img");
        for (Element img : elements) {
            images.add(img.attr("abs:src"));
        }

        News news = new News();
        news.setDate(date);
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content);
        news.setImages(images);

        return news;
    }
}
