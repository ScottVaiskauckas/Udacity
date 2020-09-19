package com.sitstaycreate.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Article>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /** URL for article data from the Guardian */
    private static final String GUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search";
    /**
     * Constant value for the article loader ID
     */
    private static final int GUARDIAN_LOADER_ID = 1;

    /** Adapter for the list of articles */
    private ArticleAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of articles as input
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        // Set the adapter on the {@link ListView}
        articleListView.setAdapter(mAdapter);

        // OnItemClickListener sends an intent to open the article in a web browse
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                Article currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object
                Uri articleUri = Uri.parse(currentArticle.getUrl());

                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter
            loaderManager.initLoader(GUARDIAN_LOADER_ID, null, this);
        } else {
            // Display an error if there is no network connection
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    //Construct URI from preferences
    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String subjectTitle = sharedPrefs.getString(
                getString(R.string.search_by_title_key),
                getString(R.string.search_by_title_default));
        subjectTitle = subjectTitle.replace(" ", "%20");
        String dateRange = sharedPrefs.getString(
                getString(R.string.search_by_date_key),
                getString(R.string.search_by_date_default));
        if(dateRange.contains("/")) {
            String [] dateRangeArray = dateRange.split("/");
            dateRange = dateRangeArray[2] + "-" + dateRangeArray[1] + "-" + dateRangeArray[0];
        }
        else if(dateRange.contains("-")) {
            String [] dateRangeArray = dateRange.split("-");
            dateRange = dateRangeArray[2] + "-" + dateRangeArray[1] + "-" + dateRangeArray[0];
        }
        else if(dateRange.contains(".")) {
            String [] dateRangeArray = dateRange.split(".");
            dateRange = dateRangeArray[2] + "-" + dateRangeArray[1] + "-" + dateRangeArray[0];
        }
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", subjectTitle);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("tag", "politics/politics");
        uriBuilder.appendQueryParameter("from-date", dateRange);
        uriBuilder.appendQueryParameter("api-key", "ea68eb81-35c5-4bab-a225-0a46c3846560");
        return new ArticleLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No articles found."
        mEmptyStateTextView.setText(R.string.no_articles);

        // Clear the adapter of previous article data
        mAdapter.clear();

        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset.
        mAdapter.clear();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}