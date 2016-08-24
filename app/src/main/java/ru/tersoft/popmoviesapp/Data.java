package ru.tersoft.popmoviesapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class Data {
    /*
        Array of MovieInfo elements and all operations with it
    */

    private static List<MovieInfo> sMovies = new ArrayList<>();
    private static Locale sLocale;

    public Data() {
    }

    static void setLocale(Locale newLocale) {
        sLocale = newLocale;
    }
    static Locale getLocale() { return sLocale; }
    static void addMovie(MovieInfo movie) {
        sMovies.add(movie);
    }
    static MovieInfo getMovie(int id) { return sMovies.get(id); }
    static void removeAllMovies() { sMovies.clear(); }
    static int getMoviesNum() {
        return sMovies.size();
    }
    static List<String> getCoverPaths() {
        List<String> coverPaths = new ArrayList<>();
        if(getMoviesNum() != 0) {
            for (MovieInfo m : sMovies) {
                coverPaths.add(m.mCoverPath);
            }
        }
        return coverPaths;
    }
}
