package com.yue.Spider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wenyue on 16/2/18.
 */
public class TestRegex {

    @org.junit.Test
    public void testRegex(){
        String url = "http://movie.douban.com/subject/11625097/";
        String regex = "http://.*?subject/(\\d+)/";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(url);
        System.out.println(m.groupCount());

        if(m.find()){
//            System.out.println(m.group(1) + "/" + m.group(2) + "/" + m.group(3));
            System.out.println(m.group(1));
        }else{
            System.out.println("匹配錯誤!");
        }
    }

}
