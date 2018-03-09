package com.udacity.garuolis.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.garuolis.popularmovies.adapters.MovieListAdapter;
import com.udacity.garuolis.popularmovies.model.MovieItem;
import com.udacity.garuolis.popularmovies.model.MovieList;
import com.udacity.garuolis.popularmovies.utils.ApiUtils;
import com.udacity.garuolis.popularmovies.utils.MovieDbApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  implements Callback<MovieList>, MovieListAdapter.OnClickListener {
    public static final String TAG = MainActivity.class.toString();
    public static final String PREF_FILTER = "order";

    final List<MovieItem> movieList = new ArrayList<>();

    ProgressBar mProgressBar;

    boolean mLoading = false;
    int mCurrentPage = 1;

    MovieListAdapter mAdapter;
    GridLayoutManager mLayoutManager;

    String mFilter;
    Call<MovieList> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mFilter = prefs.getString(PREF_FILTER, ApiUtils.FILTER_POPULAR);

        getSupportActionBar().setTitle(ApiUtils.OrderTitle(this, mFilter));

        startLoadingMovies();
     }

    public void setupViews() {
        mProgressBar = findViewById(R.id.pb_loading);

        mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.movie_grid_column_count));

        RecyclerView rv = findViewById(R.id.rv_movie_grid);
        rv.setLayoutManager(mLayoutManager);

        mAdapter = new MovieListAdapter(this);
        mAdapter.setOnClickListener(this);
        rv.setAdapter(mAdapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mLayoutManager.getChildCount();
                int totalCount = mLayoutManager.getItemCount();
                int firstPosition = mLayoutManager.findFirstVisibleItemPosition();

                if (!mLoading && visibleItemCount + firstPosition >= totalCount - getResources().getInteger(R.integer.movie_grid_column_count) * 2 ) {
                    startLoadingMovies();
                }
            }
        });
    }

    public void startLoadingMovies() {
        if (call != null && !call.isCanceled()) {
            call.cancel();
        }

        setLoadingState(true);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MovieDbApi mdb = retrofit.create(MovieDbApi.class);
        call = mdb.getMovieList(mFilter, mCurrentPage);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<MovieList> call, Response<MovieList> response) {
        if (response.isSuccessful()) {
            MovieList movies = response.body();
            movieList.addAll(movies.items);
            mAdapter.setItems(movieList);
        } else {
            onFailure(call, null);
        }
        setLoadingState(false);
        mCurrentPage++;
    }

    @Override
    public void onFailure(Call<MovieList> call, Throwable t) {
        Toast.makeText(this, R.string.error_failed_to_load_data, Toast.LENGTH_LONG).show();
        setLoadingState(false);
    }

    public void setLoadingState(boolean loading) {
        mLoading = loading;
        if (mLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }


    public void changeListFilter(String newFilter) {
        if (!mFilter.equalsIgnoreCase(newFilter)) {
            mFilter = newFilter;
            getSupportActionBar().setTitle(ApiUtils.OrderTitle(this, mFilter));

            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
            edit.putString(PREF_FILTER, mFilter);
            edit.apply();

            mCurrentPage = 1;
            movieList.clear();
            mAdapter.clearItems();

            startLoadingMovies();
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        Log.v(TAG, movieList.get(position).title);

        MovieItem item = movieList.get(position);

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_ID, item.id);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_TITLE, item.title);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mi_popular:
                changeListFilter(ApiUtils.FILTER_POPULAR);
            return true;

            case R.id.mi_top:
                changeListFilter(ApiUtils.FILTER_TOP_RATED);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_list, menu);
        return true;
    }
}
