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

    static final String COVER_BASE = "http://image.tmdb.org/t/p/w342";
    static final String BACK_BASE = "http://image.tmdb.org/t/p/w780"; // Bigger resolution for backdrops
    String mName, mCoverPath, mDesc, mBackdropPath;
    String mDate;
    float mRating; int mRuntime; int mRuntimeExt;
    long mId; long mBudget; long mBudgetExt; String mHome;
    List<String> mGenres = new ArrayList<>();

    public MovieInfo(long id, String path) {
        mId = id;
        mCoverPath = COVER_BASE + path;
    }

    public void addData(String name, String desc, String backdrop, String date, float rating, long budget, int runtime, List<String> genres, String home) {
        mName = name; mDesc = desc;
        mBackdropPath = BACK_BASE + backdrop;
        // Format date with user's local pattern
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date releaseDate = df.parse(date);
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
            String dateOut = dateFormatter.format(releaseDate);
            mDate = dateOut;
        }
        catch (ParseException e) {
            e.printStackTrace();
            mDate = date;
        }
        mRating = rating;
        mRuntime = runtime / 60; mRuntimeExt = runtime % 60;
        mBudget = budget / 1000000; mBudgetExt = (budget % 1000000) / 1000;
        mGenres = genres;
        mHome = home;
    }
}
