package com.udacity.garuolis.popularmovies.model;

import com.google.gson.annotations.SerializedName;

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
}
