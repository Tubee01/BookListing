package com.example.android.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamas on 2017. 07. 07..
 */

public class QueryUtils {

    private QueryUtils() {
    }

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Query the Google book API and return list of {@link Book} objects

    public static List<Book> fetchBooksData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "HTTP request problem", e);
        }
        //Extract relevant fields from the JSON response and create a list of {@link Book}
        // Return the list of {@link Book}
        return extractFeatureFromJson(jsonResponse);

    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem bulding the URL", e);
        }
        return url;
    }

    // Make HTTP request
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /*milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if the request was successful
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code:" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieveing the book JSON results.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    /* Convert the {@link InputStream} into a String which contains the whole JSON response */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }
        //Create an empty Arraylist that we can start adding books to
        List<Book> books = new ArrayList<>();
        //Try to parse the JSON response string. If there's a problem with the way the JSon is formatted
        //JSONExceptio exception object will be thrown. Cath the exception so the app doesnt crash, and print the error message to the logs.
        try {
            //Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);
            //Extract the JSONArray
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");
            //For each book in the array create an object
            for (int i = 0; i < bookArray.length(); i++) {

                //get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                String name= "";
                if (volumeInfo.has("title")) {
                    name = volumeInfo.getString("title");
                }
                String description = "";
                if (volumeInfo.has("description")) {
                    description = volumeInfo.getString("description");
                }
                String imageUrl = "";
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                if (imageLinks.has("smallThumbnail")) {
                    imageUrl = imageLinks.getString("smallThumbnail");
                }
                StringBuilder authorList = new StringBuilder();
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    authorList.append(authors.getString(0));
                    for (int j = 1; j < authors.length(); j++) {
                        authorList.append("," + authors.getString(j));
                        // if there is more than one author we list them all
                    }
                } else {
                    authorList.append("unkown author");
                }
                // Extract the value for the key called previewLink
                 String url = "";
                if (volumeInfo.has("previewLink")) {
                    url = volumeInfo.getString("previewLink");
                }
                // create a new {@link Book} object
                Book book = new Book(name,url, description, imageUrl, authorList);
                books.add(book);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        // return the list of books
        return books;
    }
}