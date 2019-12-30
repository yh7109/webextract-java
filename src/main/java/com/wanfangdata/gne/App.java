package com.wanfangdata.gne;

import com.wanfangdata.gne.bean.Article;
import com.wanfangdata.gne.extract.*;
import com.wanfangdata.gne.util.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.wanfangdata.gne.util.HtmlUtils;

import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) {
        Article article = new Article();
//        String url = "http://www.ciis.org.cn/2019-05/27/content_40764664.html";
        String url = "https://www.sams.ac.uk/science/projects/off-aqua/";
        String input = HttpUtils.doGet(url);
//        String input = HtmlUtils.getDocument(url);
        input = App.cleanHtml(input);
        Document doc = Jsoup.parse(input);
        ListExtractor listExtractor = new ListExtractor();
        boolean isListPage = listExtractor.isListPage(doc);
        if (!isListPage) {
            System.out.println(url);
            TitleExtractor titleExtractor = new TitleExtractor();
            ContentExtractor contentExtractor = new ContentExtractor();
            AuthorExtractor authorExtractor = new AuthorExtractor();
            DateExtractor dateExtractor = new DateExtractor();
//            ImageExtractor imageExtractor = new ImageExtractor();
            article.setTitle(titleExtractor.extract(doc));
            article.setContent(contentExtractor.extract(doc, url).get(0).getEle().toString());
            article.setAuthor(authorExtractor.extract(doc));
            article.setDate(dateExtractor.extract(doc));
            article.setImage(contentExtractor.extract(doc, url).get(0).getImage());
//            titleExtractor.extract(doc);
//            contentExtractor.extract(doc, url);
//            authorExtractor.extract(doc);
//            dateExtractor.extract(doc);
        }
        String title = article.getTitle();
        String content = article.getContent();
        String author = article.getAuthor();
        String date = article.getDate();
        String image = article.getImage();
        System.out.println(title);
        System.out.println(content);
        System.out.println(author);
        System.out.println(date);
        System.out.println(image);
    }

    public static String cleanHtml(String content) {
        String reg_tag = "<[\\s]*?#t#[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?#t#[\\s]*?>".replace("#t#", "script");
        content = Pattern.compile(reg_tag,Pattern.CASE_INSENSITIVE).matcher(content).replaceAll("");
        String reg_tag2 = "<[\\s]*?#t#[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?#t#[\\s]*?>".replace("#t#", "style");
        content = Pattern.compile(reg_tag2,Pattern.CASE_INSENSITIVE).matcher(content).replaceAll("");
        return content;
    }
}