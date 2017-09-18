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
public class JugantarParser extends AbstractParser {
    public JugantarParser() {
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (doc.select(".home_page_left_dtl").size() > 0 && doc.select("div#hl2").size() > 0) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select(".home_page_left_dtl div#rpt").first().nextElementSibling().text().substring("প্রকাশ : ".length());
        if(!Utils.isNullOrEmpty(date)){
            date = date.substring(0, date.indexOf("|")).trim();
        }
        String title = doc.select(".home_page_left_dtl div#hl2").text();
        Set<String> categories = new HashSet<>();
        categories.add(doc.select("#siteMap ul li a").last().text());

        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".home_page_left_dtl #img img");
        for (Element img : elements) {
            images.add(img.attr("abs:src"));
        }

        doc.select(".home_page_left_dtl  div#myText div").remove();
        String content = Utils.br2nl(doc.select(".home_page_left_dtl  div#myText").html()).trim();

        News news = new News();
        news.setDate(date);
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content);
        news.setImages(images);

        return news;
    }
}
