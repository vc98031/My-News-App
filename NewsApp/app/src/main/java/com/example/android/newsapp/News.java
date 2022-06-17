package com.example.android.newsapp;

/**
 * Created by Mirka on 08/07/2017.
 */

public class News {

    // News title
    private String mTitle;

    // News section
    private String mCategory;

    // URL of the book;
    private String mUrl;

    /**
     * Create a new constructor for News object.
     *@param category
     * @param title is the title of the article
     * @param url   is the url of the news
     */

    public News(String title, String category, String url) {
        mTitle = title;
        mCategory = category;
        mUrl = url;
    }

    //Getter methods
    public String getCategory() {
        return mCategory;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }
}
