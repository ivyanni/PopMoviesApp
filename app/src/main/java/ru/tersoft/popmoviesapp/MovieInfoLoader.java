package ru.tersoft.popmoviesapp;

import android.os.AsyncTask;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieInfoLoader extends AsyncTask<Object, Object, Integer> {
    /*
    AsyncTask that loads all needed movie info and makes a callback to DetailActivityFragment
    */

    HttpURLConnection connection;
    MovieInfo mMovieInfo;

    private DetailActivityFragment.FragmentCallback mFragmentCallback;

    public MovieInfoLoader(DetailActivityFragment.FragmentCallback fragmentCallback) {
        mFragmentCallback = fragmentCallback;
    }

    protected Integer doInBackground(Object... params) {
        // Parameters: 0 - api key (string), 1 - movie position (int)
        mMovieInfo = Data.Movies.get((int)params[1]);
        String dataUrl = "http://api.themoviedb.org/3/movie/" + mMovieInfo.mId + "?";
        String dataUrlParameters = "api_key=" + params[0];
        try {
            URL url = new URL(dataUrl + dataUrlParameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // Reading answer with JsonReader
            InputStream is = connection.getInputStream();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(is, "UTF-8"));
            try {
                readMovieInfo(jsonReader);
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

    public void readMovieInfo(JsonReader reader) throws IOException {
        String mBackdropPath = null, mName = null, mDesc = null;
        String mDate = null; double mRating = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("backdrop_path")) {
                mBackdropPath = reader.nextString();
            } else if (name.equals("title")) {
                mName = reader.nextString();
            } else if (name.equals("vote_average")) {
                mRating = reader.nextDouble();
            } else if (name.equals("release_date")) {
                mDate = reader.nextString();
            } else if (name.equals("overview")) {
                mDesc = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        mMovieInfo.addData(mName, mDesc, mBackdropPath, mDate, (float)mRating);
    }
}