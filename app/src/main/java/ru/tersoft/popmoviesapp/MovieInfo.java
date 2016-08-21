package ru.tersoft.popmoviesapp;

public class MovieInfo {
    /*
    MovieInfo class contains all parsed information about movie
    */

    static final String COVER_BASE = "http://image.tmdb.org/t/p/w342";
    static final String BACK_BASE = "http://image.tmdb.org/t/p/w780"; // Bigger resolution for backdrops
    String mName, mCoverPath, mDesc, mBackdropPath;
    String mDate;
    float mRating;
    long mId;

    public MovieInfo(long id, String path) {
        mId = id;
        mCoverPath = COVER_BASE + path;
    }

    public void addData(String name, String desc, String backdrop, String date, float rating) {
        mName = name; mDesc = desc; mBackdropPath = BACK_BASE + backdrop;
        mDate = date; mRating = rating;
    }
}
