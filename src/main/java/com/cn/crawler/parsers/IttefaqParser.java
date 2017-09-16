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
public class IttefaqParser extends AbstractParser {
    public IttefaqParser(){
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if(link.getUrl().endsWith(".html") && doc.select(".detailsNews").size() > 0){
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select(".detailsNews > .reporteandentrytime > span.entrytime").text();
        String title = doc.select(".detailsNews > .headline2").text();
        String content = Utils.br2nl(doc.select(".detailsNews > .details").html());

        Set<String> categories = new HashSet<>();
        Elements els = doc.select(".detailsNews > .sitemap a");
        if(els.size() > 0) {
            boolean first = true;
            for(Element el : els){
                if(first){
                    first = false;
                    continue;
                }
                String cat = el.text();
                if(cat != null || "".equals(cat)){
                    cat = cat.trim();
                    categories.add(cat);
                }
            }
        }


        Set<String> images = new HashSet<>();
        Elements elements = doc.select(".detailsNews > .detailsimage img");
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
