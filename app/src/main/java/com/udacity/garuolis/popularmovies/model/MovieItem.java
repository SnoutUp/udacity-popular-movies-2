package com.udacity.garuolis.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aurimas on 2018.02.24.
 */

public class MovieItem {
    public String title;
    public int id;

    @SerializedName("vote_count")
    public int votes;

    @SerializedName("vote_average")
    public float averageVote;

    @SerializedName("poster_path")
    public String poster;
}
