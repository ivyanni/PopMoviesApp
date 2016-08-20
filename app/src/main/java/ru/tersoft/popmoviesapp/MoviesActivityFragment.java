package ru.tersoft.popmoviesapp;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MoviesActivityFragment extends Fragment {

    private MovieListAdapter mAdapter;
    private int current_page = 0;
    private Boolean loadingMore = true;
    private Boolean stopLoadingData = false;
    private Parcelable state;
    private GridView movieList;

    public MoviesActivityFragment() {
    }

    @Override
    public void onPause() {
        // Save GridView state
        state = movieList.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieList.setAdapter(mAdapter);
        // Restore previous state (including selected item index and scroll position)
        if(state != null) {
            movieList.onRestoreInstanceState(state);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This makes AsyncTask not to stuck after changing configuration
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movies, container, false);
        mAdapter = new MovieListAdapter(getActivity());
        movieList = (GridView) v.findViewById(R.id.movielist);
        movieList.setAdapter(mAdapter);
        movieList.setOnScrollListener(new GridScrollListener(getActivity()) {
            @Override
            public void onLoadMore() {
                // Endless GridView realization
                if (!loadingMore) {
                    if (!stopLoadingData) {
                        Object[] params = {getResources().getString(R.string.api_key)};
                        new MoviesLoader().execute(params);
                    }
                }
            }
        });
        if(Data.getMoviesNum() == 0) {
            // Parameters: 0 - api key
            // API key stored in strings.xml. Get your api key here: https://www.themoviedb.org/account/signup
            Object[] params = {getResources().getString(R.string.api_key)};
            new MoviesLoader().execute(params);
        }
        return v;
    }

    public class MoviesLoader extends AsyncTask<Object, Object, Integer> {
        HttpURLConnection connection;

        protected Integer doInBackground(Object... params) {
            loadingMore = true;
            current_page += 1;
            // Data url for array sorted by popularity
            String dataUrl = "http://api.themoviedb.org/3/movie/popular?";
            // Parameters: 0 - api key
            String dataUrlParameters = "api_key=" + params[0] + "&page=" + current_page;
            try {
                URL url = new URL(dataUrl+dataUrlParameters);
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
            mAdapter.refreshData();
            loadingMore = false;
        }

        public List<MovieInfo> readMovieArray(JsonReader reader) throws IOException {
            List<MovieInfo> movies = new ArrayList<>();
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("results")) {
                    reader.beginArray();
                    while (reader.hasNext()) {
                        Data.addMovie(readMovieInfo(reader));
                    }
                    reader.endArray();
                }
                else reader.skipValue();
            }
            return movies;
        }

        public MovieInfo readMovieInfo(JsonReader reader) throws IOException {
            String path = null;
            String title = null;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("poster_path")) {
                    path = reader.nextString();
                } else if (name.equals("title")) {
                    title = reader.nextString();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return new MovieInfo(title, path);
        }
    }

}

