package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieListAdapter extends BaseAdapter {
    /*
    Custom adapter for GridView.
    GridView is needed to show movie posters on main activity.
    */

    private final Context context;
    private final List<String> urls = new ArrayList<>();

    public MovieListAdapter(Context context) {
        this.context = context;
        this.urls.clear();
        urls.addAll(Data.getMoviesPaths());
    }

    public void refreshData() {
        this.urls.clear();
        urls.addAll(Data.getMoviesPaths());
        notifyDataSetChanged();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        ImageView view = (ImageView) convertView;
        if (view == null) {
            view = new ImageView(context);
        }
        view.setAdjustViewBounds(true);
        Picasso.with(context).cancelRequest(view);
        String url = getItem(position);
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .config(Bitmap.Config.RGB_565)
                .fit()
                .tag(context)
                .into(view);
        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
