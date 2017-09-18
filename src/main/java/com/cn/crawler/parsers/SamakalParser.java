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
public class SamakalParser extends AbstractParser {
    public SamakalParser(){
    }

    @Override
    public int getPriority(Link link) {
        if(link.getUrl().contains("/article/")){
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if(link.getUrl().contains("/article/")){
            return true;
        }
        return false;
    }

    @Override
    public News parseHandler(Link link, Document doc) throws NullPointerException {

        String title = doc.select("h1.detail-headline ").text();
        String content = Utils.br2nl(doc.select(".container .description").html());

        //String date = doc.select(".time span").last().text();

        Set<String> categories = new HashSet<>();
        categories.add(doc.select("h1 span.category-title").text());

        Set<String> images = new HashSet<>();
        images.add(doc.select(".container .image-container.image img").attr("abs:src"));

        News news = new News();
        //news.setDate(date);
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content);
        news.setImages(images);

        return news;
    }
}
