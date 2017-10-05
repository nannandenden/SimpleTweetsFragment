package com.codepath.apps.simpletweetsfragment.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by nanden on 10/4/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    // return the total number of fragemtns

    private String[] fragmentName = new String[]{"Home", "Mention"};
    private Context context;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }


    // return getCount method

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0 : {
                return new HomeTimelineFragment();
            }
            case 1 : {
                return new MentionTimelineFragment();
            }
            default:
                return null;
        }
    }


    // return Fragment to use based on the position

    // return fragemtn title


    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentName[position];
    }
}
