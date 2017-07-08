package com.example.android.booklisting;

        import android.content.Context;
        import android.content.Intent;
        import android.content.Loader;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.TextView;
        import java.util.ArrayList;
        import java.util.List;

public class MainActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<List<Book>> {

    private final static String BASE_URL = "https://www.googleapis.com/books/v1/volumes?&maxResults=40&q=";
    private String mUrl;
    /* Constant value for the book loader ID. */
    private static final int BOOK_LOADER_ID = 1;
    /* Adapter for the list of books */
    private BookAdapter mAdapter;
    /* TextView if the list is empty */
    private TextView mEmptyStateTextView;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText searchTextView = (EditText) findViewById(R.id.search_field);
        loadingIndicator = (ProgressBar) findViewById(R.id.progress);
        ListView bookListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setEmptyView(mEmptyStateTextView);

        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.emptyView);

        /* Set adapter on the {@link ListView}  */
        bookListView.setAdapter(mAdapter);
        /* Get loader only if user has searched */
        if (savedInstanceState != null) {
            mUrl = savedInstanceState.getString("URL");
        }
        if (mUrl != null) {
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Find the current book
                Book currentBook = mAdapter.getItem(position);
                //Convert the Url string to URI object

                if (currentBook != null) {
                    Uri bookUri = Uri.parse(currentBook.getUrl());
                    // Create intent to view the book uri
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                    //send intent to launch new activity
                    startActivity(websiteIntent);
                }
            }
        });
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                String keyWord = searchTextView.getText().toString().trim();

                keyWord = keyWord.replaceAll(" +", "+");
                //make URL
                mUrl = BASE_URL + keyWord;
                mAdapter.clear();
                loadingIndicator.setVisibility(View.VISIBLE);
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                // get data from internet
                getBooksDataInBackground();
            }

        });
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("URL", mUrl);
    }

    private void getBooksDataInBackground() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //get details on currently active data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //if there's a network connection
        if (networkInfo != null && networkInfo.isConnected()) {
            android.app.LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(BOOK_LOADER_ID, null, this);
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            //display error
            View loadingIndicator = findViewById(R.id.progress);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, mUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // hide loading indicator because data has been loaded
        loadingIndicator.setVisibility(View.GONE);
        // set empty state display
        mEmptyStateTextView.setText(R.string.no_result);
        //clear adapter
        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();

    }
}