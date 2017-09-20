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
public class GonoKanthoParser extends AbstractParser {
    private static final Logger log = LoggerFactory.getLogger(GonoKanthoParser.class);

    public GonoKanthoParser() {
    }

    @Override
    public int getPriority(Link link) {
        if (link.getUrl().contains("details.php?id=")) {
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (link.getUrl().contains("details.php?id=")) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select("#toPrint div.pub span").first().text();
        if (!Utils.isNullOrEmpty(date)) {
            String removalStr = "Published";
            int i = date.indexOf(removalStr);
            if (i > 0) {
                i = date.indexOf(":");
                if (i > 0) {
                    date = date.substring(i + 1);
                }
            }
            date = date.trim();
        }
        String title = doc.select("#toPrint .title_detail").text();
        String content = Utils.br2nl(doc.select("#toPrint table tr td #f").html());

        Set<String> categories = new HashSet<>();
        Elements categoryElements = doc.select("article.DetailsContent ol.breadcrumb li a");
        if (categoryElements.size() >= 2) {
            categories.add(categoryElements.get(1).text());
        }

        Set<String> images = new HashSet<>();
        Elements elements = doc.select("#toPrint table tr td #f img");
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
