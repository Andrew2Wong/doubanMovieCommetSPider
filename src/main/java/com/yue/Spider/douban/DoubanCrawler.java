package com.yue.Spider.douban;

import com.yue.Spider.douban.model.*;
import com.yue.Spider.util.HttpUtil;
import com.yue.Spider.util.IPUtil;
import com.yue.Spider.util.StringUtil;
import com.yue.Spider.util.TextUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: andrew
 * Create: 12/25/14
 */
public class DoubanCrawler implements Serializable {
    private static final long serialVersionUID = -3986244606585552569L;
    private static final String URL_TMPLT = "http://movie.douban.com/subject/%s/comments?start=%s&limit=%s&sort=time";
    //    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:33.0) Gecko/20100101 Firefox/33.0";
    private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)";
    private String cacheCookie = "bid=\"7Hku0QjxH11\"";
    private static final int SOCK_TIMEOUT = 30000;
    private static final int CONNECT_TIMEOUT = 30000;

    public List<DoubanComment> getComments(CrawlItem crawlItem) throws IOException {
        return getComments(crawlItem.getSubjectId(), crawlItem.getStart(), crawlItem.getLimit());
    }

    public List<DoubanComment> getComments(String subjectId, int start, int limit) throws IOException {
        return parse(getPage(subjectId, start, limit));
    }

    public int getTotalNum(String subjectId) throws IOException {
        SubjectDetail res = parseDetail(getPage(subjectId, 0, 10));
        if (res == null) return -1;
        return res.getTotalComment();
    }

    public SubjectDetail getSujectDetail(String subjectId) throws IOException {
        return parseDetail(getPage(subjectId, 0, 0));
    }

    public String getPage(String subjectId, int start, int limit) {
        try {
            cacheCookie = genRandomBrowserId();
            String url = genUrl(subjectId, start, limit);
            HttpClient httpClient = new HttpClient();
            List<Map.Entry<String, Integer>> ip2PortList = IPUtil.getIp2PortList();

            httpClient.setConnectionTimeout(CONNECT_TIMEOUT);
            httpClient.setTimeout(SOCK_TIMEOUT);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(SOCK_TIMEOUT);
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
            GetMethod getMethod = new GetMethod(url);

            System.out.println(url);

            getMethod.setRequestHeader("Upgrade-Insecure-Requests", "1");
            getMethod.setRequestHeader("Host", "movie.douban.com");
            getMethod.setRequestHeader("User-Agent", USER_AGENT);
            getMethod.setRequestHeader("Referer", "http://movie.douban.com/subject/" + subjectId + "/?from=showing");
            getMethod.setRequestHeader("Cookie", cacheCookie);
            getMethod.setRequestHeader("Connection", "keep-alive");
            getMethod.setRequestHeader("Pragma", "no-cache");

            httpClient.executeMethod(getMethod);
            String html = getMethod.getResponseBodyAsString();
//          System.out.println("cacheCookie: " + cacheCookie);
            getMethod.abort();

            if (isForbid(html)) {
                System.out.println("forbidden!!!!");
                String bid = "7Hku0QjxH" + Integer.toString(new Random().nextInt(9)) + Integer.toString(new Random().nextInt(9));
                cacheCookie = "bid=\"" + bid + "\"";
                HttpClient httpClientSpare = new HttpClient();
                httpClientSpare.setConnectionTimeout(CONNECT_TIMEOUT);
                httpClientSpare.setTimeout(SOCK_TIMEOUT);
                httpClientSpare.getHttpConnectionManager().getParams().setSoTimeout(SOCK_TIMEOUT);
                httpClientSpare.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
                getMethod = new GetMethod(url);
                getMethod.setRequestHeader("User-Agent", USER_AGENT);
                getMethod.setRequestHeader("Cookie", cacheCookie);
                getMethod.setRequestHeader("Referer", "http://movie.douban.com/subject/" + subjectId + "/?from=showing");


                try {
                    httpClientSpare.executeMethod(getMethod);
                    html = getMethod.getResponseBodyAsString();
                    String browserId = TextUtil.getFirstBetween(html, "browserId = '", "'");
                    cacheCookie = String.format("bid=\"%s\"", browserId);

                } catch (Exception e1) {
                    System.out.println("在更换了cookie之后还是出现了错误!");
                    System.out.println("开始使用代理IP");
                    e1.printStackTrace();

                    int ipIndex = 0;

                    if (HttpUtil.executeNewIp2Port(httpClient, getMethod, ip2PortList, ipIndex).equals("error")) {
                        if (ipIndex <= ip2PortList.size()) {
                            ipIndex++;
                        } else {
                            ipIndex = 0;
                        }
                        HttpUtil.executeNewIp2Port(httpClient, getMethod, ip2PortList, ipIndex);
                    }
                }


                getMethod.abort();
            }
            return html;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isForbid(String html) {
        if (html.contains("403 Forbidden") || !html.contains("browserId")) {
            return true;
        } else {
            return false;
        }
    }

    private SubjectDetail parseDetail(String html) {
        Document document = Jsoup.parse(html);
        //得到这个电影的详细信息
        SubjectDetail subjectDetail = getSubjectDetail(document);
        int total = -1;
        try {
            String totalStr = document.select("span.total").text();
            total = Integer.parseInt(totalStr.split(" ")[1]);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fail get total number");
        }
        subjectDetail.setTotalComment(total);

        return subjectDetail;
    }

    private List<DoubanComment> parse(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }
        List<DoubanComment> doubanComments = new LinkedList<DoubanComment>();
        Document document = Jsoup.parse(html);
        Elements commentList = document.select("div.comment-item");

        if (commentList.isEmpty())
            return null;
        for (Element comment : commentList) {
            try {
                Element u = comment.select("div.comment").select("span.comment-info").first();
                String cid = comment.attr("data-cid");
                String userName = u.select("a").text();
                String userUrl = u.select("a").attr("href");
                String userId = getUserId(userUrl);
                int star = -1;
                String time;
                if (u.select("span").size() > 2) {
                    String starStr = u.select("span").get(1).attr("class");
                    star = StringUtil.isNullOrEmpty(starStr) ? -1 : getStar(starStr);
                    time = u.select("span").get(2).text();
                } else {
                    time = u.select("span").get(1).text();
                }

                String commentText = comment.select("div.comment").select("p").text();
                String headUrl = comment.select("div.avatar").select("img").attr("src");
                DoubanComment doubanComment = new DoubanComment(cid, userName, userId, headUrl, commentText, time, star);
                doubanComments.add(doubanComment);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return doubanComments;
    }

    private SubjectDetail getSubjectDetail(Document document) {
        Elements elements = document.select("div.movie-summary").select("span.attrs").select("p");
        String subjectName = getSubjectName(document);

        SubjectDetail subjectDetail = new SubjectDetail();
        subjectDetail.setSubjectName(subjectName);

        Map<String, String> moreInfo = new HashMap<String, String>();
        for (Element element : elements) {
            String title = element.select("span.pl").text();
            String val = "";
            if (title.contains("导演")) {
                val = element.select("a").text();
                subjectDetail.setDirector(val);
            } else if (title.contains("主演")) {
                String acts = element.select("a").text();
                subjectDetail.setActors(acts);
            } else if (title.contains("类型")) {
                val = element.text();
                if (!val.isEmpty()) {
                    val = val.split(": ")[1];
                }
                subjectDetail.setType(val);
            } else if (title.contains("地区")) {
                val = element.text();
                if (!val.isEmpty()) {
                    val = val.split(": ")[1];
                }
                subjectDetail.setDict(val);
            } else if (title.contains("片长")) {
                val = element.text();
                if (!val.isEmpty()) {
                    val = val.split(": ")[1];
                }
                subjectDetail.setDuration(val);
            } else if (title.contains("上映")) {
                val = element.text();
                if (!val.isEmpty()) {
                    val = val.split(": ")[1];
                }
                subjectDetail.setTime(val);
            } else {
                val = element.text();
                if (!val.isEmpty()) {
                    val = val.split(": ")[1];
                }
                moreInfo.put(title, val);
            }
        }
        subjectDetail.setMoreInfo(moreInfo);
        return subjectDetail;
    }

    private String getSubjectName(Document document) {
        String subjectName = null;
        Elements subjectNameNode = document.select("div#content").select("h1");
        try {
            for (Element element : subjectNameNode) {
                subjectName = element.text().split(" ")[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return subjectName;
    }

    private String genUrl(String subjectId, int start, int limit) {
        return String.format(URL_TMPLT, subjectId, start, limit);
    }

    private int getStar(String starStr) {
        try {
            return Integer.parseInt(TextUtil.getFirstBetween(starStr, "star", " "));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fail get star: " + starStr);
            return -1;
        }
    }

    private String getUserId(String userUrl) {
        try {
            String sps[] = userUrl.split("/");
            if (sps[sps.length - 1].isEmpty()) {
                return sps[sps.length - 2];
            } else {
                return sps[sps.length - 1];
            }
        } catch (Exception e) {
            e.getMessage();
            System.out.println("fail getUserId: " + userUrl);
            return "-1";
        }
    }

    private static Random randGen = null;
    private static char[] numbersAndLetters = null;

    private String genRandomBrowserId() {
        return String.format("bid=\"%s\"", randomString(11));
    }

    private static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            randGen = new Random();
            numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
                    "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

}
