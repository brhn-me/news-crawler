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
public class SangramParser extends AbstractParser {
    private static final Logger log = LoggerFactory.getLogger(SangramParser.class);

    public SangramParser() {
    }

    @Override
    public int getPriority(Link link) {
        if (link.getUrl().contains("/post/")) {
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (link.getUrl().contains("/post/")) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select(".postDetails .postInfo").text().trim();
        String removalStr = "প্রকাশিত";
        int i = date.indexOf(removalStr);
        if (i > 0) {
            i = date.indexOf(":");
            if (i > 0) {
                date = date.substring(i + 1);
            }
            i = date.indexOf("|");
            if (i > 0) {
                date = date.substring(0, i);
            }
        }
        date = date.trim();
        String title = doc.select(".postDetails h1").text();

        Set<String> categories = new HashSet<>();
        Elements categoryElements = doc.select(".mainSubMenu ul.breadcrumb li a");
        boolean first = true;
        for (Element li : categoryElements) {
            if (first) {
                first = false;
                continue;
            }
            String cat = li.text();
            if (cat != null || "".equals(cat)) {
                cat = cat.trim();
                categories.add(cat);
            }
        }

        String content = Utils.br2nl(doc.select(".postDetails .postBody").html());

        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".postDetails .postMedia img");
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
