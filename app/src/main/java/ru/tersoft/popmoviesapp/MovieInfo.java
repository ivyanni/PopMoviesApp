package ru.tersoft.popmoviesapp;

public class MovieInfo {
    /*
    MovieInfo class contains all parsed information about movie
    */

    static final String BASE = "http://image.tmdb.org/t/p/w342";
    String mName, mCoverPath, mDesc;

    public MovieInfo(String name, String path) {
        mCoverPath = BASE + path;
        mName = name;
    }
}
