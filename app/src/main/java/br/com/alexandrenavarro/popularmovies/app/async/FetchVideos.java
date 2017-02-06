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
import br.com.alexandrenavarro.popularmovies.app.model.Video;

/**
 * Created by alexandrenavarro on 05/02/17.
 */

public class FetchVideos extends AsyncTask<String, Void, List<Video>> {

    private final String TAG = FetchVideos.class.getSimpleName();

    private OnResponse callback;
    private int movieId;

    public FetchVideos(OnResponse callback, int movieId) {
        this.callback = callback;
        this.movieId = movieId;
    }

    @Override
    protected List<Video> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String videosJsonStr;

        try {
            final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";
            final String API_KEY_PARAM = "api_key";
            final String LANGUAGE = "language";

            Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                    .appendPath("movie")
                    .appendPath(Integer.toString(movieId))
                    .appendPath("videos")
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

            videosJsonStr = buffer.toString();
            Log.d(TAG, "Movie Db JSON String: " + videosJsonStr);
            return getVideosFromJson(videosJsonStr);
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
    protected void onPostExecute(List<Video> videos) {
        callback.onResponseVideo(videos);
    }

    private List<Video> getVideosFromJson(String videosJsonStr)
            throws JSONException {

        final String RESULTS = "results";
        final String ID = "id";
        final String NAME = "name";
        final String KEY = "key";
        final String SITE = "site";

        JSONObject moviesJson = new JSONObject(videosJsonStr);
        JSONArray videosArray = moviesJson.getJSONArray(RESULTS);

        List<Video> videos = new ArrayList<>();

        for (int i = 0; i < videosArray.length(); i++) {
            JSONObject movieObject = videosArray.getJSONObject(i);
            Video video = new Video();
            video.setId(movieObject.getString(ID));
            video.setMovieId(movieId);
            video.setKey(movieObject.getString(KEY));
            video.setName(movieObject.getString(NAME));
            video.setSite(movieObject.getString(SITE));
            videos.add(video);
        }

        return videos;
    }
}
