package yoyorony.me.RSSReader;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class RSSReader {
    public static Feed ReadRSS(URL url) throws IOException {
        Feed feed = new Feed();
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(6000);
        connection.setReadTimeout(6000);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String sourceCode = "";
        String line;
        while ((line = in.readLine()) != null) {
            sourceCode += line;
        }
        in.close();

        feed.setTitle(findTitle(sourceCode));
        feed.setDescription(findDescription(sourceCode));
        feed.setPubDate(findPubDate(sourceCode));
        feed.setLastBuildDate(findLastBuildDate(sourceCode));
        feed.setGenerator(findGenerator(sourceCode));
        feed.setLink(findLink(sourceCode));
        feed.setItems(findItems(sourceCode));

        return feed;
    }

    private static String findTitle(String source) {
        String temp = source.substring(source.indexOf("<title>"));
        temp = temp.replace("<title>", "");
        return temp.substring(0, temp.indexOf("</title>"));
    }

    private static String findDescription(String source) {
        String temp = source.substring(source.indexOf("<description>"));
        temp = temp.replace("<description>", "");
        return temp.substring(0, temp.indexOf("</description>"));
    }

    private static String findPubDate(String source) {
        String temp = source.substring(source.indexOf("<pubDate>"));
        temp = temp.replace("<pubDate>", "");
        return temp.substring(0, temp.indexOf("</pubDate>"));
    }

    private static String findLastBuildDate(String source) {
        String temp = source.substring(source.indexOf("<lastBuildDate>"));
        temp = temp.replace("<lastBuildDate>", "");
        return temp.substring(0, temp.indexOf("</lastBuildDate>"));
    }

    private static String findGenerator(String source) {
        String temp = source.substring(source.indexOf("<generator>"));
        temp = temp.replace("<generator>", "");
        return temp.substring(0, temp.indexOf("</generator>"));
    }

    private static String findLink(String source) {
        String temp = source.substring(source.indexOf("<link>"));
        temp = temp.replace("<link>", "");
        return temp.substring(0, temp.indexOf("</link>"));
    }

    private static ArrayList<Item> findItems(String source) {
        ArrayList<Item> items = new ArrayList<>();
        int index;
        int fromIndex = 0;
        while ((index = source.indexOf("<item>", fromIndex)) != -1) {
            fromIndex = index + 1;
            Item item = new Item();

            item.setTitle(findItemTitle(source, index));
            item.setPubDate(findItemPubDate(source, index));
            item.setLink(findItemLink(source, index));
            item.setGuid(findItemGuid(source, index));
            item.setAuthor(findItemAuthor(source, index));
            item.setCreator(findItemCreator(source, index));
            item.setContent(findItemContent(source, index));

            items.add(item);
        }
        return items;
    }

    private static String findItemTitle(String source, int index) {
        String temp = source.substring(source.indexOf("<title>", index));
        temp = temp.replace("<title>", "");
        return temp.substring(0, temp.indexOf("</title>"));
    }

    private static String findItemPubDate(String source, int index) {
        String temp = source.substring(source.indexOf("<pubDate>", index));
        temp = temp.replace("<pubDate>", "");
        return temp.substring(0, temp.indexOf("</pubDate>"));
    }

    private static String findItemLink(String source, int index) {
        String temp = source.substring(source.indexOf("<link>", index));
        temp = temp.replace("<link>", "");
        return temp.substring(0, temp.indexOf("</link>"));
    }

    private static String findItemGuid(String source, int index) {
        String temp = source.substring(source.indexOf("<guid>", index));
        temp = temp.replace("<guid>", "");
        return temp.substring(0, temp.indexOf("</guid>"));
    }

    private static String findItemAuthor(String source, int index) {
        String temp = source.substring(source.indexOf("<author>", index));
        temp = temp.replace("<author>", "");
        return temp.substring(0, temp.indexOf("</author>"));
    }

    private static String findItemCreator(String source, int index) {
        String temp = source.substring(source.indexOf("<dc:creator>", index));
        temp = temp.replace("<dc:creator>", "");
        return temp.substring(0, temp.indexOf("</dc:creator>"));
    }

    private static String findItemContent(String source, int index) {
        String temp = source.substring(source.indexOf("<content:encoded>", index));
        temp = temp.replace("<content:encoded>", "");
        return temp.substring(0, temp.indexOf("</content:encoded>"));
    }
}
