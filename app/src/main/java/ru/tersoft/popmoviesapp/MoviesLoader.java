package ru.tersoft.popmoviesapp;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoviesLoader extends AsyncTask<Object, Object, Integer> {
    /*
    AsyncTask that loads new content from page and makes a callback to MoviesActivityFragment
    */

    HttpURLConnection connection;

    private MoviesActivityFragment.FragmentCallback mFragmentCallback;

    public MoviesLoader(MoviesActivityFragment.FragmentCallback fragmentCallback) {
        mFragmentCallback = fragmentCallback;
    }

    protected Integer doInBackground(Object... params) {
        int current_page = (int) params[2];
        int sort_method = (int) params[1];
        String dataUrl;
        switch(sort_method) {
            case 1:
                dataUrl = "http://api.themoviedb.org/3/movie/top_rated?";
                break;
            default:
                dataUrl = "http://api.themoviedb.org/3/movie/popular?";
                break;
        }
        // Parameters: 0 - api key (string), 1 - sort method (int), 2 - current page (int)
        String dataUrlParameters = "api_key=" + params[0] + "&page=" + current_page;
        try {
            URL url = new URL(dataUrl + dataUrlParameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // Reading answer with JsonReader
            InputStream is = connection.getInputStream();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            try {
                readMovieArray(jsonReader);
            } finally {
                jsonReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return 0;
    }

    protected void onPostExecute(Integer i) {
        mFragmentCallback.onTaskDone();
    }

    public void readMovieArray(JsonReader reader) throws IOException {
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

    public MovieInfo readMovieInfo(JsonReader reader) throws IOException {
        String path = null;
        long id = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("poster_path")) {
                path = reader.nextString();
            } else if (name.equals("id")) {
                id = reader.nextLong();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new MovieInfo(id, path);
    }
}