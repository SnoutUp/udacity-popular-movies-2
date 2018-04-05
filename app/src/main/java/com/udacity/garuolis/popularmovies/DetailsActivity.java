package com.udacity.garuolis.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.garuolis.popularmovies.data.MoviesContract;
import com.udacity.garuolis.popularmovies.databinding.ActivityDetailsBinding;
import com.udacity.garuolis.popularmovies.model.Movie;
import com.udacity.garuolis.popularmovies.model.MovieReview;
import com.udacity.garuolis.popularmovies.model.MovieReviewList;
import com.udacity.garuolis.popularmovies.model.MovieVideo;
import com.udacity.garuolis.popularmovies.model.MovieVideoList;
import com.udacity.garuolis.popularmovies.utils.ApiUtils;
import com.udacity.garuolis.popularmovies.utils.MovieDbApi;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = DetailsActivity.class.toString();
    public final static String EXTRA_MOVIE_ID       = "extra_movie_id";
    public final static String EXTRA_MOVIE_TITLE    = "extra_movie_title";

    private boolean isFavorite = false;
    private int movieId = 0;

    private Movie mMovie;
    private boolean mLoading;
    ActivityDetailsBinding mBinding;

    private Menu optionsMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);
        String movieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);

        mBinding.tvTitle.setText(movieTitle);

        loadMovieDetails();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFavoriteState();
    }

    public void loadMovieDetails() {
            mLoading = true;
            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
            MovieDbApi mdb = retrofit.create(MovieDbApi.class);

            Single<Movie> movieObservable = mdb.getMovieDetails(movieId).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
            movieObservable.subscribe(new SingleObserver<Movie>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(Movie movie) {
                    Log.v(TAG, "MOVIE LOADED: " + movie.title);
                    mMovie = movie;
                    showMovieDetails();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(DetailsActivity.this, R.string.error_failed_to_load_data, Toast.LENGTH_LONG).show();
                }
            });

            Observable<MovieVideoList> videoObservable = mdb.getMovieVideos(movieId).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
            videoObservable.subscribe( new Observer<MovieVideoList>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(MovieVideoList movieVideoList) {
                    for (MovieVideo mv : movieVideoList.items) {
                        Log.v(TAG, "Movie video: " + mv.name);
                    }
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
    }


    public void showMovieDetails() {
        mBinding.tvTitle.setText(mMovie.title);
        mBinding.tvPlotSynopsis.setText(mMovie.overview);
        mBinding.tvUserRating.setText(getResources().getString(R.string.rating_label_format, mMovie.averageVote));
        mBinding.tvReleaseDate.setText(mMovie.releaseDate);
        if (!mMovie.originalTitle.equalsIgnoreCase(mMovie.title)) {
            mBinding.tvOriginalTitle.setVisibility(View.VISIBLE);
            mBinding.tvOriginalTitle.setText(mMovie.originalTitle);
        } else {
            mBinding.tvOriginalTitle.setVisibility(View.GONE);
        }
        Picasso.with(this).load(ApiUtils.ImageUrl(mMovie.poster, ApiUtils.IMG_SIZE_MEDIUM)).into(mBinding.ivPoster);
    }

    public void showReviewActivity() {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_ID, movieId);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_TITLE, mMovie.title);
        startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.mi_favorite:
                favoriteClicked();
                break;

            case R.id.mi_reviews:
                showReviewActivity();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void favoriteClicked() {
        if (isFavorite) {
            getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI, MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[] {Integer.toString(movieId)});
        } else {
            ContentValues cv = new ContentValues();
            cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.id);
            cv.put(MoviesContract.MovieEntry.COLUMN_TITLE, mMovie.title);
            cv.put(MoviesContract.MovieEntry.COLUMN_POSTER, mMovie.poster);
            getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, cv);
        }
        updateFavoriteState();
    }

    public void updateFavoriteState() {
        Cursor c = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null,MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[] {Integer.toString(movieId)}, null);
        isFavorite = (c.getCount() == 1);
        if (isFavorite) {
            if (optionsMenu != null) optionsMenu.findItem(R.id.mi_favorite).setIcon(getResources().getDrawable(R.drawable.ic_favorite_selected));
        } else {
            if (optionsMenu != null) optionsMenu.findItem(R.id.mi_favorite).setIcon(getResources().getDrawable(R.drawable.ic_favorite_empty_white));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        optionsMenu = menu;
        updateFavoriteState();
        return super.onCreateOptionsMenu(menu);

    }
}
