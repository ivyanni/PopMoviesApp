package ru.tersoft.popmoviesapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.ViewHolder> {
    /*
    Adapter for RecyclerView that contains different CardViews
    */

    private List<String> mDataSet;
    private List<Integer> mDataSetTypes;

    public static final int DESC = 0; // Description
    public static final int RATING = 1; // Movie rating


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class DescriptionViewHolder extends ViewHolder {
        TextView temp;
        public DescriptionViewHolder(View v) {
            super(v);
            this.temp = (TextView) v.findViewById(R.id.desc);
        }
    }

    public class RatingViewHolder extends ViewHolder {
        RatingBar rating;
        public RatingViewHolder(View v) {
            super(v);
            this.rating = (RatingBar) v.findViewById(R.id.ratingBar);
        }
    }

    public MovieInfoAdapter(List<String> dataSet, List<Integer> dataSetTypes) {
        mDataSet = dataSet;
        mDataSetTypes = dataSetTypes;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == DESC) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.desc_card, viewGroup, false);
            return new DescriptionViewHolder(v);
        }
        if (viewType == RATING) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.rating_card, viewGroup, false);
            return new RatingViewHolder(v);
        }
        else return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == DESC) {
            DescriptionViewHolder descHolder = (DescriptionViewHolder) viewHolder;
            descHolder.temp.setText(mDataSet.get(position));
        } else if(viewHolder.getItemViewType() == RATING) {
            RatingViewHolder ratingHolder = (RatingViewHolder) viewHolder;
            ratingHolder.rating.setRating(Float.parseFloat(mDataSet.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSetTypes.get(position);
    }
}
