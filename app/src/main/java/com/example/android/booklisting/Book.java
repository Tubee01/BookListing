package com.example.android.booklisting;

/**
 * Created by tamas on 2017. 07. 07..
 */

public class Book {

    private String mName;
    private String mUrl;
    private String mDescription;
    private String mImageUrl;
    private StringBuilder mAuthor;

    public Book(String name, String url, String description, String imageUrl, StringBuilder author) {
        mName = name;
        mUrl = url;
        mDescription = description;
        mImageUrl = imageUrl;
        mAuthor = author;
    }

    public String getName() {
        return mName;
    }
    public String getUrl() {
        return mUrl;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getImageUrl() {
        return mImageUrl;
    }
    public StringBuilder getAuthor() {
        return mAuthor;
    }
}
