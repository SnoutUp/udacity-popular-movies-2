package com.udacity.garuolis.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieReviewList {

    public int page;

    @SerializedName("total_pages")
    public int pageCount;

    @SerializedName("total_results")
    public int resultCount;

    @SerializedName("results")
    public List<MovieReview> items;
}
