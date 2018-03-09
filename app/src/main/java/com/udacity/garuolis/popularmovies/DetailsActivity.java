package com.udacity.garuolis.popularmovies;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.garuolis.popularmovies.databinding.ActivityDetailsBinding;
import com.udacity.garuolis.popularmovies.model.Movie;
import com.udacity.garuolis.popularmovies.utils.ApiUtils;
import com.udacity.garuolis.popularmovies.utils.MovieDbApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity implements Callback<Movie> {
    private static final String TAG = MainActivity.class.toString();
    public final static String EXTRA_MOVIE_ID       = "extra_movie_id";
    public final static String EXTRA_MOVIE_TITLE    = "extra_movie_title";
    private Movie mMovie;
    private boolean mLoading;
    Call<Movie> call;

    ActivityDetailsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);
        String movieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);

        mBinding.tvTitle.setText(movieTitle);

        loadMovieDetails(movieId);
    }

    public void loadMovieDetails(int movieId) {
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }

            mLoading = true;
            Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            MovieDbApi mdb = retrofit.create(MovieDbApi.class);
            call = mdb.getMovieDetails(movieId);
            call.enqueue(this);
    }


    @Override
    public void onResponse(Call<Movie> call, Response<Movie> response) {
        if (response.isSuccessful()) {
            mMovie = response.body();
            if (mMovie != null) {
                showMovieDetails();
            }
        }
        mLoading = false;
    }

    @Override
    public void onFailure(Call<Movie> call, Throwable t) {
        mLoading = false;
        Toast.makeText(this, R.string.error_failed_to_load_data, Toast.LENGTH_LONG).show();
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
