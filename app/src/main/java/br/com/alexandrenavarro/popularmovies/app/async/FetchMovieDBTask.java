package br.com.alexandrenavarro.popularmovies.app.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.alexandrenavarro.popularmovies.app.BuildConfig;
import br.com.alexandrenavarro.popularmovies.app.model.Movie;

/**
 * Created by alexandrenavarro on 23/01/17.
 */

public class FetchMovieDBTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMovieDBTask.class.getSimpleName();

    private MovieDBResponse callback;

    public FetchMovieDBTask(MovieDBResponse callback){
        this.callback = callback;
    }

    @Override
    protected Movie[] doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";
            final String PAGE = "PAGE";
            final String API_KEY_PARAM = "api_key";
            final String LANGUAGE = "language";
            final String ORDER = "popular";

            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendPath("movie")
                    .appendPath(params[0] != null ? params[0] : ORDER)
                    .appendQueryParameter(PAGE, params[2])
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.OPEN_THE_MOVIE_DB_API_KEY)
                    .appendQueryParameter(LANGUAGE, params[1]).build();

            Log.d("URL", builtUri.toString());
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                moviesJsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                moviesJsonStr = null;
            }
            moviesJsonStr = buffer.toString();
            Log.d(LOG_TAG, "Movie Db JSON String: " + moviesJsonStr);
            return getMoviesFromJson(moviesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            moviesJsonStr = null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        callback.onResult(movies);
    }

    private Movie[] getMoviesFromJson(String movieDbJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String RATE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String TITLE = "original_title";

        JSONObject moviesJson = new JSONObject(movieDbJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        Movie[] movies = new Movie[moviesArray.length()];

        for(int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);
            Movie movie = new Movie();
            movie.setPosterPath(movieObject.getString(POSTER_PATH));
            movie.setSynopsis(movieObject.getString(OVERVIEW));
            movie.setRating(movieObject.getDouble(RATE));
            movie.setTitle(movieObject.getString(TITLE));
            String releaseDate = movieObject.getString(RELEASE_DATE);
            if(releaseDate != null){
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM");
                try {
                    cal.setTime(sdf.parse(releaseDate));
                    movie.setReleaseDate(cal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            movies[i] = movie;
        }

        return movies;
    }
}
