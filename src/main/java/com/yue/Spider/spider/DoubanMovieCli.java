package com.yue.Spider.spider;


import com.yue.Spider.douban.DoubanCommentCrawlerRunner;
import com.yue.Spider.util.HttpUtil;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by andrew on 16/2/13.
 */
public class DoubanMovieCli {

    private static final String URL = "https://www.baidu.com/s?wd=";
    private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)";
    private static final int SOCK_TIMEOUT = 30000;
    private static final int CONNECT_TIMEOUT = 30000;

    public static void main(String[] args) {
        System.out.println("请输入电影的名字");
        Scanner scanner = new Scanner(System.in);
        String movie = scanner.next();
        start(movie);
    }

    public static void start(String movie) {

        try {
            String html = HttpUtil.getDocumentString(URL + URLEncoder.encode(movie + "豆瓣电影"));
            Document document = Jsoup.parse(html);
            Elements content = document.select("div#content_left");
            Element firstResult = content.select("div#1").first();
            String href = firstResult.select("h3.t").select("a").attr("href");
            System.out.println("重定向的url是:" + href);

            String movieID = getMovieID(href);

            saveComments2Local(movieID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveComments2Local(String subjectID) {
        DoubanCommentCrawlerRunner crawler = new DoubanCommentCrawlerRunner(subjectID);
        crawler.runSingleSubject(subjectID);
    }

    private static String getMovieID(String url) {
        String movieID = null;
        String newURL = null;

        GetMethod getMethod = HttpUtil.getCommonGetMethod(url);
        try {
            newURL = getMethod.getURI().toString();
        } catch (URIException e) {
            e.printStackTrace();
        }


        if (newURL != null && !newURL.equals("")) {
            System.out.println("新的url是:" + newURL);

            movieID = parseURL2ID(newURL);

            System.out.println("电影的ID是:" + movieID);
        }

        return movieID;
    }

    private static String parseURL2ID(String url) {
        String subjectID = null;
        String regex = null;

        if(url.contains("https")){
            regex = "https://.*?subject/(\\d+)/";
        }else{
            regex = "http://.*?subject/(\\d+)/";
        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(url);

        if(m.find()){
            subjectID = m.group(1);
        }else{
            System.out.println("匹配错误!");
        }

        return subjectID;
    }

}
