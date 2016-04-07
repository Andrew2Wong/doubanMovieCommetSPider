package com.yue.Spider;


import com.yue.Spider.spider.DoubanMovieCli;
import org.junit.Test;


/**
 * Created by wenyue on 16/2/18.
 */
public class TestMovieCli {

    private static final String URL = "https://www.baidu.com/s?wd=";

    @Test
    public void testMovieCli(){
        DoubanMovieCli.start("澳门风云");
    }
}
