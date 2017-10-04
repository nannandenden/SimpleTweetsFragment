package com.codepath.apps.simpletweetsfragment.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by nanden on 9/27/17.
 */

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private static final String LOG_TAG = EndlessRecyclerViewScrollListener.class.getSimpleName();
    // the min number of item to have below your current list of items
    private int visibleThreshold = 5;
    // current index of the page you have loaded
    private int currentPage = 0;
    // total number of items in the array after the last loaded
    private int previousTotalItemCount = 0;
    // true if we are still waiting for the last set of data to load
    private boolean loading = true;
    // sets the starting page index
    private int startingPageIndex = 0;

    RecyclerView.LayoutManager layoutManager;


    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }
    // callback method to be invoked when the recyclerview has been scrolled. this method is
    // called after the scroll has benn completed. this method will also be called if visible
    // item range changes after a layout calculation.
    @Override
    public void onScrolled(RecyclerView recyclerView, int horizontalScrollAmount, int
            verticalScrollAmount) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = this.layoutManager.getItemCount();

        if (this.layoutManager instanceof  LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) this.layoutManager)
                    .findLastVisibleItemPosition();
            Log.d(LOG_TAG, "lastVisibleItemPosition: " + lastVisibleItemPosition);
            Log.d(LOG_TAG, "totalItemCount: " + totalItemCount);
            Log.d(LOG_TAG, "previousTotalItemCount: " + previousTotalItemCount);
            Log.d(LOG_TAG, "loading: " + loading);
            Log.d(LOG_TAG, "visibleThreshold: " + visibleThreshold);
        }

        // if the total item count is zero and previousTotalItemCount is not zero the item of
        // list is invalid. we will set the condition to the it's initial state
        if (totalItemCount < previousTotalItemCount) {
            Log.d(LOG_TAG, "list is invalid. setting to initial state");
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
            Log.d(LOG_TAG, "currentPage: " + this.currentPage);
            Log.d(LOG_TAG, "startingPageIndex: " + this.startingPageIndex);
        }

        // if it's loading, check if array>dataset has benn changed(count increased). if the
        // array dataset has been changed, we finish loading and update the data.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            Log.d(LOG_TAG, "check if array has increased. if it is finish loading and update the " +
                    "item information");
            loading = false;
            previousTotalItemCount = totalItemCount;
            Log.d(LOG_TAG, "currentPage: " + currentPage);
        }

        // if it's not loading, we check to see if we have break the visibleThreshold and need to
        // load more data. if more data needs to be loaded, we call onLoadMore to fetch more data
        // threshold should reflect how many total columns there are as well
        if (!loading && (lastVisibleItemPosition + visibleThreshold)>totalItemCount) {
            Log.d(LOG_TAG, "need to load more data");
            currentPage++;
            onLoadMore(currentPage, totalItemCount, recyclerView);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currentPage, int totalItemCount, RecyclerView
            recyclerView);
}
