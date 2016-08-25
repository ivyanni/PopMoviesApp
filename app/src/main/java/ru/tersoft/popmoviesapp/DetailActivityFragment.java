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
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivityFragment extends Fragment {
    private MovieInfo mMovie;
    private MovieInfoAdapter mAdapter;
    private List<Integer> mDataSetTypes = new ArrayList<>(); // CardView order
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_detail, container, false);
        RecyclerView mRecyclerView = (RecyclerView) v.findViewById(R.id.mainView);
        // Get position in GridView of selected movie
        Bundle args = getArguments();
        if (args != null) {
            int pos = getArguments().getInt("position");
            mMovie = Data.getMovie(pos);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new MovieInfoAdapter(pos, mDataSetTypes, getActivity());
            mRecyclerView.setAdapter(mAdapter);
       /*     // Parameters: 0 - movie position (int)
            Object[] params = {pos};*/
            loadMovieInfo(pos);
        }
        return v;
    }

    private void loadMovieInfo(Object... params) {
        MovieInfoLoader testAsyncTask = new MovieInfoLoader(new DetailActivityFragment.FragmentCallback() {
            @Override
            public void onTaskDone(boolean result) {
                // Callback from MovieInfoLoader task
                if(getActivity() != null) {
                    if(result) {
                        // Set CardView order
                        mDataSetTypes.clear();
                        if (mMovie.mDesc != null && !mMovie.mDesc.isEmpty()) {
                            mDataSetTypes.add(0);
                        }
                        mDataSetTypes.add(1);
                        if(mMovie.mRating != 0) {
                            mDataSetTypes.add(2);
                        }
                        String mBackdropPath = mMovie.mBackdropPath;
                        String mName = mMovie.mName;
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
