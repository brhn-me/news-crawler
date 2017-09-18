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
public class BanglaTribuneParser extends AbstractParser {
    public BanglaTribuneParser() {
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (link.getUrl().contains("/news/") && doc.select(".detail_article article").size() > 0) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select(".detail_article .time_info .time").first().attr("data-published");
        String title = doc.select(".detail_article h2.title_holder span.title").text();
        String content = Utils.br2nl(doc.select(".detail_article article").html());

        Set<String> categories = new HashSet<>();
        Elements categoryElements = doc.select(".breadcrumb li");
        boolean first = true;
        for(Element li : categoryElements){
            if(first){
                first = false;
                continue;
            }
            String cat = li.text();
            if(cat != null || "".equals(cat)){
                cat = cat.trim();
                categories.add(cat);
            }
        }

        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".detail_article article img");
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
