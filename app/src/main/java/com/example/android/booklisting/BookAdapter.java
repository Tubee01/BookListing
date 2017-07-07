package com.example.android.booklisting;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tamas on 2017. 07. 07..
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        }
        Book currentBook = getItem(position);

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.title);
        nameTextView.setText(currentBook.getName());

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(currentBook.getAuthor());

        TextView descriptionTextView = (TextView) listItemView.findViewById(R.id.description);
        descriptionTextView.setText(currentBook.getDescription());

        WebView imageWebView = (WebView) listItemView.findViewById(R.id.webView_image);
        imageWebView.loadUrl(currentBook.getImageUrl());

        return listItemView;
    }
}
