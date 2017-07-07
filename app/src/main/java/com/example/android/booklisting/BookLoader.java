package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by tamas on 2017. 07. 07..
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String mUrl;
    private static final String LOG_TAG = BookLoader.class.getName();


    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List<Book> books = QueryUtils.fetchBooksData(mUrl);
        return books;
    }
}