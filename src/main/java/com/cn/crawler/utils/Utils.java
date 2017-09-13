package com.cn.crawler.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
        String s = document.html().replaceAll("\\\\n", "\n");
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

    public static String list2Csv(List<String> items, String seperator){
        StringBuilder sb  = new StringBuilder();
        for(String item : items){
            sb.append(item);
            sb.append(seperator);
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
}
