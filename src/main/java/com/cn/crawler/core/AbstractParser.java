package com.cn.crawler.core;

import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import com.cn.crawler.parsers.*;
import com.cn.crawler.utils.Utils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by burhan on 5/19/17.
 */
public abstract class AbstractParser {
    private static final Logger log = LoggerFactory.getLogger(Fetcher.class);

    public AbstractParser() {
    }

    protected abstract boolean isParsable(Link link, Document doc) throws ParseException;

    protected abstract News parseHandler(Link link, Document doc) throws ParseException;

    public News parse(Link link, Document doc) throws ParseException {
        if (isParsable(link, doc)) {
            try {
                log.debug(" - Parsing: " + link.getUrl());
                News news = parseHandler(link, doc);
                if (news != null) {
                    try {
                        news.setUrl(link.getUrl());
                        news.setId(link.getHash());
                        news.setHash(Utils.hash(news.getContent()));
                        news.setHost(link.getHost());
                        news.setModified(new Date());
                    } catch (NoSuchAlgorithmException e) {
                        throw new ParseException("Failed to generate (id, hash) from (url, content) for link : " + link
                                .toString(), e);
                    }
                }
                return news;
            } catch (Exception ex) {
                throw new ParseException("Failed to parse : " + link.toString(), ex);
            }
        }
        return null;
    }

