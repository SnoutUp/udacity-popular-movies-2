package com.udacity.garuolis.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.garuolis.popularmovies.adapters.VideoListAdapter;
import com.udacity.garuolis.popularmovies.data.MoviesContract;
import com.udacity.garuolis.popularmovies.databinding.ActivityDetailsBinding;
import com.udacity.garuolis.popularmovies.model.Movie;
import com.udacity.garuolis.popularmovies.model.MovieItem;
import com.udacity.garuolis.popularmovies.model.MovieVideo;
import com.udacity.garuolis.popularmovies.model.MovieVideoList;
import com.udacity.garuolis.popularmovies.utils.ApiUtils;
import com.udacity.garuolis.popularmovies.utils.MovieDbApi;

import java.util.List;

import cz.intik.overflowindicator.SimpleSnapHelper;
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

public class DetailsActivity extends AppCompatActivity implements VideoListAdapter.OnClickListener{
    private static final String TAG = DetailsActivity.class.toString();
    public final static String EXTRA_MOVIE_PARCEL = "extra_movie_parcel";

    private boolean isFavorite = false;

    private MovieItem mItem;
    private Movie mMovie;
    ActivityDetailsBinding mBinding;
    VideoListAdapter mVideoAdapter;
    private Menu optionsMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mItem = getIntent().getParcelableExtra(EXTRA_MOVIE_PARCEL);

        if (mItem != null) {
            mBinding.tvTitle.setText(mItem.title);
            setupViews();
            loadMovieDetails();
        }
    }

    private void setupViews() {
        mVideoAdapter = new VideoListAdapter(this);
        mVideoAdapter.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mBinding.rvVideos.setAdapter(mVideoAdapter);
        mBinding.rvVideos.setLayoutManager(layoutManager);

        mBinding.piPager.attachToRecyclerView(mBinding.rvVideos);

        SnapHelper snapHelper = new SimpleSnapHelper(mBinding.piPager);
        snapHelper.attachToRecyclerView(mBinding.rvVideos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFavoriteState();
    }

    private void updateViewsWithVideos(List<MovieVideo> videos) {
        if (videos != null && videos.size() > 0) {
            mVideoAdapter.setItems(videos);
            mBinding.rvVideos.setVisibility(View.VISIBLE);
        } else {
            mBinding.rvVideos.setVisibility(View.GONE);
        }
    }

    public void loadMovieDetails() {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
            MovieDbApi mdb = retrofit.create(MovieDbApi.class);

            Single<Movie> movieObservable = mdb.getMovieDetails(mItem.id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
            movieObservable.subscribe(new SingleObserver<Movie>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onSuccess(Movie movie) {
                    mMovie = movie;
                    showMovieDetails();
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(DetailsActivity.this, R.string.error_failed_to_load_data, Toast.LENGTH_LONG).show();
                }
            });

            Observable<MovieVideoList> videoObservable = mdb.getMovieVideos(mItem.id).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
            videoObservable.subscribe( new Observer<MovieVideoList>() {

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(MovieVideoList movieVideoList) {
                    updateViewsWithVideos(movieVideoList.items);
                }

                @Override
                public void onError(Throwable e) {
                    updateViewsWithVideos(null);
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
        mBinding.tvRuntime.setText(getResources().getString(R.string.runtime_label_format, mMovie.runtime));

        mBinding.tvReleaseDate.setText(mMovie.getReleaseYear());


        if (!mMovie.originalTitle.equalsIgnoreCase(mMovie.title)) {
            mBinding.tvOriginalTitle.setVisibility(View.VISIBLE);
            mBinding.tvOriginalTitle.setText(mMovie.originalTitle);
        } else {
            mBinding.tvOriginalTitle.setVisibility(View.GONE);
        }

        if (mMovie.genres != null && mMovie.genres.size() > 0) {
            String[] genreStrings = new String[mMovie.genres.size()];
            for (int i = 0; i < mMovie.genres.size(); i++) {
                genreStrings[i] = mMovie.genres.get(i).name;
            }
            mBinding.tvGenres.setText(TextUtils.join(", ", genreStrings));
        }

        Picasso.with(this).load(ApiUtils.ImageUrl(mMovie.poster, ApiUtils.IMG_SIZE_MEDIUM)).into(mBinding.ivPoster);
    }

    public void showReviewActivity() {
        Intent intent = new Intent(this, ReviewsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_MOVIE_PARCEL, mItem);
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
        if (mItem == null) return;

        if (isFavorite) {
            getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI, MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[] {Integer.toString(mItem.id)});
            Toast.makeText(DetailsActivity.this, R.string.info_movie_removed_from_favorites, Toast.LENGTH_LONG).show();
        } else {
            ContentValues cv = new ContentValues();
            cv.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, mItem.id);
            cv.put(MoviesContract.MovieEntry.COLUMN_TITLE, mItem.title);
            cv.put(MoviesContract.MovieEntry.COLUMN_POSTER, mItem.poster);
            getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, cv);
            Toast.makeText(DetailsActivity.this, R.string.info_movie_added_to_favorites, Toast.LENGTH_LONG).show();
        }
        updateFavoriteState();
    }

    public void updateFavoriteState() {
        if (mItem == null) return;

        Cursor c = getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI, null,MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", new String[] {Integer.toString(mItem.id)}, null);
        isFavorite = (c != null && c.getCount() == 1);

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


    @Override
    public void playVideo(String videoUrl) {

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));
    }

    @Override
    public void shareVideo(String videoUrl) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.label_share_title));
        share.putExtra(Intent.EXTRA_TEXT, videoUrl);

        startActivity(Intent.createChooser(share, getString(R.string.label_share)));
    }
}
