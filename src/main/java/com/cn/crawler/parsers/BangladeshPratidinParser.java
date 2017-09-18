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
    public int getPriority(Link link) {
        if(getArticleId(link.getUrl()) > 0){
            return 1;
        }
        return 0;
    }

    private int getArticleId(String url){
        String articleId = url.substring(url.lastIndexOf("/")+1);
        try{
            return Integer.parseInt(articleId);
        }catch (NumberFormatException ex){

        }
        return 0;
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        String url = link.getUrl();
        if(getArticleId(url) < 1){
            return false;
        }
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
        doc.select("#newsDtl").prepend(doc.select("#newsDtl p").text());
        doc.select("#newsDtl div").remove();
        doc.select("#newsDtl p").remove();
        String content = Utils.br2nl(doc.select("#newsDtl").html());

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
        news.setContent(content);
        news.setImages(images);

        return news;
    }
}
