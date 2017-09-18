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
public class ManabZaminParser extends AbstractParser {
    public ManabZaminParser() {
    }

    @Override
    public int getPriority(Link link) {
        if (link.getUrl().contains("article.php?mzamin=")) {
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (link.getUrl().contains("article.php?mzamin=")) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        doc.select(".container .details-source span").remove();

        String date = doc.select(".container .details-source").text().trim().substring(1);
        String title = doc.select(".container h1").text();


        Set<String> categories = new HashSet<>();
        categories.add(doc.select(".container h1").first().nextElementSibling().text());

        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".container .details-text .details-image img");
        for (Element img : elements) {
            images.add(img.attr("abs:src"));
        }

        doc.select(".container .details-text div").remove();
        String content = Utils.br2nl(doc.select(".container .details-text").html());

        News news = new News();
        news.setDate(date);
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content);
        news.setImages(images);

        return news;
    }
}
