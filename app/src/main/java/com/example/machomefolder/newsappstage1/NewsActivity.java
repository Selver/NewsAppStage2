package com.example.machomefolder.newsappstage1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    private static final int ARTICLE_LOADER_ID = 1;
    //URL of the data 
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?section=education&order-by=newest&show-elements=image&show-fields=byline%2Cthumbnail&page=1&page-size=10&api-key=test";
    private ArticleAdapter mAdapter;
    //displayed when the list is empty
    private TextView emptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //create and initialize a loader manager
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        //find and set empty state view of the list
        ListView articleListView = findViewById(R.id.list);
        emptyStateTextView = findViewById(R.id.empty_view);
        articleListView.setEmptyView(emptyStateTextView);
        //create and set article adapter
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleListView.setAdapter(mAdapter);
        //when clicked open the list item
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Article currentArticle = mAdapter.getItem(position);
                Uri articleUri = Uri.parse(currentArticle.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                startActivity(websiteIntent);
            }
        });
    }

    //check if the device is connected to the internet
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //return a new loader
    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {

        return new ArticleLoader(this, GUARDIAN_REQUEST_URL);
    }

    //when loader finished its load set loading indicator visibility gone
    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        mAdapter.clear();
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // if there is data retrieved add them to the list. Otherwise show empty text view.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        } else {
            if (!isConnected()) {
                emptyStateTextView.setText(R.string.no_internet_connection);
            } else {
                emptyStateTextView.setText(R.string.no_news);
            }
        }
    }

    // reset the loader
    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {

        mAdapter.clear();
    }
}

