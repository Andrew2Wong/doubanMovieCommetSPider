package com.yue.Spider.douban.model;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: abekwok
 * Create: 12/26/14
 */
public class SubjectDetail {
    private String subjectName;
    private String subjectId;
    private String director;
    private String actors;
    private String type;
    private String dict;
    private String duration;
    private String time;
    private int totalComment;
    private Map<String,String> moreInfo;

    public SubjectDetail(){}

    public SubjectDetail(String subjectId, String director, String actors, String type, String dict, String duration, String time, int totalComment, Map<String, String> moreInfo) {
        this.subjectId = subjectId;
        this.director = director;
        this.actors = actors;
        this.type = type;
        this.dict = dict;
        this.duration = duration;
        this.time = time;
        this.totalComment = totalComment;
        this.moreInfo = moreInfo;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDict() {
        return dict;
    }

    public void setDict(String dict) {
        this.dict = dict;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(int totalComment) {
        this.totalComment = totalComment;
    }

    public Map<String, String> getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(Map<String, String> moreInfo) {
        this.moreInfo = moreInfo;
    }

    @Override
    public String toString() {
        return "SubjectDetail{" +
                "subjectId='" + subjectId + '\'' +
                ", director='" + director + '\'' +
                ", actors='" + actors + '\'' +
                ", type='" + type + '\'' +
                ", dict='" + dict + '\'' +
                ", duration='" + duration + '\'' +
                ", time='" + time + '\'' +
                ", totalComment=" + totalComment +
                ", moreInfo=" + moreInfo +
                '}';
    }

    public String toGsonString() {
        Gson gson = new Gson();
        return gson.toJson(this).toString();
    }

    public static SubjectDetail fromGsonString(String gsonString) {
        Gson gson = new Gson();
        return gson.fromJson(gsonString, SubjectDetail.class);
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
