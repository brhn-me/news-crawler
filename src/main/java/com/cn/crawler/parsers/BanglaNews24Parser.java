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
public class BanglaNews24Parser extends AbstractParser {
    public BanglaNews24Parser(){
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if(link.getUrl().endsWith(".details")){
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select(".left-content-area > #fbz > .row > .col-sm-5.pt10 > span").text();
        String title = doc.select(".details-heading h1").text();
        String content = Utils.br2nl(doc.select("#main-article .newsitem_text").html());

        Set<String> categories = new HashSet<>();
        Elements breadcrumb = doc.select(".left-content-area ol.breadcrumb li");
        if(breadcrumb.size() > 0) {
            boolean first = true;
            for(Element li : breadcrumb){
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
        }


        Set<String> images = new HashSet<>();
        Elements elements = doc.select("#main-article table.image img");
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
