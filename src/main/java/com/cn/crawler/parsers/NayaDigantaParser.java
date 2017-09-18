package com.cn.crawler.parsers;

import com.cn.crawler.core.AbstractParser;
import com.cn.crawler.core.ParseException;
import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by burhan on 5/19/17.
 */
public class NayaDigantaParser extends AbstractParser {
    public NayaDigantaParser(){
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if(link.getUrl().contains("/detail/news/")){
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select(".innerPagesLeft p.date2").text();
        if(date.indexOf("|") > 0) {
            date = date.substring(0, date.indexOf("|")).trim();
        }
        String title = doc.select(".innerPagesLeft h3.articledetail").text();
        Elements paras = doc.select(".innerPagesLeft .col-md-10.responsiveimage p");
        StringBuilder content = new StringBuilder();
        for(Element para : paras){
            content.append(para.text());
            content.append("\r\n\r\n");
        }

        Set<String> categories = new HashSet<>();
        categories.add(doc.select(".catTitles .catTitlesIn").text());


        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".innerPagesLeft .col-md-10.responsiveimage .thumbnail.with-caption img");
        for(Element img : elements){
            images.add(img.attr("abs:src"));
        }

        News news = new News();
        news.setDate(date);
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content.toString().trim());
        news.setImages(images);

        return news;
    }
}
