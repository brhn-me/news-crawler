package com.cn.crawler.parsers;

import com.cn.crawler.core.AbstractParser;
import com.cn.crawler.entities.Link;
import com.cn.crawler.core.ParseException;
import com.cn.crawler.utils.Utils;
import com.cn.crawler.entities.News;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by burhan on 5/19/17.
 */
public class ProthomAloParser extends AbstractParser {
    public ProthomAloParser(){
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
        String date = doc.select(".time span").last().text();
        String title = doc.select(".content_detail h1.title").text();
        String content = Utils.br2nl(doc.select("article.content").html());

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
        Elements elements = doc.select("article.content img");
        for(Element img : elements){
            images.add(img.attr("src"));
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
