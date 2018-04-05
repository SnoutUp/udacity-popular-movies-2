package com.udacity.garuolis.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideoList {
    @SerializedName("results")
    public List<MovieVideo> items;
}
