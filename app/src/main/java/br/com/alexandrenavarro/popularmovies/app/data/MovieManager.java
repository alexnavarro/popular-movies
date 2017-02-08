package br.com.alexandrenavarro.popularmovies.app.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import br.com.alexandrenavarro.popularmovies.app.model.Movie;

/**
 * Created by alexandrenavarro on 08/02/17.
 */

public class MovieManager {

    public static void insertFavorite(Context context, Movie mMovie){
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                MovieProvider.Movies.CONTENT_URI,
                null,
                MovieColumns._ID +" = "+ mMovie.getId(),
                null, null);

        if(cursor.getCount() == 1){
            cursor.close();
            return;
        }

        long _id = mMovie.getId();
        ContentValues cv = new ContentValues();
        cv.put(MovieColumns.POSTER_PATH, mMovie.getPosterPath());
        cv.put(MovieColumns.RATING, mMovie.getRating());
        cv.put(MovieColumns.RELEASE_DATE, mMovie.getReleaseDate().getTimeInMillis());
        cv.put(MovieColumns.SYNOPSIS, mMovie.getSynopsis());
        cv.put(MovieColumns.TITLE, mMovie.getTitle());
        cv.put(MovieColumns._ID, mMovie.getId());

        contentResolver.insert(MovieProvider.Movies.withId(_id), cv);
    }
}
