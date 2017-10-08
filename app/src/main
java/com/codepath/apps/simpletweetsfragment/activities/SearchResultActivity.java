package com.codepath.apps.simpletweetsfragment.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.ActivitySearchResultBinding;
import com.codepath.apps.simpletweetsfragment.fragments.SearchResultTimeLineFragment;
import com.codepath.apps.simpletweetsfragment.fragments.TweetsListFragment;
import com.codepath.apps.simpletweetsfragment.models.User;
import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;

public class SearchResultActivity extends AppCompatActivity implements
        TweetsListFragment.OnSpanNameClickedListener,
        TweetsListFragment.OnSpanTagClickedListener {

    private static final String LOG_ATG = SearchResultActivity.class.getSimpleName();
    private ActivitySearchResultBinding binding;
    private TwitterClient client;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_result);
        String hashTag = getIntent().getStringExtra("hash_tag");
        // set toolbar title with hashtag
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(hashTag);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // call fragment to display the result
        SearchResultTimeLineFragment fragment = SearchResultTimeLineFragment.newInstance(hashTag);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(binding.flContainer.getId(), fragment);
        ft.commit();
    }

    @Override
    public void onSpanNameClick(String screenName) {
        // using the screenName to get the user object
        String searchName = screenName.replace("@", "");
        Log.d(LOG_TAG, searchName);
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new Gson();
                User user = gson.fromJson(response.toString(), User.class);
                displayProfileInfo(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utils.showToast(SearchResultActivity.this, "Request Failed: " + throwable.getMessage());
            }
        };
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(this, "No internet connection");
        } else {
            client.userShow(handler, searchName);
        }
    }

    @Override
    public void onSpanTagClick(String hashTag) {
        Log.d(LOG_TAG, "hashTag: " + hashTag);
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("hash_tag", hashTag);
        startActivity(intent);
    }

    private void displayProfileInfo(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        if (user != null) {
            intent.putExtra("user", Parcels.wrap(user));
        }
        startActivity(intent);
    }
}
