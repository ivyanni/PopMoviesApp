package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieInfoAdapter extends RecyclerView.Adapter<MovieInfoAdapter.ViewHolder> {
    /*
    Adapter for RecyclerView that contains different CardViews
    */

    private int mMoviePosition;
    private List<Integer> mCardTypes;
    private Context mContext;

    private static final int DESC = 0; // Description
    private static final int INFO = 1; // Movie additional info
    private static final int RATING = 2; // Movie rating
    private static final int HEADER = 3; // Header image for two-pane UI
    private static final int TRAILERS = 4; // Trailers list

    MovieInfoAdapter(int movieId, List<Integer> dataSetTypes, Context context) {
        mMoviePosition = movieId;
        mCardTypes = dataSetTypes;
        mContext = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View v) {
            super(v);
        }
    }

    private class DescriptionViewHolder extends ViewHolder {
        TextView temp;
        DescriptionViewHolder(View v) {
            super(v);
            this.temp = (TextView) v.findViewById(R.id.desc);
        }
    }

    private class TrailerViewHolder extends ViewHolder {
        ListView trailersList;

        TrailerViewHolder(View v) {
            super(v);
            trailersList = (ListView) v.findViewById(R.id.trailersList);
            trailersList.setAdapter(Data.mTrailerAdapter);
            trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(Data.getMovie(mMoviePosition).mTrailers.get(i).getTrailerUrl())));
                }
            });
        }
    }

    private class RatingViewHolder extends ViewHolder {
        RatingBar rating;
        RatingViewHolder(View v) {
            super(v);
            this.rating = (RatingBar) v.findViewById(R.id.ratingBar);
        }
    }

    private class InfoViewHolder extends ViewHolder {
        TextView budgetTextView, timeTextView, dateTextView, genresTextView, homeTextView;
        ImageView flagImageView;
        LinearLayout budgetLayout, timeLayout, dateLayout, genresLayout, homeLayout;

        InfoViewHolder(View v) {
            super(v);
            this.budgetTextView = (TextView) v.findViewById(R.id.budgetText);
            this.timeTextView = (TextView) v.findViewById(R.id.timeText);
            this.dateTextView = (TextView) v.findViewById(R.id.dateText);
            this.genresTextView = (TextView) v.findViewById(R.id.genresText);
            this.homeTextView = (TextView) v.findViewById(R.id.homeText);
            this.flagImageView = (ImageView) v.findViewById(R.id.flagImage);
            this.budgetLayout = (LinearLayout) v.findViewById(R.id.budgetLayout);
            this.timeLayout = (LinearLayout) v.findViewById(R.id.timeLayout);
            this.dateLayout = (LinearLayout) v.findViewById(R.id.dateLayout);
            this.genresLayout = (LinearLayout) v.findViewById(R.id.genresLayout);
            this.homeLayout = (LinearLayout) v.findViewById(R.id.homeLayout);
        }
    }

    private class HeaderViewHolder extends ViewHolder {
        ImageView headerImageView;
        TextView headerTextView;

        HeaderViewHolder(View v) {
            super(v);
            this.headerImageView = (ImageView) v.findViewById(R.id.headerImage);
            this.headerTextView = (TextView) v.findViewById(R.id.headerText);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        switch (viewType) {
            case DESC:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.desc_card, viewGroup, false);
                return new DescriptionViewHolder(v);
            case RATING:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.rating_card, viewGroup, false);
                return new RatingViewHolder(v);
            case INFO:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.info_card, viewGroup, false);
                return new InfoViewHolder(v);
            case HEADER:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.header_image, viewGroup, false);
                return new HeaderViewHolder(v);
            case TRAILERS:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.movies_card, viewGroup, false);
                return new TrailerViewHolder(v);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == DESC) {
            DescriptionViewHolder descHolder = (DescriptionViewHolder) viewHolder;
            descHolder.temp.setText(Data.getMovie(mMoviePosition).mDesc);
        } else if(viewHolder.getItemViewType() == RATING) {
            RatingViewHolder ratingHolder = (RatingViewHolder) viewHolder;
            ratingHolder.rating.setRating(Data.getMovie(mMoviePosition).mRating);
        } else if(viewHolder.getItemViewType() == INFO) {
            InfoViewHolder infoHolder = (InfoViewHolder) viewHolder;

            // Showing movie's release date
            if(Data.getMovie(mMoviePosition).mDate == null) {
                infoHolder.dateLayout.setVisibility(LinearLayout.GONE);
            } else {
                Picasso.with(mContext)
                        .load("http://www.translatorscafe.com/cafe/images/flags/"
                                + (Data.getMovie(mMoviePosition).mIsLocal ? Data.getLocale().getCountry() : "US")
                                + ".gif")
                        .config(Bitmap.Config.RGB_565)
                        .tag(mContext)
                        .into(infoHolder.flagImageView);
                infoHolder.dateTextView.setText(Data.getMovie(mMoviePosition).mDate);
            }

            // Showing movie's runtime
            if(Data.getMovie(mMoviePosition).mRuntime == 0) {
                infoHolder.timeLayout.setVisibility(LinearLayout.GONE);
            } else {
                String hour_abb = mContext.getResources().getString(R.string.hour_abb);
                String minute_abb = mContext.getResources().getString(R.string.minute_abb);
                if (Data.getMovie(mMoviePosition).mRuntimeExt == 0) {
                    infoHolder.timeTextView
                            .setText(Data.getMovie(mMoviePosition).mRuntime + hour_abb);
                } else infoHolder.timeTextView
                        .setText(Data.getMovie(mMoviePosition).mRuntime + hour_abb + " "
                                + Data.getMovie(mMoviePosition).mRuntimeExt + minute_abb);
            }

            // Showing movie's budget
            if(Data.getMovie(mMoviePosition).mBudget == 0) {
                if(Data.getMovie(mMoviePosition).mBudgetExt == 0) {
                    infoHolder.budgetLayout.setVisibility(LinearLayout.GONE);
                }
                else {
                    // Movie's budget less than 1 million
                    String thousands_abb = mContext.getResources().getString(R.string.thousands_abb);
                    infoHolder.budgetTextView.setText(Long.toString(Data.getMovie(mMoviePosition).mBudgetExt)
                            + thousands_abb);
                }
            } else {
                // Movie's budget more than 1 million
                String million_abb = mContext.getResources().getString(R.string.million_abb);
                infoHolder.budgetTextView.setText(Long.toString(Data.getMovie(mMoviePosition).mBudget)
                        + million_abb);
            }

            // Showing movie's homepage
            if(Data.getMovie(mMoviePosition).mHome == null
                    || Data.getMovie(mMoviePosition).mHome.isEmpty()
                    || Data.getMovie(mMoviePosition).mHome.equals("null")) {
                infoHolder.homeLayout.setVisibility(LinearLayout.GONE);
            } else {
                infoHolder.homeTextView.setText(Data.getMovie(mMoviePosition).mHome);
            }

            // Showing movie genres list
            if(Data.getMovie(mMoviePosition).mGenres.size() == 0) {
                infoHolder.genresLayout.setVisibility(LinearLayout.GONE);
            } else {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while(i < Data.getMovie(mMoviePosition).mGenres.size()-1) {
                    sb.append(Data.getMovie(mMoviePosition).mGenres.get(i));
                    sb.append(", ");
                    i++;
                }
                sb.append(Data.getMovie(mMoviePosition).mGenres.get(i));
                infoHolder.genresTextView.setText(sb.toString());
            }
        } else if(viewHolder.getItemViewType() == HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) viewHolder;
            final TextView holderText = headerHolder.headerTextView;
            Picasso.with(mContext)
                    .load(Data.getMovie(mMoviePosition).mBackdropPath)
                    .config(Bitmap.Config.RGB_565)
                    .tag(mContext)
                    .into(headerHolder.headerImageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                holderText.setText(Data.getMovie(mMoviePosition).mName);
                                Typeface myTypeface = Typeface.create("sans-serif-condensed", Typeface.BOLD);
                                holderText.setTypeface(myTypeface);
                            }

                            @Override
                            public void onError() {
                                // Do nothing
                            }
                        });
        } else if(viewHolder.getItemViewType() == TRAILERS) {
            Data.mTrailerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mCardTypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mCardTypes.get(position);
    }
}
