package ru.tersoft.popmoviesapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class Data {
    /*
        Array of MovieInfo elements and all operations with it
    */

    static final List<MovieInfo> Movies = new ArrayList<>();
    static Locale locale;

    public Data() {
    }

    public static final void setLocale(Locale newLocale) {
        locale = newLocale;
    }

    public static final Locale getLocale() {
        return locale;
    }

    public static final void addMovie(MovieInfo movie) {
        Movies.add(movie);
    }

    public static final int getMoviesNum() {
        return Movies.size();
    }

    public static final List<String> getMoviesPaths() {
        List<String> paths = new ArrayList<>();
        if(Movies.size() != 0) {
            for (MovieInfo m : Movies) {
                paths.add(m.mCoverPath);
            }
        }
        return paths;
    }
}
