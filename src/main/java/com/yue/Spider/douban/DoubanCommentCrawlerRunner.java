package com.yue.Spider.douban;

import com.yue.Spider.douban.model.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.*;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Author: andrew
 */
public final class DoubanCommentCrawlerRunner implements Serializable {
    private static Log LOG = LogFactory.getLog(DoubanCommentCrawlerRunner.class);
    private static final long serialVersionUID = -3986244606585552569L;
    private String sparkUrl;
    private String[] jars;
    private JavaSparkContext jsc;
    private String subjectId;
    private String subjectName;
    private String destFile;
    private String outputFilename;

    List<String> subjectIds = new ArrayList<String>();
    private int commentSize = 100;

    public void setJars(String[] jars) {
        this.jars = jars;
    }

    public DoubanCommentCrawlerRunner(String subjectId) {
        this.subjectId = subjectId;
    }

    public DoubanCommentCrawlerRunner(String subjectId, List<String> subjectIds, int commentSize) {
        this.subjectId = subjectId;
        this.subjectIds = subjectIds;
        this.commentSize = commentSize;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    @Override
    public String toString() {
        return "DoubanCommentCrawlerRunner{" +
                "subjectId=" + subjectId +
                ", commentSize=" + commentSize +
                ", outputFilename=" + outputFilename +
                '}';
    }


    public void runSingleSubject(String subjectId) {
        LOG.info("runSubject:" + subjectId);
        try {

            stepInit(subjectId);
            List<CrawlItem> items;

            items = stepGenItems(subjectId);
            stepCrawlComment(items);
            jsc.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jsc != null) jsc.stop();
        }
    }

    private static final int PAGE_LIMIT = 50;

    private List<CrawlItem> stepGenItems(String subjectId) throws IOException {
        System.out.println("start gen items");
        List<CrawlItem> items = new LinkedList<CrawlItem>();
        DoubanCrawler doubanCrawler = new DoubanCrawler();
        int totalNum = doubanCrawler.getTotalNum(subjectId);

        int maxPage = 100;
        for (int i = 0; i < maxPage; i++) {
            items.add(new CrawlItem(subjectId, i * PAGE_LIMIT, PAGE_LIMIT));
        }
        System.out.println("totalNum: " + totalNum);
        System.out.println("maxPage: " + maxPage);
        return items;
    }





    private void stepInit(String subjectId) {
        String sparkHome = System.getenv("SPARK_HOME");
        SparkConf conf = new SparkConf()
                .setMaster("local[4]")
                .setAppName("DoubanCommentCrawl-" + subjectId)
                .setJars(jars != null ? jars : JavaSparkContext.jarOfClass(DoubanCommentCrawlerRunner.class));
        conf = conf.set("spark.cores.max", "2");
        conf = conf.set("spark.default.parallelism", "8");
        if (sparkHome != null) {
            System.out.println("SparkHome: " + sparkHome);
            conf = conf.setSparkHome(sparkHome);
        }

        jsc = new JavaSparkContext(conf);
    }

    private void stepCrawlComment(List<CrawlItem> crawlItems) {
        System.out.println("start crawl douban comment in spark");
        JavaRDD<CrawlItem> itemJavaRDD = jsc.parallelize(crawlItems);
        List<DoubanComment> comments = itemJavaRDD.repartition(40).flatMap(new DoubanCommentCrawlerMap()).collect();

        System.out.println("comments " + comments.size());
        List<String> commentStrs = new ArrayList<String>();

        for (DoubanComment doubanComment : comments) {
            commentStrs.add(doubanComment.getUserName() + "\t"
                    + doubanComment.getComment() + "\t"
                    + doubanComment.getTime() + "\t"
                    + doubanComment.getStar());
        }

        try {

            FileUtils.writeLines(new File("/Users/wenyue/test/" + subjectId + ".txt"), commentStrs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("完成抓取");

    }


}

