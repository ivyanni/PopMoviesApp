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
import android.widget.Toast;

public class MoviesActivityFragment extends Fragment {

    public MovieListAdapter mAdapter;
    public int current_page;
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("current_page", current_page);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movies, container, false);
        // Check saved variable after changing configuration
        if(savedInstanceState != null) {
            current_page = savedInstanceState.getInt("current_page");
        }
        else current_page = 1;

        mAdapter = new MovieListAdapter(getActivity());
        movieList = (GridView) v.findViewById(R.id.movielist);
        movieList.setAdapter(mAdapter);
        movieList.setOnScrollListener(new GridScrollListener(3, current_page, getActivity()) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                        // Endless GridView realization
                        current_page = page;
                        int mSortMethod = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("sort_method", 0);
                        Object[] params = {getResources().getString(R.string.api_key), mSortMethod, current_page};
                        loadMovies(params);
                        return true;
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
            Object[] params = {getResources().getString(R.string.api_key), mSortMethod, 1};
            loadMovies(params);
        }
        return v;
    }

    private void loadMovies(Object... params) {
        MoviesLoader testAsyncTask = new MoviesLoader(new FragmentCallback() {
            @Override
            public void onTaskDone(int i) {
                if(i == 1) { // SocketTimeoutException
                    if(getActivity() != null) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
                mAdapter.refreshData();
            }
        });
        testAsyncTask.execute(params);
    }

    public interface FragmentCallback {
        void onTaskDone(int i);
    }

}

