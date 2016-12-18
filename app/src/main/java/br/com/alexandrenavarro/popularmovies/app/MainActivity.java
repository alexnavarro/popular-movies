package br.com.alexandrenavarro.popularmovies.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.util.NetworkUtil;

public class MainActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIES = "EXTRA_MOVIES";

    private GridView mGridView;
    private PopMovieAdapter adapter;
    private List<Movie> movies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (GridView) findViewById(R.id.movies_grid);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        adapter = new PopMovieAdapter(getApplicationContext(), movies);
        mGridView.setAdapter(adapter);

        if(savedInstanceState == null){
            if(!NetworkUtil.isOnline(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "Sorry, No internet connection!", Toast.LENGTH_LONG).show();
                return;
            }


//        String zipCode = PreferenceManager.getDefaultSharedPreferences(getContext()).
//                getString(getString(R.string.pref_location_key),
//                        getString(R.string.pref_location_default));
            new FetchMovieDBTask().execute("popular", "en_US", "1");
        }else{
            List<Movie> movies = (ArrayList<Movie>)savedInstanceState.get(EXTRA_MOVIES);
            this.movies = movies;
            if(movies !=null && movies.size() > 0)
                adapter.addAll(movies);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRA_MOVIES, (ArrayList<Movie>) movies);
        super.onSaveInstanceState(outState);
    }

    public class FetchMovieDBTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieDBTask.class.getSimpleName();

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
                return getWeatherDataFromJson(moviesJsonStr);
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
            if(adapter != null && movies!= null && movies.length > 0) {
                adapter.clear();
                adapter.addAll(movies);
                MainActivity.this.movies.clear();
                MainActivity.this.movies.addAll(Arrays.asList(movies));
            }
        }

        private Movie[] getWeatherDataFromJson(String movieDbJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String OVERVIEW = "overview";

            JSONObject moviesJson = new JSONObject(movieDbJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

            Movie[] movies = new Movie[moviesArray.length()];

            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                Movie movie = new Movie(movieObject.getString(POSTER_PATH), movieObject.getString(OVERVIEW));
                movies[i] = movie;
            }

            return movies;
        }
    }
}