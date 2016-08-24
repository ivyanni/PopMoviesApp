package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.widget.AbsListView;

import com.squareup.picasso.Picasso;

public abstract class GridScrollListener implements AbsListView.OnScrollListener {

    private final Context mContext;
    // The minimum number of items to have below your current scroll position
    // before mLoading more.
    private int mVisibleThreshold = 5;
    // The current offset index of data you have loaded
    private int mCurrentPage = 0;
    // The total number of items in the dataset after the last load
    private int mPreviousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean mLoading = true;
    // Sets the starting page index
    private int mStartingPageIndex = 0;

    GridScrollListener(int visibleThreshold, int startPage, Context context) {
        this.mVisibleThreshold = visibleThreshold;
        this.mStartingPageIndex = 1;
        this.mCurrentPage = startPage;
        this.mContext = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        final Picasso picasso = Picasso.with(mContext);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            picasso.resumeTag(mContext);
        } else {
            picasso.pauseTag(mContext);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < mPreviousTotalItemCount) {
            this.mCurrentPage = this.mStartingPageIndex;
            this.mPreviousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.mLoading = true; }
        }

        // If it's still mLoading, we check to see if the dataset count has
        // changed, if so we conclude it has finished mLoading and update the current page
        // number and total item count.
        if (mLoading && (totalItemCount > mPreviousTotalItemCount)) {
            mLoading = false;
            mPreviousTotalItemCount = totalItemCount;
        }

        // If it isn't currently mLoading, we check to see if we have breached
        // the mVisibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!mLoading && (firstVisibleItem + visibleItemCount + mVisibleThreshold) >= totalItemCount ) {
            mCurrentPage++;
            mLoading = onLoadMore(mCurrentPage, totalItemCount);
        }
    }

    public abstract boolean onLoadMore(int page, int totalItemsCount);
}