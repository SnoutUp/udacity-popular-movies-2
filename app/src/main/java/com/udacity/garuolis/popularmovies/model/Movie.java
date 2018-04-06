package com.udacity.garuolis.popularmovies.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aurimas on 2018.03.09.
 */

public class Movie {
    public int id;

    public String title;

    @SerializedName("original_title")
    public String originalTitle;

    public String overview;

    @SerializedName("release_date")
    public String releaseDate;

    @SerializedName("vote_average")
    public float averageVote;

    @SerializedName("poster_path")
    public String poster;

    public int runtime;
    public int revenue;

    public String tagline;

    public List<MovieGenre> genres;

    public String getReleaseYear() {
        String[] dateParts = TextUtils.split(releaseDate, "-");
        return dateParts[0];
    }
}
