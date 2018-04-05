package com.udacity.garuolis.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aurimas on 2018.02.24.
 */

public class MovieItem {
    public int id;
    public String title;

    @SerializedName("poster_path")
    public String poster;
}
