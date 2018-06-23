package com.example.machomefolder.newsappstage1;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?show-fields=byline%2Cthumbnail&api-key=3ffed2d9-6a7f-4672-9a8c-313113e2f6ac&show-tags=contributor";
    //https://content.guardianapis.com/search?section=education&order-by=newest&show-elements=image&show-fields=byline%2Cthumbnail&page=1&page-size=10&api-key=test"

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

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);


        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String section = sharedPrefs.getString(
                getString(R.string.listpreference_key),
                getString(R.string.section_value_11));


    /*    String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );*/

            // parse breaks apart the URI string that's passed into its parameter
            Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

            // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
            Uri.Builder uriBuilder = baseUri.buildUpon();

            // Append query parameter and its value.

            uriBuilder.appendQueryParameter("sectionName", section);
//            uriBuilder.appendQueryParameter("orderBy", orderBy);

            // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time
            return new ArticleLoader(this, uriBuilder.toString());

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

    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
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

