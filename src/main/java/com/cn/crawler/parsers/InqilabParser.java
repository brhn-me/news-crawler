package com.cn.crawler.parsers;

import com.cn.crawler.core.AbstractParser;
import com.cn.crawler.core.ParseException;
import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import com.cn.crawler.utils.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by burhan on 5/19/17.
 */
public class InqilabParser extends AbstractParser {
    private static final Logger log = LoggerFactory.getLogger(InqilabParser.class);

    public InqilabParser() {
    }

    @Override
    public int getPriority(Link link) {
        if (link.getUrl().contains("/article/")) {
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (link.getUrl().contains("/article/")) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select(".home_top_left h5").text().trim();
        String removalStr = "প্রকাশের সময়";
        int i = date.indexOf(removalStr);
        if (i > 0) {
            i = date.indexOf(":");
            if (i > 0) {
                date = date.substring(i + 1);
            }
        }
        date = date.trim();
        String title = doc.select(".home_top_left .row h1").text();

        Set<String> categories = new HashSet<>();
        Elements categoryElements = doc.select(".home_top_left .row ol.breadcrumb li a");
        if (categoryElements.size() >= 2) {
            categories.add(categoryElements.get(1).text());
        }

        Elements paras = doc.select(".home_top_left .row div").get(1).select("p");
        StringBuilder content = new StringBuilder();
        for (Element para : paras) {
            content.append(para.text());
            content.append("\r\n\r\n");
        }

        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".home_top_left .row div").get(1).select("img");
        ;
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
