package br.com.alexandrenavarro.popularmovies.app.util;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.data.MovieColumns;
import br.com.alexandrenavarro.popularmovies.app.model.Movie;

/**
 * Created by alexandrenavarro on 08/02/17.
 */

public class MovieCursorUtil {

    public static List<Movie> toList(Cursor cursor) {
        List<Movie> movies = new ArrayList<>();
        if (cursor != null && !cursor.isClosed()) {
            if (cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie();
                    movie.setId(cursor.getInt(cursor.getColumnIndex(MovieColumns._ID)));

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(MovieColumns.RELEASE_DATE)));
                    movie.setReleaseDate(calendar);

                    movie.setRating(cursor.getDouble(cursor.getColumnIndex(MovieColumns.RATING)));
                    movie.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieColumns.POSTER_PATH)));
                    movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieColumns.TITLE)));
                    movie.setSynopsis(cursor.getString(cursor.getColumnIndex(MovieColumns.SYNOPSIS)));

                    movies.add(movie);
                } while (cursor.moveToNext());
            }

        }

        cursor.close();

        return movies;
    }
}
