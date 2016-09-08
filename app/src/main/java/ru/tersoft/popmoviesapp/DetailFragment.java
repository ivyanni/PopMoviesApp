package ru.tersoft.popmoviesapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private MovieInfo mMovie;
    private MovieInfoAdapter mAdapter;
    private List<Integer> mDataSetTypes = new ArrayList<>(); // CardView order
    View v;

    public static DetailFragment newInstance() {
        DetailFragment fragmentDemo = new DetailFragment();
        return fragmentDemo;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail, container, false);
        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.mainView);
        mMovie = Data.getMovie(Data.getPosition());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieInfoAdapter(Data.getPosition(), mDataSetTypes, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        loadMovieInfo(Data.getPosition());
        Data.mTrailerAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.movies_list_item, R.id.movieName, mMovie.mTrailerNames);
        return v;
    }

    private void loadMovieInfo(Object... params) {
        MovieInfoLoader testAsyncTask = new MovieInfoLoader(new DetailFragment.FragmentCallback() {
            @Override
            public void onTaskDone(boolean result) {
                // Callback from MovieInfoLoader task
                if(getActivity() != null) {
                    if(result) {
                        // Set CardView order
                        mDataSetTypes.clear();
                        if(Data.isTwoPane()) {
                            mDataSetTypes.add(3);
                        }
                        if (mMovie.mDesc != null && !mMovie.mDesc.isEmpty()) {
                            mDataSetTypes.add(0);
                        }
                        mDataSetTypes.add(1);
                        Data.lastCardIndex = 1;
                        if(mMovie.mRating != 0) {
                            mDataSetTypes.add(2);
                            Data.lastCardIndex = 2;
                        }
                        if(mMovie.mTrailers.size() > 0) {
                            mDataSetTypes.add(4);
                            Data.lastCardIndex = 4;
                        }
                        String mBackdropPath = mMovie.mBackdropPath;
                        String mName = mMovie.mName;
                        if(getActivity().findViewById(R.id.collapsing_toolbar) != null) {
                            // In two-pane mode we don't have collapsing toolbar
                            CollapsingToolbarLayout collapsingToolbar =
                                    (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar);
                            collapsingToolbar.setTitle(mName);
                            // Load backdrop image to toolbar
                            ImageView backdropView = (ImageView) getActivity().findViewById(R.id.backdropView);
                            Picasso.with(getActivity())
                                    .load(mBackdropPath)
                                    .config(Bitmap.Config.RGB_565)
                                    .tag(getActivity())
                                    .into(backdropView);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else { // Connect or Socket Exception
                        Toast.makeText(getActivity(), getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }
                }
            }
        });
        testAsyncTask.execute(params);
    }

    public interface FragmentCallback {
        void onTaskDone(boolean result);
    }
}
