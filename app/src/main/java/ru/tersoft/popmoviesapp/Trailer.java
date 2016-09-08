package ru.tersoft.popmoviesapp;

public class Trailer {
    private String trailerName, trailerUrl;

    public Trailer(String url, String name) {
        trailerName = name;
        trailerUrl = url;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }
}
