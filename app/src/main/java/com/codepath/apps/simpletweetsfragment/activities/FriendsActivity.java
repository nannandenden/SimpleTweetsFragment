package com.codepath.apps.simpletweetsfragment.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.ActivityFriendsBinding;
import com.codepath.apps.simpletweetsfragment.fragments.FollowersFragment;
import com.codepath.apps.simpletweetsfragment.fragments.FollowingFragment;

public class FriendsActivity extends AppCompatActivity {

    private static final String LOG_TAG = FriendsActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ActivityFriendsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get extra passing from the Profile Activity
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friends);
        boolean isFollowers = getIntent().getBooleanExtra("is_follower", true);
        String screenName = getIntent().getStringExtra("screen_name");
        // set views
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        String title = isFollowers ? "Followers" : "Following";
        getSupportActionBar().setTitle(title);
        if (isFollowers) {
            startFollowersFragment(screenName);
        } else {
            startFollowingFragment(screenName);
        }
    }

    private void startFollowersFragment(String screenName) {
        FollowersFragment fragment = FollowersFragment.newInstance(screenName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(binding.flContainer.getId(), fragment);
        ft.commit();
    }

    private void startFollowingFragment(String screenName) {
        FollowingFragment fragment = FollowingFragment.newInstance(screenName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(binding.flContainer.getId(), fragment);
        ft.commit();
    }
}
