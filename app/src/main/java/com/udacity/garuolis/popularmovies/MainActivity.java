package com.udacity.garuolis.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.garuolis.popularmovies.adapters.MovieListAdapter;
import com.udacity.garuolis.popularmovies.data.MoviesContract;
import com.udacity.garuolis.popularmovies.model.MovieItem;
import com.udacity.garuolis.popularmovies.model.MovieList;
import com.udacity.garuolis.popularmovies.utils.ApiUtils;
import com.udacity.garuolis.popularmovies.utils.MovieDbApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity  implements MovieListAdapter.OnClickListener {
    public static final String TAG = MainActivity.class.toString();
    public static final String PREF_FILTER = "order";

    final List<MovieItem> itemList          = new ArrayList<>();
    final List<MovieItem> favoriteMovies    = new ArrayList<>();
    ProgressBar mProgressBar;
    boolean mLoading = false;
    int mCurrentPage = 1;

    MovieListAdapter mAdapter;
    GridLayoutManager mLayoutManager;
    RecyclerView mRecycler;

    String mFilter;

    RecyclerView.OnScrollListener mScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mFilter = prefs.getString(PREF_FILTER, ApiUtils.FILTER_POPULAR);

        getSupportActionBar().setTitle(ApiUtils.OrderTitle(this, mFilter));

        setupViews();
        startLoadingMovies();
     }

    public void setupViews() {
        mProgressBar = findViewById(R.id.pb_loading);

        mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.movie_grid_column_count));

        mRecycler = findViewById(R.id.rv_movie_grid);
        mRecycler.setLayoutManager(mLayoutManager);

        mAdapter = new MovieListAdapter(this);
        mAdapter.setOnClickListener(this);
        mRecycler.setAdapter(mAdapter);

        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mLayoutManager.getChildCount();
                int totalCount = mLayoutManager.getItemCount();
                int firstPosition = mLayoutManager.findFirstVisibleItemPosition();

                if (!mLoading && visibleItemCount + firstPosition >= totalCount - getResources().getInteger(R.integer.movie_grid_column_count) * 2  && dy != 0) {
                    startLoadingMovies();
                }
            }
        };

        mRecycler.addOnScrollListener(mScrollListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteMovies();
    }

    public void loadFavoriteMovies() {
        favoriteMovies.clear();
        Cursor c = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, MoviesContract.MovieEntry._ID + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                MovieItem mi = new MovieItem();
                mi.id = c.getInt(c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_MOVIE_ID));
                mi.title = c.getString(c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
                mi.poster = c.getString(c.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER));
                favoriteMovies.add(mi);
            }
        }


        if (mFilter.equals(ApiUtils.FILTER_FAVORITES)) {
            mAdapter.setItems(favoriteMovies);
        }
    }

    public void startLoadingMovies() {
        if (!mFilter.equals(ApiUtils.FILTER_FAVORITES)) {
            setLoadingState(true);

            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
            MovieDbApi mdb = retrofit.create(MovieDbApi.class);

            Observable<MovieList> listObservable = mdb.getMovieList(mFilter, mCurrentPage).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
            listObservable.subscribe(new Observer<MovieList>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(MovieList movieList) {
                    itemList.addAll(movieList.items);
                    mAdapter.setItems(itemList);
                    mCurrentPage++;
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, R.string.error_failed_to_load_data, Toast.LENGTH_LONG).show();
                    setLoadingState(false);
                }

                @Override
                public void onComplete() {
                    setLoadingState(false);
                }
            });
        } else {
            setLoadingState(false);
        }
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
            itemList.clear();
            mAdapter.clearItems();

            startLoadingMovies();
            loadFavoriteMovies();
        }
    }

    @Override
    public void onMovieSelected(MovieItem mi) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_PARCEL, mi);
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

            case R.id.mi_favorites:
                changeListFilter(ApiUtils.FILTER_FAVORITES);
                break;
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
