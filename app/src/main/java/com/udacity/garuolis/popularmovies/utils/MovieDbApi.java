package com.udacity.garuolis.popularmovies.utils;

import com.udacity.garuolis.popularmovies.BuildConfig;
import com.udacity.garuolis.popularmovies.model.Movie;
import com.udacity.garuolis.popularmovies.model.MovieList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Aurimas on 2018.02.24.
 */

public interface MovieDbApi {
    @GET("movie/{filter}?vote_count.gte=100&api_key=" + BuildConfig.API_KEY)
    Call <MovieList> getMovieList(@Path("filter") String filter, @Query("page") int page);

    @GET("movie/{id}?api_key=" + BuildConfig.API_KEY)
    Call <Movie> getMovieDetails(@Path("id") int movieId);
}
