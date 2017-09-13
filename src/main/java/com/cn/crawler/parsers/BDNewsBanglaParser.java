package com.cn.crawler.parsers;

import com.cn.crawler.core.AbstractParser;
import com.cn.crawler.entities.Link;
import com.cn.crawler.core.ParseException;
import com.cn.crawler.utils.Utils;
import com.cn.crawler.entities.News;
import org.jsoup.nodes.Document;

/**
 * Created by burhan on 5/19/17.
 */
public class BDNewsBanglaParser extends AbstractParser {
    public BDNewsBanglaParser(){
        super("http://bangla.bdnews24.com");
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if(!link.getUrl().endsWith(".bdnews")){
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select("#main .dateline span").last().text();
        String title = doc.select("#main .article h1").text();
        String category = doc.select("#main .breadcrumb a").last().text();
        String content = Utils.br2nl(doc.select("#main .article_body").html());

        News news = new News();
        news.setUrl(link.getUrl());
        news.setDate(date);
        news.setTitle(title);
        //news.setCategory(category);
        news.setContent(content);

        return news;
    }
}
