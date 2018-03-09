package com.udacity.garuolis.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Aurimas on 2018.02.24.
 */

/*
{
  "page": 1,
  "total_results": 348107,
  "total_pages": 17406,
  "results": [
    {
    },
 */


public class MovieList {

    public int page = 1;
    public int pageCount = 1;
    public int resultCount = 0;

    @SerializedName("results")
    public List<MovieItem> items;

}
