package com.example.machomefolder.newsappstage1;

public class Article {
    private final String mTitle;
    private final String mSection;
    private final String mAuthor;
    private final String mDate;
    private final String mUrl;
    private final String mImage;

    //Constructor
    public Article(String title, String section, String author, String date, String url, String image) {
        mTitle = title;
        mSection = section;
        mAuthor = author;
        mDate = date;
        mUrl = url;
        mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getImage() {
        return mImage;
    }
}

