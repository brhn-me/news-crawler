package com.cn.crawler.utils;

import com.beust.jcommander.Strings;
import com.cn.crawler.entities.Link;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by burhan on 5/23/17.
 */
public class Utils {
    public static String br2nl(String html) {
        if(html==null)
            return html;
        Document document = Jsoup.parse(html);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document
                .html()
                .replaceAll("\\\\n", "\n")
                .replaceAll("&nbsp;", " ");

        return Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    public static String md5(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static void save2File(String path, String data) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "utf-8"))) {
            writer.write(data);
        }
    }

    public static String list2Csv(List<String> items, String separator){
        StringBuilder sb  = new StringBuilder();
        for(String item : items){
            sb.append(item);
            sb.append(separator);
        }
        String r = sb.toString();
        // remove last extra seperator
        r = r.substring(0, r.length() - 1);
        return r;
    }

    public static String list2Csv(List<String> items){
        return list2Csv(items, ",");
    }

    public static boolean isNullOrEmpty(String str){
        if(str == null){
            return true;
        }
        if("".equalsIgnoreCase(str)){
            return true;
        }
        return false;
    }

    public static Map<String, List<String>> parseQueryString(URL url) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }


    public static String getEncodedQueryString(URL url) throws UnsupportedEncodingException {
        StringBuilder r = new StringBuilder();
        Map<String, List<String>> params = parseQueryString(url);

        for(Map.Entry<String, List<String>> param : params.entrySet()){
            String key = param.getKey();
            key = URLEncoder.encode(key, "UTF-8");
            List<String> values = param.getValue();
            for(String value : values){
                value = URLEncoder.encode(value, "UTF-8");
                r.append(key + "=" + value);
                r.append("&");
            }
        }
        String output = r.toString();
        if(output.length() > 0){
            output = "?"+output;
            if(output.endsWith("&")) {
                output = output.substring(0, output.length() - 1);
            }
        }
        return output;
    }


    public static String getEncodedUrl(String str) throws MalformedURLException, UnsupportedEncodingException {
        URL url = new URL(str);
        String protocol = url.getProtocol();
        int port = url.getPort();
        String host = url.getHost();
        String path = url.getPath();
        String query = url.getQuery();

        String r = protocol + "://" + host;
        if(port != -1){
            r += ":"+port;
        }
        path = URLEncoder.encode(path, "UTF-8").replace("%2F", "/");
        if(!Utils.isNullOrEmpty(path)){
            r += path;
        }
        if(!Utils.isNullOrEmpty(query)){
            query = getEncodedQueryString(url);
            r += query;
        }
        return r;
    }

    public static String getDecodedUrl(String url) throws UnsupportedEncodingException {
        return URLDecoder.decode(url, "UTF-8");
    }

    public static String hash(String str) throws NoSuchAlgorithmException {
        return md5(str);
    }
}
