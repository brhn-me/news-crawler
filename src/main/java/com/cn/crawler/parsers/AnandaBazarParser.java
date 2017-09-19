package com.cn.crawler.parsers;

import com.cn.crawler.core.AbstractParser;
import com.cn.crawler.core.Fetcher;
import com.cn.crawler.core.ParseException;
import com.cn.crawler.entities.Link;
import com.cn.crawler.entities.News;
import com.cn.crawler.utils.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by burhan on 5/19/17.
 */
public class AnandaBazarParser extends AbstractParser {
    private static final Logger log = LoggerFactory.getLogger(AnandaBazarParser.class);

    public AnandaBazarParser() {
    }

    @Override
    protected boolean isParsable(Link link, Document doc) throws ParseException {
        if (doc.select("#story_container").size() > 0) {
            return true;
        }
        return false;
    }

    public News parseHandler(Link link, Document doc) throws NullPointerException {
        String date = doc.select("#story_container .abp-story-date-div").text();
        String title = doc.select("#story_container h1").text();

        Set<String> categories = new HashSet<>();
        Elements categoryElements = doc.select(".breadcrumbs li");
        boolean first = true;
        for (Element li : categoryElements) {
            if (first) {
                first = false;
                continue;
            }
            String cat = li.text();
            if (cat != null || "".equals(cat)) {
                cat = cat.trim();
                categories.add(cat);
            }
        }

        categoryElements = doc.select("#story_container .tag a");
        for (Element el : categoryElements) {
            String cat = el.text();
            if (!Utils.isNullOrEmpty(cat)) {
                String[] words = cat.split(" ");
                boolean isEnglish = false;
                for (String word : words) {
                    if (word.matches("\\w+")) {
                        isEnglish = true;
                    }
                }
                if (!isEnglish) {
                    cat = cat.trim();
                    categories.add(cat);
                }
            }
        }

        Set<String> images = new HashSet<>();
        Elements elements = doc.select("#story_container img.img-responsive");
        for (Element img : elements) {
            images.add(img.attr("abs:src"));
        }

        Elements paras = doc.select("#story_container .articleBody #textbody > p");
        StringBuilder content = new StringBuilder();
        for (Element para : paras) {
            content.append(para.text());
            content.append("\r\n\r\n");
        }

        News news = new News();
        news.setDate(date);
        news.setTitle(title);
        news.setCategories(categories);
        news.setContent(content.toString());
        news.setImages(images);

        return news;
    }

    @Override
    public URI normalize(URI uri) {
        try {
            Map<String, List<String>> params = Utils.parseQueryString(uri.toURL());
            params.remove("ref");
            String queryString = Utils.getQueryString(params);
            uri = new URI(uri.getScheme(),
                    uri.getUserInfo(), uri.getHost(), uri.getPort(),
            uri.getPath(), queryString, null);
            return uri;

        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
        } catch (NullPointerException e){
            System.err.println(uri.toString());
        }
        return uri;
    }
}
