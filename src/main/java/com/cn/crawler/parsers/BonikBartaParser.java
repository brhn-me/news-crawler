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
public class BonikBartaParser extends AbstractParser {
    public BonikBartaParser() {
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (link.getUrl().contains("http://bonikbarta.net/bangla/news")) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        System.out.println(doc.outerHtml());
        Elements dateEl = doc.select(".news .news-heading p.meta span.date-pub");
        String date = dateEl.select("date").text() + dateEl.select("time").text();

        String title = doc.select(".news h1.heading").text();
        String content = Utils.br2nl(doc.select("article .news .content").html());

        Set<String> categories = new HashSet<>();
        categories.add(doc.select(".news .cats-of p.cat-name").text());

        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".news .content .thumbs img");
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
