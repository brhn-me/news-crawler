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
public class BangladeshPratidinParser extends AbstractParser {
    public BangladeshPratidinParser(){
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        String url = link.getUrl();
        String articleId = url.substring(url.lastIndexOf("/")+1);
        try{
            Integer.parseInt(articleId);
        }catch (NumberFormatException ex){
            return false;
        }
        if(doc.select("#newsDtl.span12.details-bottom-margin").size() > 0){
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select("#news_update_time").first().text().substring("প্রকাশ : ".length());
        String title = doc.select("#hl2").text();
        doc.select("#newsDtl p").last().remove();
        Elements paras = doc.select("#newsDtl p");
        StringBuilder content = new StringBuilder();
        for(Element para : paras){
            content.append(para.text());
            content.append("\r\n\r\n");
        }

        Set<String> categories = new HashSet<>();
        Elements breadcrumb = doc.select("ol.breadcrumb li a");
        if(breadcrumb.size() > 1) {
            categories.add(breadcrumb.get(1).text());
        }


        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".main_image img");
        for(Element img : elements){
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
