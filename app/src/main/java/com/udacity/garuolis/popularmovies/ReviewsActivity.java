package com.udacity.garuolis.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.udacity.garuolis.popularmovies.adapters.ReviewListAdapter;
import com.udacity.garuolis.popularmovies.model.MovieItem;
import com.udacity.garuolis.popularmovies.model.MovieReview;
import com.udacity.garuolis.popularmovies.model.MovieReviewList;
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

public class ReviewsActivity extends AppCompatActivity {
    public static final String BUNDLE_RECYCLER_STATE    = "recyclerview_state";
    public static final String BUNDLE_LIST_ITEMS        = "list_items";

    ProgressBar mProgressBar;
    boolean mLoading = false;
    int mCurrentPage = 1;
    ReviewListAdapter mAdapter;
    RecyclerView mRecycler;

    ArrayList<MovieReview> reviews = new ArrayList<>();
    MovieItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mItem = getIntent().getParcelableExtra(DetailsActivity.EXTRA_MOVIE_PARCEL);

        if (mItem != null) {
            setupViews(savedInstanceState);
        }
    }

    private void updateViews() {
        mAdapter.setItems(reviews);

        if (reviews.size() == 0) {
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.rv_review_list).setVisibility(View.GONE);
        } else {
            findViewById(R.id.rv_review_list).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_empty).setVisibility(View.GONE);
        }
        setLoadingState(false);
    }

    private void loadReviews() {
        setLoadingState(true);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        MovieDbApi mdb = retrofit.create(MovieDbApi.class);

        Observable<MovieReviewList> reviewListObservable = mdb.getMovieReviews(mItem.id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        reviewListObservable.subscribe(new Observer<MovieReviewList>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(MovieReviewList movieReviewList) {
                reviews.addAll(movieReviewList.items);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ReviewsActivity.this, R.string.error_failed_to_load_data, Toast.LENGTH_LONG).show();
                setLoadingState(false);
            }

            @Override
            public void onComplete() {
                updateViews();
            }
        });

    }

    private void setupViews(Bundle savedState) {
        mProgressBar = findViewById(R.id.pb_loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecycler = findViewById(R.id.rv_review_list);
        mRecycler.setLayoutManager(layoutManager);

        mAdapter = new ReviewListAdapter(this);
        mRecycler.setAdapter(mAdapter);

        if (savedState != null) {
            reviews = savedState.getParcelableArrayList(BUNDLE_LIST_ITEMS);
            mRecycler.getLayoutManager().onRestoreInstanceState(savedState.getParcelable(BUNDLE_RECYCLER_STATE));
            updateViews();
        } else {
            loadReviews();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_STATE, mRecycler.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(BUNDLE_LIST_ITEMS, reviews);
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
