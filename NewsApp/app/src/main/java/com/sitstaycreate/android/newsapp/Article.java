package com.sitstaycreate.android.newsapp;

public class Article {

    /** Title of the article */
    private String mTitle;

    /** Author of the article */
    private String mAuthor;

    /** Section of the article */
    private String mSection;

    /** Website URL of the article */
    private String mUrl;

    /** Section of the article */
    private String mDatePublished;


    /**
     * Constructs a new {@link Article} object.
     *
     * @param title is the title of the news article
     * @param section is the section of the news article
     * @param datePublished is the date the article was published
     * @param url is the website URL to open the article in a web browser
     */
    public Article(String title, String author, String section, String datePublished, String url){
        mTitle = title;
        mSection = section;
        mDatePublished = datePublished;
        mUrl = url;
        mAuthor = author;

    }

    /**
     * Returns the title of the article.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Returns the section of the article.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the section of the article.
     */
    public String getSection() {
        return mSection;
    }

    /**
     * Returns the website URL to open the article in a web browser
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Returns the date the article was published.
     */
    public String getDatePublished() {
        return mDatePublished;
    }
}
