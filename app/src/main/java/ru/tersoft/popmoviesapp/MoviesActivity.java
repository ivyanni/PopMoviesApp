package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MoviesActivity extends AppCompatActivity implements MoviesFragment.OnGridItemSelectedListener {
    private SharedPreferences sPref;
    private static final String SORT_METHOD = "sort_method";
    private Integer mSortMethod;
    private Fragment mFragment;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mFragment != null) {
            getSupportFragmentManager().putFragment(outState, "movies_fragment", mFragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        Data.setLocale(Locale.getDefault()); // Get device's locale
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Typeface myTypeface = Typeface.create("sans-serif-condensed", Typeface.BOLD);
        toolbarTitle.setTypeface(myTypeface);
        // Checking active internet mConnection
        if(!isOnline()) {
            Toast.makeText(this, getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
            finish();
        }
        sPref = getPreferences(MODE_PRIVATE);
        mSortMethod = sPref.getInt(SORT_METHOD, 0);
        // Check configuration changes
        determinePaneLayout();
        if (savedInstanceState == null) {
                MoviesFragment mFragment = new MoviesFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.moviesFragment, mFragment, "movies_fragment")
                        .commit();
        }
        else {
            mFragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, "movies_fragment");
        }
    }

    // Two-pane mode checking
    private void determinePaneLayout() {
        FrameLayout fragmentDetail = (FrameLayout) findViewById(R.id.detailFragment);
        if (fragmentDetail != null) {
            Data.setTwoPane(true);
        } else Data.setTwoPane(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                showSortDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSortDialog() {
        final AlertDialog mSortDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.sort_dialog_title);
        builder.setSingleChoiceItems(R.array.sort_methods, mSortMethod, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Getting selected item's position
                int selectedPosition = ((AlertDialog)dialogInterface).getListView().getCheckedItemPosition();
                if(selectedPosition != mSortMethod) {
                    // Sort method was changed
                    mSortMethod = selectedPosition;
                    sPref = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor mEditor = sPref.edit();
                    mEditor.putInt(SORT_METHOD, selectedPosition);
                    mEditor.apply();
                    final MoviesFragment moviesFragment =
                            (MoviesFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.moviesFragment);
                    Object[] params = {selectedPosition, 1};
                    // Remove old posters and load new
                    Data.removeAllMovies();
                    Data.setPosition(-1);
                    moviesFragment.movieList.smoothScrollToPosition(0);
                    new MoviesLoader(new MoviesFragment.FragmentCallback() {
                        @Override
                        public void onTaskDone(boolean result) {
                            if(!result) { // SocketTimeoutException
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else moviesFragment.adapter.refreshData();
                        }
                    }).execute(params);
                    if(Data.isTwoPane()) {
                        DetailFragment fragmentItem =
                                (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.remove(fragmentItem);
                        ft.commit();
                    }
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        mSortDialog = builder.create();
        mSortDialog.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onItemSelected(int position) {
        Data.setPosition(position);
        if (Data.isTwoPane()) {
            // Single activity with list and detail
            DetailFragment fragmentItem;
            fragmentItem = DetailFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.detailFragment, fragmentItem, DETAILFRAGMENT_TAG);
            ft.commit();
        } else {
            // Separate activity
            Intent i = new Intent(this, DetailActivity.class);
            startActivity(i);
        }
    }
}
