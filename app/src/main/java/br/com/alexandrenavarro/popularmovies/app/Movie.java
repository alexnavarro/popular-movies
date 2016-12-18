package br.com.alexandrenavarro.popularmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class Movie implements Parcelable {

    private String posterPath;
    private String overView;

    public Movie(String posterPath, String overView){
        this.posterPath = posterPath;
        this.overView = overView;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverView() {
        return overView;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.posterPath);
        dest.writeString(this.overView);
    }

    protected Movie(Parcel in) {
        this.posterPath = in.readString();
        this.overView = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
