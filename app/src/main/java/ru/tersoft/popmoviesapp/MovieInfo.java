package ru.tersoft.popmoviesapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovieInfo {
    /*
    MovieInfo class contains all parsed information about movie
    */

    private static final String COVER_BASE = "http://image.tmdb.org/t/p/w342";
    private static final String BACK_BASE = "http://image.tmdb.org/t/p/w780"; // Bigger resolution for backdrops
    String mName, mCoverPath, mDesc, mBackdropPath, mDate, mHome;
    boolean mIsLocal = false;
    float mRating;
    int mRuntime, mRuntimeExt;
    long mId, mBudget, mBudgetExt;
    List<String> mGenres = new ArrayList<>();

    MovieInfo(long id, String coverPath) {
        mId = id;
        mCoverPath = COVER_BASE + coverPath;
    }

    private String getLocalDateFormat(String date) {
        // Format dateTextView with user's local pattern
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date releaseDate = df.parse(date);
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
            return dateFormatter.format(releaseDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
            return date;
        }
    }

    void addData(String name, String desc, String backdrop, String date, boolean isLocal, float rating, long budget, int runtime, List<String> genres, String home) {
        mName = name; mDesc = desc;
        mBackdropPath = BACK_BASE + backdrop;
        mIsLocal = isLocal;
        mDate = getLocalDateFormat(date);
        mRating = rating;
        mRuntime = runtime / 60; mRuntimeExt = runtime % 60;
        mBudget = budget / 1000000; mBudgetExt = (budget % 1000000) / 1000;
        mGenres = genres;
        mHome = home;
    }
}
