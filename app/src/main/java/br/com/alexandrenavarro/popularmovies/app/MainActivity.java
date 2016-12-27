package br.com.alexandrenavarro.popularmovies.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.com.alexandrenavarro.popularmovies.app.util.NetworkUtil;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_SETTINGS_UPDATE = 666;
    public static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";

    private GridView mGridView;
    private PopMovieAdapter adapter;
    private ProgressBar progressBar;
    private List<Movie> movies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (GridView) findViewById(R.id.movies_grid);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        adapter = new PopMovieAdapter(getApplicationContext(), movies);
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra(MainActivity.EXTRA_MOVIE, adapter.getItem(i));
                startActivityForResult(intent, REQUEST_CODE_SETTINGS_UPDATE);
            }
        });

        if(savedInstanceState == null){
            fetchMovies();
        }else{
            List<Movie> movies = (ArrayList<Movie>)savedInstanceState.get(EXTRA_MOVIES);
            this.movies = movies;
            if(movies !=null && movies.size() > 0)
                adapter.addAll(movies);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), REQUEST_CODE_SETTINGS_UPDATE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchMovies() {
        if(!NetworkUtil.isOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry, No internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);

        String order = sharedPrefs.getString(
                getString(R.string.pref_sort_movies_key),
                getString(R.string.pref_order_popular));

        new FetchMovieDBTask().execute(order, Locale.getDefault().toString() , "1");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(EXTRA_MOVIES, (ArrayList<Movie>) movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SETTINGS_UPDATE && resultCode == RESULT_OK){
            fetchMovies();
        }

        super.onActivityResult(requestCode, resultCode, data);
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
            progressBar.setVisibility(View.GONE);
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
}