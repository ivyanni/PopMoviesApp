package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.ViewHolder> {
    /*
    Adapter for RecyclerView that contains different CardViews
    */

    private int mMovieId;
    private List<Integer> mDataSetTypes;
    private Context mContext;

    public static final int DESC = 0; // Description
    public static final int INFO = 1; // Movie additional info
    public static final int RATING = 2; // Movie rating


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

    public class InfoViewHolder extends ViewHolder {
        TextView budget, time, date, genres, home;
        // Initialize layouts to hide them
        LinearLayout budgetLayout, timeLayout, dateLayout, genresLayout, homeLayout;
        public InfoViewHolder(View v) {
            super(v);
            this.budget = (TextView) v.findViewById(R.id.budgetText);
            this.time = (TextView) v.findViewById(R.id.timeText);
            this.date = (TextView) v.findViewById(R.id.dateText);
            this.genres = (TextView) v.findViewById(R.id.genresText);
            this.home = (TextView) v.findViewById(R.id.homeText);
            this.budgetLayout = (LinearLayout) v.findViewById(R.id.budgetLayout);
            this.timeLayout = (LinearLayout) v.findViewById(R.id.timeLayout);
            this.dateLayout = (LinearLayout) v.findViewById(R.id.dateLayout);
            this.genresLayout = (LinearLayout) v.findViewById(R.id.genresLayout);
            this.homeLayout = (LinearLayout) v.findViewById(R.id.homeLayout);
        }
    }

    public MovieInfoAdapter(int movieId, List<Integer> dataSetTypes, Context context) {
        mMovieId = movieId;
        mDataSetTypes = dataSetTypes;
        mContext = context;
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
        if (viewType == INFO) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.info_card, viewGroup, false);
            return new InfoViewHolder(v);
        }
        else return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == DESC) {
            DescriptionViewHolder descHolder = (DescriptionViewHolder) viewHolder;
            descHolder.temp.setText(Data.Movies.get(mMovieId).mDesc);

        } else if(viewHolder.getItemViewType() == RATING) {
            RatingViewHolder ratingHolder = (RatingViewHolder) viewHolder;
            ratingHolder.rating.setRating(Data.Movies.get(mMovieId).mRating);

        } else if(viewHolder.getItemViewType() == INFO) {
            InfoViewHolder infoHolder = (InfoViewHolder) viewHolder;

            if(Data.Movies.get(mMovieId).mDate == null) {
                infoHolder.dateLayout.setVisibility(LinearLayout.GONE);
            } else {
                infoHolder.date.setText(Data.Movies.get(mMovieId).mDate);
            }

            if(Data.Movies.get(mMovieId).mRuntime == 0) {
                infoHolder.timeLayout.setVisibility(LinearLayout.GONE);
            } else {
                String hour_abb = mContext.getResources().getString(R.string.hour_abb);
                String minute_abb = mContext.getResources().getString(R.string.minute_abb);
                if (Data.Movies.get(mMovieId).mRuntimeExt == 0) {
                    infoHolder.time
                            .setText(Data.Movies.get(mMovieId).mRuntime + hour_abb);
                } else infoHolder.time
                        .setText(Data.Movies.get(mMovieId).mRuntime + hour_abb + " "
                                + Data.Movies.get(mMovieId).mRuntimeExt + minute_abb);
            }

            if(Data.Movies.get(mMovieId).mBudget == 0) {
                if(Data.Movies.get(mMovieId).mBudgetExt == 0) {
                    infoHolder.budgetLayout.setVisibility(LinearLayout.GONE);
                }
                else {
                    String thousands_abb = mContext.getResources().getString(R.string.thousands_abb);
                    infoHolder.budget.setText(Long.toString(Data.Movies.get(mMovieId).mBudgetExt)
                            + thousands_abb);
                }
            } else {
                String million_abb = mContext.getResources().getString(R.string.million_abb);
                infoHolder.budget.setText(Long.toString(Data.Movies.get(mMovieId).mBudget)
                        + million_abb);
            }

            if(Data.Movies.get(mMovieId).mHome == null
                    || Data.Movies.get(mMovieId).mHome.isEmpty()
                    || Data.Movies.get(mMovieId).mHome.equals("null")) {
                infoHolder.homeLayout.setVisibility(LinearLayout.GONE);
            } else {
                infoHolder.home.setText(Data.Movies.get(mMovieId).mHome);
            }

            if(Data.Movies.get(mMovieId).mGenres.size() == 0) {
                infoHolder.genresLayout.setVisibility(LinearLayout.GONE);
            } else {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while(i < Data.Movies.get(mMovieId).mGenres.size()-1) {
                    sb.append(Data.Movies.get(mMovieId).mGenres.get(i));
                    sb.append(", ");
                    i++;
                }
                sb.append(Data.Movies.get(mMovieId).mGenres.get(i));
                infoHolder.genres.setText(sb.toString());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataSetTypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSetTypes.get(position);
    }
}
