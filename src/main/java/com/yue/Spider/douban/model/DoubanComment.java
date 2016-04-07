package com.yue.Spider.douban.model;



import com.yue.Spider.util.TextUtil;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Author: abekwok
 * Create: 12/25/14
 */
public class DoubanComment implements Serializable{
    private static final long serialVersionUID = -3986244606585552569L;
    private String cid;
    private String userName;
    private String userId;
    private String headUrl;
    private String comment;
    private String time;
    private int star;

    public DoubanComment(){}

    public DoubanComment(String cid, String userName, String userId, String headUrl, String comment, String time, int star) {
        this.cid = cid;
        this.userName = userName;
        this.userId = userId;
        this.headUrl = headUrl;
        this.comment = comment;
        this.time = time;
        this.star = star;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    @Override
    public String toString() {
        return "DoubanComment{" +
                "cid='" + cid + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", headUrl='" + headUrl + '\'' +
                ", comment='" + comment + '\'' +
                ", time='" + time + '\'' +
                ", star=" + star +
                '}';
    }

    public static DoubanComment fromToString(String toString) {
        String cid = TextUtil.getFirstBetween(toString, "cid='", "'");
        String userName = TextUtil.getFirstBetween(toString, "userName='", "'");
        String userId = TextUtil.getFirstBetween(toString, "userId='", "'");
        String headUrl = TextUtil.getFirstBetween(toString, "headUrl='", "'");
        String comment = TextUtil.getFirstBetween(toString, "comment='", "'");
        String time = TextUtil.getFirstBetween(toString, "time='", "'");
        String star = TextUtil.getFirstBetween(toString, "star=", "}");
        return new DoubanComment(cid, userName, userId, headUrl, comment, time, Integer.parseInt(star));
    }
}
