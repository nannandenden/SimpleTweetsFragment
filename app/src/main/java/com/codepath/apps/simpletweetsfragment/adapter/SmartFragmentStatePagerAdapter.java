package com.codepath.apps.simpletweetsfragment.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Extension of FragmentStatePagerAdapter which intelligently caches all active fragments and
 * manages the fragment lifecycles.
 * FragmentStatePagerAdapter - Implementation of PagerAdapter that uses a Fragment to manage each
 * page. This class also handles saving and restoring of fragment's state.
 * This version of the pager is more useful when there are large number of pages, working more
 * like a list view.
 */

public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    // Sparse array to keep track of registered fragments in memory
    // SparseArray - maps integers to Objects. Unlike a normal array of Objects, there can be
    // gaps in the indices. It is intended to be more memory efficient that using a HashMap to
    // map Integers to Objects, both because it avoids auto-boxing keys and it its data structure
    // doesn't rely on an extra entry object for each mapping.
    private SparseArray<Fragment> registeredFragment = new SparseArray<>();


    public SmartFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }
    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragment.put(position, fragment);
        return fragment;
    }

    // unregister the fragment when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragment.remove(position);
        super.destroyItem(container, position, object);
    }

    // get the fragment for it's position if instantiated
    public Fragment getRegisteredFragment(int position) {
        return registeredFragment.get(position);
    }
}
