package com.codepath.apps.simpletweetsfragment.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.apps.simpletweetsfragment.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletweetsfragment.fragments.MentionTimelineFragment;

/**
 * Created by nanden on 10/4/17.
 */
// this class act as the adapter for viewpager and controls the order of the tabs, titles and
// their associated content.
public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

    // return the total number of fragemtns
    private static final String[] fragmentNames = new String[]{"Home", "Mention"};
    private static final int PAGE_COUNT = fragmentNames.length;
    private Context context;

    public TweetsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }


    // determines the fragment for each tab
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

    // get te title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentNames[position];
    }
}
