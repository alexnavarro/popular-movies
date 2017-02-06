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
import java.util.ArrayList;
import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.BuildConfig;
import br.com.alexandrenavarro.popularmovies.app.model.Review;

/**
 * Created by alexandrenavarro on 05/02/17.
 */

public class FetchReviews extends AsyncTask<String, Void, List<Review>> {

    private final String TAG = FetchReviews.class.getSimpleName();

    private OnResponse callback;
    private int movieId;

    public FetchReviews(OnResponse callback, int movieId) {
        this.callback = callback;
        this.movieId = movieId;
    }


    @Override
    protected List<Review> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewsJsonStr;

        try {
            final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";
            final String API_KEY_PARAM = "api_key";
            final String LANGUAGE = "language";
            final String PAGE = "PAGE";

            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendPath("movie")
                    .appendPath(Integer.toString(movieId))
                    .appendPath("reviews")
                    .appendQueryParameter(PAGE, params[1])
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.OPEN_THE_MOVIE_DB_API_KEY)
                    .appendQueryParameter(LANGUAGE, params[0]).build();

            Log.d("URL", builtUri.toString());
            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            reviewsJsonStr = buffer.toString();
            Log.d(TAG, "Movie Db JSON String: " + reviewsJsonStr);
            return getReviewsFromJson(reviewsJsonStr);
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        callback.onResponseReview(reviews);
    }

    private List<Review> getReviewsFromJson(String reviewsJsonStr)
            throws JSONException {

        final String RESULTS = "results";
        final String ID = "id";
        final String AUTHOR = "author";
        final String URL = "url";
        final String CONTENT = "content";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);

        List<Review> reviews = new ArrayList<>();

        for (int i = 0; i < reviewsArray.length(); i++) {
            JSONObject movieObject = reviewsArray.getJSONObject(i);
            Review review = new Review();
            review.setId(movieObject.getString(ID));
            review.setMovieId(movieId);
            review.setAuthor(movieObject.getString(AUTHOR));
            review.setContent(movieObject.getString(CONTENT));
            review.setUrl(movieObject.getString(URL));
            reviews.add(review);
        }

        return reviews;
    }
}
