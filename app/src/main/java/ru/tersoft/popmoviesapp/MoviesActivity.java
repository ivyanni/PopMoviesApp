package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

public class MoviesActivity extends AppCompatActivity {
    private SharedPreferences sPref;
    private static final String SORT_METHOD = "sort_method";
    private Integer mSortMethod;
    private Fragment mFragment;

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
        setSupportActionBar(toolbar);
        // Checking active internet mConnection
        if(!isOnline()) {
            Toast.makeText(this, getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
            finish();
        }
        sPref = getPreferences(MODE_PRIVATE);
        mSortMethod = sPref.getInt(SORT_METHOD, 0);
        // Check configuration changes
        if (savedInstanceState == null) {
            MoviesActivityFragment mFragment = new MoviesActivityFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.moviesFragment, mFragment, "movies_fragment").commit();
        }
        else {
            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, "movies_fragment");
        }
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
                    final MoviesActivityFragment moviesFragment =
                            (MoviesActivityFragment) getSupportFragmentManager().findFragmentById(R.id.moviesFragment);
                    Object[] params = {getResources().getString(R.string.api_key), selectedPosition, 1};
                    // Remove old posters and load new
                    Data.removeAllMovies();
                    moviesFragment.movieList.smoothScrollToPosition(0);
                    new MoviesLoader(new MoviesActivityFragment.FragmentCallback() {
                        @Override
                        public void onTaskDone(boolean result) {
                            if(!result) { // SocketTimeoutException
                                Toast.makeText(getBaseContext(), getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else moviesFragment.adapter.refreshData();
                        }
                    }).execute(params);
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
}
