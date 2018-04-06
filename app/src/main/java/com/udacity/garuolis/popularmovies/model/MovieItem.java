package com.udacity.garuolis.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Aurimas on 2018.02.24.
 */

public class MovieItem  implements Parcelable{
    public int id;
    public String title;

    @SerializedName("poster_path")
    public String poster;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };

    public MovieItem() {

    }

    public MovieItem(Parcel in) {
        this.id         = in.readInt();
        this.title      = in.readString();
        this.poster     = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(poster);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
