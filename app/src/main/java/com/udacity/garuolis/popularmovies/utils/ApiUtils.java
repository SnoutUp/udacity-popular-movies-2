package com.udacity.garuolis.popularmovies.utils;

import android.content.Context;

import com.udacity.garuolis.popularmovies.R;

/**
 * Created by Aurimas on 2018.02.24.
 */

/*

    Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original".

 */
public class ApiUtils {
    public final static String BASE_URL                 = "https://api.themoviedb.org/3/";
    public final static String FILTER_POPULAR           = "popular";
    public final static String FILTER_TOP_RATED         = "top_rated";

    public final static String IMG_SIZE_SMALL           = "w342";
    public final static String IMG_SIZE_MEDIUM          = "w500";

    private final static String BASE_IMAGE_PATH         = "http://image.tmdb.org/t/p/";
    public final static String DEFAULT_ORDER            = FILTER_POPULAR;

    public static String ImageUrl(String posterPath, final String size) {
        return BASE_IMAGE_PATH + "/" + size + posterPath;
    }

    public static String OrderTitle(Context ctx, final String order) {
        switch (order) {
            case FILTER_POPULAR:
                return ctx.getResources().getString(R.string.title_popular);
            case FILTER_TOP_RATED:
                return ctx.getResources().getString(R.string.title_best_rated);
        }

        return "";
    }
}
