package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.widget.AbsListView;

import com.squareup.picasso.Picasso;

public abstract class GridScrollListener implements AbsListView.OnScrollListener {
    private final Context context;

    public GridScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        final Picasso picasso = Picasso.with(context);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            picasso.resumeTag(context);
        } else {
            picasso.pauseTag(context);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // Pre-loading next page when user near the end of current page (3 images before the end)
        if ((firstVisibleItem + visibleItemCount + 3) >= totalItemCount) {
            onLoadMore();
        }
    }

    public abstract void onLoadMore();
}