    public static void test(String url, Class c) {
        Link link = null;
        try {
            link = new Link(url, 0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("Testing : " + link.getUrl());
        try {
            url = Utils.getEncodedUrl(url);
            Connection.Response response = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .referrer("http://www.google.com")
                    .timeout(60 * 1000)
                    .execute();
            Document doc = response.parse();
            AbstractParser parser = (AbstractParser) c.newInstance();


            News news = parser.parse(link, doc);
            if (news != null) {
                System.out.println(news.toString());
            } else {
                System.err.println("Parse failed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        //test("http://www.prothom-alo.com/sports/article/1323321/%E0%A6%AC%E0%A6%BE%E0%A6%82%E0%A6%B2%E0%A6%BE%E0%A6%A6%E0%A7%87%E0%A6%B6-%E0%A6%97%E0%A7%87%E0%A6%B2%E0%A7%87%E0%A6%87-%E0%A6%AE%E0%A6%BE%E0%A6%A0%E0%A6%97%E0%A7%81%E0%A6%B2%E0%A7%8B%E0%A6%B0-%E2%80%98%E0%A6%A4%E0%A6%BE%E0%A6%B2%E0%A6%BE-%E0%A6%96%E0%A7%8B%E0%A6%B2%E0%A7%87%E2%80%99", ProthomAloParser.class);
        //test("http://bangla.bdnews24.com/politics/article1393999.bdnews", BDNews24BanglaParser.class);
        //test("http://www.kalerkantho.com/online/national/2017/09/14/542604", KalerKanthoParser.class);
        //test("http://www.samakal.com/sports/article/1709801/%E0%A6%AA%E0%A7%8D%E0%A6%B0%E0%A6%A5%E0%A6%AE-%E0%A6%86%E0%A6%AB%E0%A6%97%E0%A6%BE%E0%A6%A8-%E0%A6%B9%E0%A6%BF%E0%A6%B8%E0%A7%87%E0%A6%AC%E0%A7%87-%E0%A6%AC%E0%A6%BF%E0%A6%97-%E0%A6%AC%E0%A7%8D%E0%A6%AF%E0%A6%BE%E0%A6%B6%E0%A7%87-%E0%A6%B0%E0%A6%B6%E0%A6%BF%E0%A6%A6", SamakalParser.class);
        //test("http://www.samakal.com/technology/article/1709703/এলো-আইফোন-এক্স", SamakalParser.class);
        //test("http://www.ittefaq.com.bd/wholecountry/2017/09/16/127562.html", IttefaqParser.class);
        //test("http://www.bd-pratidin.com/tech-world/2017/09/16/264490", BangladeshPratidinParser.class);
        //test("http://bonikbarta.net/bangla/news/2017-09-18/131580/%E0%A6%96%E0%A6%BE%E0%A6%A6%E0%A7%8D%E0%A6%AF-%E0%A6%B8%E0%A6%82%E0%A6%95%E0%A6%9F%E0%A7%87-%E0%A6%AA%E0%A7%9C%E0%A6%A4%E0%A7%87-%E0%A6%AA%E0%A6%BE%E0%A6%B0%E0%A7%87-%E0%A6%B6%E0%A6%B0%E0%A6%A3%E0%A6%BE%E0%A6%B0%E0%A7%8D%E0%A6%A5%E0%A7%80%E0%A6%B0%E0%A6%BE%E2%80%94%E0%A6%B8%E0%A7%87%E0%A6%AD-%E0%A6%A6%E0%A7%8D%E0%A6%AF-%E0%A6%9A%E0%A6%BF%E0%A6%B2%E0%A6%A1%E0%A7%8D%E0%A6%B0%E0%A7%87%E0%A6%A8/", BonikBartaParser.class);
        //test("http://www.dailynayadiganta.com/detail/news/244339", NayaDigantaParser.class);
        //test("http://www.banglatribune.com/sport/news/217209/%E0%A6%B9%E0%A6%95%E0%A6%BF%E0%A6%B0-%E0%A6%B8%E0%A6%BE%E0%A6%AB%E0%A6%B2%E0%A7%8D%E0%A6%AF%E0%A7%87-%E0%A6%95%E0%A7%8D%E0%A6%B0%E0%A6%BF%E0%A6%95%E0%A7%87%E0%A6%9F%E0%A7%87%E0%A6%B0-%E0%A6%AC%E0%A7%8D%E0%A6%AF%E0%A6%B0%E0%A7%8D%E0%A6%A5%E0%A6%A4%E0%A6%BE-%E0%A6%AD%E0%A7%81%E0%A6%B2%E0%A6%9B%E0%A7%87-%E0%A6%AD%E0%A6%BE%E0%A6%B0%E0%A6%A4", BanglaTribuneParser.class);
        //test("http://www.mzamin.com/article.php?mzamin=83578", ManabZaminParser.class);
        //test("http://www.bhorerkagoj.net/এনামুলের-ডাবল-সেঞ্চুরি", BhorerKagojParser.class);
        //test("http://www.dailyjanakantha.us/details/article/293735/%E0%A6%A8%E0%A6%BE%E0%A6%87%E0%A6%95%E0%A7%8D%E0%A6%B7%E0%A7%8D%E0%A6%AF%E0%A6%82%E0%A6%9B%E0%A7%9C%E0%A6%BF%E0%A6%A4%E0%A7%87-%E0%A6%B8%E0%A7%8D%E0%A6%A5%E0%A6%B2%E0%A6%AE%E0%A6%BE%E0%A6%87%E0%A6%A8-%E0%A6%AC%E0%A6%BF%E0%A6%B8%E0%A7%8D%E0%A6%AB%E0%A7%8B%E0%A6%B0%E0%A6%A3%E0%A7%87-%E0%A6%AC%E0%A6%BE%E0%A6%82%E0%A6%B2%E0%A6%BE%E0%A6%A6%E0%A7%87%E0%A6%B6%E0%A6%BF-%E0%A6%A8%E0%A6%BF%E0%A6%B9%E0%A6%A4", JanakanthaParser.class);
        //test("https://www.jugantor.com/online/sports/2017/09/18/58213/%E0%A6%A1%E0%A6%BE%E0%A6%AC%E0%A6%B2-%E0%A6%B8%E0%A7%87%E0%A6%9E%E0%A7%8D%E0%A6%9A%E0%A7%81%E0%A6%B0%E0%A6%BF%E0%A6%A4%E0%A7%87-%E0%A6%AE%E0%A6%BE%E0%A6%A0%E0%A7%87-%E0%A6%86%E0%A6%B2%E0%A7%8B-%E0%A6%9B%E0%A7%9C%E0%A6%BE%E0%A6%B2%E0%A7%87%E0%A6%A8-%E0%A6%B8%E0%A7%87%E0%A6%87-%E0%A6%8F%E0%A6%A8%E0%A6%BE%E0%A6%AE%E0%A7%81%E0%A6%B2", JugantarParser.class);
        //test("http://www.anandabazar.com/bangladesh-news/bengal-will-listen-the-untold-story-of-bangladesh-1.676555?ref=bangladesh-news-ft-stry", AnandaBazarParser.class);
        test("http://www.bbc.com/bengali/news-41303904", BBCBanglaParser.class);

    }
}
