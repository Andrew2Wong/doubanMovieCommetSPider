package com.yue.Spider.util;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by andrew on 16/2/19.
 */
public class IPUtil {

    public static Map<String, Integer> getIps() {
        Map<String,Integer> ipPool = new HashMap<String, Integer>();
        String url = "http://www.kuaidaili.com/proxylist/1/";
        HttpClient client = HttpUtil.getCommonClient();
        GetMethod getMethod = new GetMethod(url);
        String html = null;

        getMethod.setRequestHeader("Upgrade-Insecure-Requests", "1");
        getMethod.setRequestHeader("Host", "www.kuaidaili.com\n");
        getMethod.setRequestHeader("User-Agent", SparkConsts.UA_PC_CHROME);
        getMethod.setRequestHeader("Connection", "keep-alive");
        getMethod.setRequestHeader("Pragma", "no-cache");
        getMethod.setRequestHeader("Referer", "http://www.kuaidaili.com/proxylist/2/");

        try {
            client.executeMethod(getMethod);
            html = getMethod.getResponseBodyAsString();
            Document doc = Jsoup.parse(html);
            Elements ipMessageList = doc.select("div#list").select("table").select("tbody").select("tr");
            for(Element ipMessage : ipMessageList){
                String ip = ipMessage.select("td").first().html();
                Integer port = Integer.parseInt(ipMessage.select("td").get(1).html());
                ipPool.put(ip, port);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ipPool;
    }


    public static List<Map.Entry<String, Integer>> getIp2PortList() {
        Map<String, Integer> ipPool = IPUtil.getIps();
        List<Map.Entry<String, Integer>> ip2PortList = new LinkedList<Map.Entry<String, Integer>>();

        for (Map.Entry<String, Integer> ip2Port : ipPool.entrySet()) {
            ip2PortList.add(ip2Port);
        }

        return ip2PortList;
    }
}
