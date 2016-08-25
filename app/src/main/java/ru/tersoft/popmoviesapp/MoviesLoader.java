package ru.tersoft.popmoviesapp;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoviesLoader extends AsyncTask<Object, Object, Boolean> {
    /*
    AsyncTask that loads new content from page and makes a callback to MoviesActivityFragment
    */

    private HttpURLConnection mConnection;

    private MoviesActivityFragment.FragmentCallback mFragmentCallback;

    MoviesLoader(MoviesActivityFragment.FragmentCallback fragmentCallback) {
        mFragmentCallback = fragmentCallback;
    }

    protected Boolean doInBackground(Object... params) {
        int sortMethod = (int) params[0];
        int currentPage = (int) params[1];
        String dataUrl;

        switch(sortMethod) {
            case 1: // Sort by rating
                dataUrl = "http://api.themoviedb.org/3/movie/top_rated?";
                break;
            default: // Sort by popularity
                dataUrl = "http://api.themoviedb.org/3/movie/popular?";
                break;
        }
        // Parameters: 0 - sort method (int), 1 - current page (int)
        String dataUrlParameters = "api_key=" + BuildConfig.TMDB_API_KEY +
                "&page=" + currentPage +
                "&language=" + Data.getLocale().getLanguage();
        try {
            URL url = new URL(dataUrl + dataUrlParameters);
            mConnection = (HttpURLConnection) url.openConnection();
            mConnection.setRequestMethod("GET");
            mConnection.setConnectTimeout(2000);
            // Reading answer with JsonReader
            InputStream is = mConnection.getInputStream();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            try {
                readMovieArray(jsonReader);
            } finally {
                jsonReader.close();
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (mConnection != null) {
                mConnection.disconnect();
            }
        }
        return true;
    }

    protected void onPostExecute(Boolean i) {
        mFragmentCallback.onTaskDone(i);
    }

    private void readMovieArray(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("results")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    Data.addMovie(readMovieInfo(reader));
                }
                reader.endArray();
            } else reader.skipValue();
        }
    }

    private MovieInfo readMovieInfo(JsonReader reader) throws IOException {
        String coverPath = null;
        long movieId = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "poster_path":
                    coverPath = reader.nextString();
                    break;
                case "id":
                    movieId = reader.nextLong();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        return new MovieInfo(movieId, coverPath);
    }
}