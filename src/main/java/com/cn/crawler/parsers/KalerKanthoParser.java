package com.cn.crawler.parsers;

import com.cn.crawler.core.AbstractParser;
import com.cn.crawler.core.ParseException;
import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import com.cn.crawler.utils.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by burhan on 9/14/17.
 */
public class KalerKanthoParser extends AbstractParser {
    public KalerKanthoParser(){
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        Elements title = doc.select(".details > h2");
        Elements content = doc.select(".details .some-class-name2");

        if(title.size() > 0 && content.size() > 0){
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String title = doc.select(".details > h2").text();
        String date = doc.select(".details .text-left.pull-left.n_author").last().text();
        String content = Utils.br2nl(doc.select(".details .some-class-name2").html());

        Set<String> categories = new HashSet<>();
        Elements breadcrumb = doc.select(".details ol.breadcrumb a");
        if(breadcrumb.size() > 0) {
            String category = breadcrumb.get(breadcrumb.size() - 1).text();
            categories.add(category);
        }


        News news = new News();
        news.setDate(date);
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content);

        return news;
    }
}
