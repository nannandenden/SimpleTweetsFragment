package com.codepath.apps.simpletweetsfragment.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.ActivityProfileBinding;
import com.codepath.apps.simpletweetsfragment.fragments.UserTimelineFragment;
import com.codepath.apps.simpletweetsfragment.models.User;
import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    private static final String LOG_TAG = ProfileActivity.class.getSimpleName();
    private TwitterClient client;
    private ActivityProfileBinding binding;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private String screenName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupView();
        // get user
        User user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        if (user ==  null) {
            // if it's null, current user's profile was clicked
            getUserInfo(client);
        } else {
            // if it's not null, profileImage in the tweetlist was clicked
            screenName = user.getScreenName();
            showProfileInfo(user);
        }
        startFragment(screenName);
    }

    private void setupView() {
        client = TwitterApp.getRestClient();
        // setup data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        setSupportActionBar(binding.toolbar);
        tvFollowers = binding.tvFollowers;
        tvFollowing = binding.tvFollowing;
        tvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                intent.putExtra("is_follower", true);
                intent.putExtra("screen_name", screenName);
                startActivity(intent);
            }
        });
        tvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                intent.putExtra("is_follower", false);
                intent.putExtra("screen_name", screenName);
                startActivity(intent);
            }
        });
    }

    private void startFragment(String screenName) {
        UserTimelineFragment fragment = UserTimelineFragment.newInstance(screenName);
        // display the user timeline fragment inside the container dynamically
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // replace the container with the usertimeline fragment
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
    }

    private void getUserInfo(TwitterClient client) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(LOG_TAG, response.toString());
                User user = new Gson().fromJson(response.toString(), User.class);
                showProfileInfo(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(LOG_TAG, errorResponse.toString());
            }
        };
        if (!Utils.isNetworkAvailable(this)) {
            Log.d(LOG_TAG, "no internet");
        } else {
            client.getUserInfo(handler);
        }

    }

    private void showProfileInfo(User user) {
        getSupportActionBar().setTitle("@" + user.getScreenName());
        binding.setUser(user);
        binding.executePendingBindings();
    }

}
