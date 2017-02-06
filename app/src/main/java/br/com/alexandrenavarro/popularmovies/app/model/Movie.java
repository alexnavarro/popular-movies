package br.com.alexandrenavarro.popularmovies.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class Movie implements Parcelable {

    private String posterPath;
    private String synopsis;
    private double rating;
    private Calendar releaseDate;
    private String title;
    private int id;

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Calendar getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Calendar releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.posterPath);
        dest.writeString(this.synopsis);
        dest.writeDouble(this.rating);
        dest.writeSerializable(this.releaseDate);
        dest.writeString(this.title);
        dest.writeInt(this.id);
    }

    public Movie() {
    }

    protected Movie(Parcel in) {
        this.posterPath = in.readString();
        this.synopsis = in.readString();
        this.rating = in.readDouble();
        this.releaseDate = (Calendar) in.readSerializable();
        this.title = in.readString();
        this.id = in.readInt();
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
