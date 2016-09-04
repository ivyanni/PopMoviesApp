package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MoviesFragment extends Fragment {

    public MovieListAdapter adapter;
    public int currentPage;
    private Parcelable state;
    public GridView movieList;
    private OnGridItemSelectedListener listener;

    public interface OnGridItemSelectedListener {
        void onItemSelected(int position);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieList.setAdapter(adapter);
        // Restore previous state (including selected item index and scroll position)
        if(state != null) {
            movieList.onRestoreInstanceState(state);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movies, container, false);
        // Check saved variable after changing configuration
        if(savedInstanceState != null) {
            currentPage = savedInstanceState.getInt("current_page");
        }
        else currentPage = 1;
        adapter = new MovieListAdapter(getActivity());
        movieList = (GridView) v.findViewById(R.id.movielist);
        movieList.setAdapter(adapter);
        if(Data.getPosition() != -1) {
            movieList.smoothScrollByOffset(Data.getPosition());
        }
        movieList.setOnScrollListener(new GridScrollListener(3, currentPage, getActivity()) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                        // Endless GridView scrolling realization
                        currentPage = page;
                        int mSortMethod = getActivity().getPreferences(Context.MODE_PRIVATE)
                                .getInt("sort_method", 0);
                        Object[] params = {mSortMethod, currentPage};
                        loadMovies(params);
                        return true;
            }
        });
        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item,
                                    int position, long rowId) {
                listener.onItemSelected(position);
            }
        });
        if(Data.getMoviesNum() == 0) {
            // Parameters: 0 - sort method (int), 1 - current page (int)
            // API key stored in strings.xml. Get your api key here: https://www.themoviedb.org/account/signup
            int mSortMethod = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("sort_method", 0);
            Object[] params = {mSortMethod, 1};
            loadMovies(params);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGridItemSelectedListener) {
            listener = (OnGridItemSelectedListener) context;
        } else {
            throw new ClassCastException(
                    context.toString()
                            + " must implement MoviesFragment.OnGridItemSelectedListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("current_page", currentPage);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        // Save GridView state
        state = movieList.onSaveInstanceState();
        super.onPause();
    }

    private void loadMovies(Object... params) {
        MoviesLoader testAsyncTask = new MoviesLoader(new FragmentCallback() {
            @Override
            public void onTaskDone(boolean result) {
                if(getActivity() != null) {
                    if (!result) { // Connect or Socket Exception
                        Toast.makeText(getActivity(), getResources().getString(R.string.noconnection),
                                Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else adapter.refreshData();
                }
            }
        });
        testAsyncTask.execute(params);
    }

    public interface FragmentCallback {
        void onTaskDone(boolean result);
    }

}

