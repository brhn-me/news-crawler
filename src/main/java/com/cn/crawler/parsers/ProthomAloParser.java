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
import java.util.List;

/**
 * Created by burhan on 5/19/17.
 */
public class ProthomAloParser extends AbstractParser {
    public ProthomAloParser(){
        super("http://www.prothom-alo.com");
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
        List<String> categories = new ArrayList<>();
        Elements categoryElements = doc.select(".breadcrumb li");
        for(Element li : categoryElements){
            String cat = li.text();
            if(cat != null || "".equals(cat)){
                cat = cat.trim();
                categories.add(cat);
            }
        }
        String category = Utils.list2Csv(categories);
        String content = Utils.br2nl(doc.select("article.content").html());

        News news = new News();
        news.setUrl(link.getUrl());
        news.setDate(date);
        news.setTitle(title);
        //news.setCategory(category);
        news.setContent(content);

        return news;
    }
}
