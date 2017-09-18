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
public class AnandaBazarParser extends AbstractParser {
    public AnandaBazarParser() {
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (doc.select("#story_container").size() > 0) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select("#story_container .abp-story-date-div").text();
        String title = doc.select("#story_container h1").text();

        Set<String> categories = new HashSet<>();
        Elements categoryElements = doc.select(".breadcrumbs li");
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

        categoryElements = doc.select("#story_container .tag a");
        for (Element el : categoryElements) {
            String cat = el.text();
            if (!Utils.isNullOrEmpty(cat)) {
                String[] words = cat.split(" ");
                boolean isEnglish = false;
                for (String word : words) {
                    if (word.matches("\\w+")) {
                        isEnglish = true;
                    }
                }
                if (!isEnglish) {
                    cat = cat.trim();
                    categories.add(cat);
                }
            }
        }

        Set<String> images = new HashSet<>();
        Elements elements = doc.select("#story_container img.img-responsive");
        for (Element img : elements) {
            images.add(img.attr("abs:src"));
        }

        Elements paras = doc.select("#story_container .articleBody #textbody > p");
        StringBuilder content = new StringBuilder();
        for (Element para : paras) {
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
