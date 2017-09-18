package com.cn.crawler.parsers;

import com.cn.crawler.core.AbstractParser;
import com.cn.crawler.entities.Link;
import com.cn.crawler.core.ParseException;
import com.cn.crawler.utils.Utils;
import com.cn.crawler.entities.News;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by burhan on 5/19/17.
 */
public class BDNews24BanglaParser extends AbstractParser {
    public BDNews24BanglaParser(){
    }

    @Override
    public int getPriority(Link link) {
        if(link.getUrl().endsWith(".bdnews")){
            return 1;
        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if(link.getUrl().endsWith(".bdnews")){
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select("#main .dateline span").last().text();
        String title = doc.select("#main .article h1").text();
        String content = Utils.br2nl(doc.select("#main .article_body").html());

        Set<String> categories = new HashSet<>();
        Elements breadcrumb = doc.select("#main .breadcrumb a");
        if(breadcrumb.size() > 0) {
            String category = breadcrumb.last().text();
            categories.add(category);
        }

        Set<String> images = new HashSet<>();
        Elements elements = doc.select("#main .article_body img");
        for(Element img : elements){
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
