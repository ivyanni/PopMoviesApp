package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class MoviesActivityFragment extends Fragment {

    public MovieListAdapter mAdapter;
    public int current_page = 1;
    public Boolean loadingMore = true;
    private Boolean stopLoadingData = false;
    private Parcelable state;
    public GridView movieList;

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
                        int mSortMethod = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("sort_method", 0);
                        Object[] params = {getResources().getString(R.string.api_key), mSortMethod, current_page};
                        loadMovies(params);
                    }
                }
            }
        });
        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra("position", i);
                startActivity(detailIntent);
            }
        });
        if(Data.getMoviesNum() == 0) {
            // Parameters: 0 - api key (string), 1 - sort method (int), 2 - current page (int)
            // API key stored in strings.xml. Get your api key here: https://www.themoviedb.org/account/signup
            int mSortMethod = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("sort_method", 0);
            Object[] params = {getResources().getString(R.string.api_key), mSortMethod, current_page};
            loadMovies(params);
        }
        return v;
    }

    private void loadMovies(Object... params) {
        MoviesLoader testAsyncTask = new MoviesLoader(new FragmentCallback() {
            @Override
            public void onTaskDone() {
                loadingMore = false;
                current_page += 1;
                mAdapter.refreshData();
            }
        });
        loadingMore = true;
        testAsyncTask.execute(params);
    }

    public interface FragmentCallback {
        void onTaskDone();
    }

}

