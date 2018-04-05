package com.udacity.garuolis.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.udacity.garuolis.popularmovies.adapters.ReviewListAdapter;
import com.udacity.garuolis.popularmovies.model.MovieReviewList;
import com.udacity.garuolis.popularmovies.utils.ApiUtils;
import com.udacity.garuolis.popularmovies.utils.MovieDbApi;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewsActivity extends AppCompatActivity {
    private int movieId;

    ProgressBar mProgressBar;
    boolean mLoading = false;
    int mCurrentPage = 1;
    ReviewListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        movieId = getIntent().getIntExtra(DetailsActivity.EXTRA_MOVIE_ID, 0);
        String movieTitle = getIntent().getStringExtra(DetailsActivity.EXTRA_MOVIE_TITLE);

        setupViews();
        loadReviews();
    }

    private void loadReviews() {
        setLoadingState(true);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        MovieDbApi mdb = retrofit.create(MovieDbApi.class);

        Observable<MovieReviewList> reviewListObservable = mdb.getMovieReviews(movieId).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        reviewListObservable.subscribe(new Observer<MovieReviewList>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MovieReviewList movieReviewList) {
                mAdapter.addItems(movieReviewList.items);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                setLoadingState(false);
            }
        });

    }

    private void setupViews() {
        mProgressBar = findViewById(R.id.pb_loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView rv = findViewById(R.id.rv_review_list);
        rv.setLayoutManager(layoutManager);

        mAdapter = new ReviewListAdapter(this);
        rv.setAdapter(mAdapter);
    }

    public void setLoadingState(boolean loading) {
        mLoading = loading;
        if (mLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
