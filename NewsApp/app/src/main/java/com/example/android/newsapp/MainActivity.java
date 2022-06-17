package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    // Tag for LOG Message
    private static final String LOG_TAG = MainActivity.class.getName();

    // Constant value for the news loader ID
    private static final int NEWS_LOADER_ID = 1;

    // URL from Guardian API
    private static final String NEWS_URL_BASE = "http://content.guardianapis.com/search";

    // The Api Key test
    private static final String NEWS_KEY = "09f4bdc7-43a6-4a0a-b8cd-aa9663693732";

    // Adapter for the News list
    NewsAdapter mAdapter;

    //ListView
    ListView news_list_view;

    // TextView that is visible when there is a problem with the connection or the query
    TextView mEmptyView;

    // Searched criteria
    String searchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting the launch screen view
        news_list_view = (ListView) findViewById(R.id.list_view);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.search_field);
        linearLayout.setVisibility(View.GONE);
        Button searchButton = (Button) findViewById(R.id.button_FirstSearch);
        mEmptyView = (TextView) findViewById(R.id.empty_text_view);

        //Set ClickListener on Search Button Click
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Find the edit text's actual text and make it compatible for a url search query
                String searchQueried = ((EditText) findViewById(R.id.editText_FirstSearch)).getText().toString();
                if (searchQueried.isEmpty()) {
                    searchQueriedIsEmpty();
                } else {
                    LinearLayout firstSearch = (LinearLayout) findViewById(R.id.firstSearchScreen);
                    firstSearch.setVisibility(View.GONE);
                    LinearLayout screenWithResults = (LinearLayout) findViewById(R.id.search_field);
                    screenWithResults.setVisibility(View.VISIBLE);
                    EditText editText = (EditText) findViewById(R.id.search_EditTextView);
                    editText.setText(searchQueried);
                    // know what was the status of the app, i.e, was it the first click or not
                    int SEARCH_BEFORE_OR_AFTER_FIRST_CLICK = 1;
                    //Handle the loader manager as per the the button and view selected
                    clickHandle(searchQueried, SEARCH_BEFORE_OR_AFTER_FIRST_CLICK);
                }
            }


        });

        //setting button click after first search
        ImageButton searchButtonSecondScreen = (ImageButton) findViewById(R.id.search_button);

        //Set click Listener on Search Button Click
        searchButtonSecondScreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Find the edit text's actual text and make it compatible for a url search query
                String searchQueried = ((EditText) findViewById(R.id.search_EditTextView)).getText().toString();

                //Check if user input is empty or it contains some query text
                if (searchQueried.isEmpty()) {
                    searchQueriedIsEmpty();
                } else {
                    //know what was the status of the app, i.e, was it the second or later click or not
                    int SEARCH_BEFORE_OR_AFTER_FIRST_CLICK = 2;
                    clickHandle(searchQueried, SEARCH_BEFORE_OR_AFTER_FIRST_CLICK);
                }
            }
        });
    }

    private void searchQueriedIsEmpty() {
        Context context = getApplicationContext();
        String text = "Nothing Entered in Search";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void clickHandle(String searchQueried, int SEARCH_BEFORE_OR_AFTER_FIRST_CLICK) {
        TextView searchQueriedFor = (TextView) findViewById(R.id.searchQueried);
        searchQueriedFor.setText(searchQueried);
        searchQuery = searchQueried.replace(" ", "%20");

        //Create a ConnectivityManager and get the NetworkInfo from it
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        //Create a boolean variable for the connectivity status
        if (networkInfo != null && networkInfo.isConnected()) {

          /* fetch data. Get a reference to the LoaderManager, in order to interact with loaders. */
            if (SEARCH_BEFORE_OR_AFTER_FIRST_CLICK == 1) {
                startLoaderManager();
            } else {
                View loadingIndicator = findViewById(R.id.progress_bar);
                loadingIndicator.setVisibility(View.VISIBLE);
                reStartLoaderManager();
            }
        } else {
            // display error
            setEmptyView();
        }
    }

    private void startLoaderManager() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    private void reStartLoaderManager() {
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
    }

    private void setEmptyView() {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyView = (TextView) findViewById(R.id.empty_text_view);
        mEmptyView.setText(R.string.no_internet);

        // Hide the keyboard when the app starts
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.e(LOG_TAG, "Hide the keyboard.");
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(NEWS_URL_BASE);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", searchQuery);
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("api-key", NEWS_KEY);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        Log.e(LOG_TAG, "What is the current URL " + uriBuilder);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, final ArrayList<News> data) {
        EditText editText = (EditText) findViewById(R.id.editText_FirstSearch);
        editText.setText("");
        EditText editText2 = (EditText) findViewById(R.id.search_EditTextView);
        editText2.setText("");

        View loadingIndicator = findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(View.GONE);

        if (data.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.no_news_found);
            Log.e(LOG_TAG, "Why is the list null?");
        }

        mAdapter = new NewsAdapter(MainActivity.this, data);

        // Find a reference to the {@link ListView} in the layout
        ListView news_list_view = (ListView) findViewById(R.id.list_view);
        /*
        Set the adapter on the {@link ListView}
        so the list can be populated in the user interface
        */
        news_list_view.setAdapter(mAdapter);

        news_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News news = data.get(position);
                Intent goToUrl = new Intent(Intent.ACTION_VIEW);
                goToUrl.setData(Uri.parse(news.getUrl()));
                startActivity(goToUrl);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {
        mAdapter.clear();
        news_list_view.setVisibility(View.GONE);
    }

}

