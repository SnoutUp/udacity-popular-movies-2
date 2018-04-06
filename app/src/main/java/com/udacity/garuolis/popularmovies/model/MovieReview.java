package com.udacity.garuolis.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReview implements Parcelable{
    public String author;
    public String content;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    public MovieReview() {

    }

    public MovieReview(Parcel in) {
        this.author      = in.readString();
        this.content     = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(author);
        parcel.writeString(content);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
