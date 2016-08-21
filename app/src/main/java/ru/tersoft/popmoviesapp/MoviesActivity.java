package ru.tersoft.popmoviesapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MoviesActivity extends AppCompatActivity {
    SharedPreferences sPref;
    final String SORT_METHOD = "sort_method";
    Integer mSortMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Checking active internet connection
        if(!isOnline()) {
            Toast.makeText(this, getResources().getString(R.string.noconnection), Toast.LENGTH_SHORT).show();
            finish();
        }
        sPref = getPreferences(MODE_PRIVATE);
        mSortMethod = sPref.getInt(SORT_METHOD, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
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
                    mEditor.commit();
                    final MoviesActivityFragment fragment =
                            (MoviesActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                    fragment.current_page = 1;
                    fragment.loadingMore = true;
                    Object[] params = {getResources().getString(R.string.api_key), selectedPosition, fragment.current_page};
                    // Remove old posters and load new
                    Data.Movies.clear();
                    fragment.movieList.smoothScrollToPosition(0);
                    new MoviesLoader(new MoviesActivityFragment.FragmentCallback() {
                        @Override
                        public void onTaskDone() {
                            fragment.loadingMore = false;
                            fragment.current_page += 1;
                            fragment.mAdapter.refreshData();
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