package br.com.alexandrenavarro.popularmovies.app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.com.alexandrenavarro.popularmovies.app.adapter.PopMovieAdapter;
import br.com.alexandrenavarro.popularmovies.app.async.FetchMovieDBTask;
import br.com.alexandrenavarro.popularmovies.app.async.MovieDBResponse;
import br.com.alexandrenavarro.popularmovies.app.data.MovieProvider;
import br.com.alexandrenavarro.popularmovies.app.model.Movie;
import br.com.alexandrenavarro.popularmovies.app.util.MovieCursorUtil;
import br.com.alexandrenavarro.popularmovies.app.util.NetworkUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieDBResponse, LoaderManager.LoaderCallbacks<Cursor>{

    public static final int REQUEST_CODE_SETTINGS_UPDATE = 666;
    private static final int CURSOR_LOADER_ID = 0;
    public static final String EXTRA_MOVIES = "EXTRA_MOVIES";
    public static final String EXTRA_MOVIE = "EXTRA_MOVIE";
    public static final String PAGE = "1";

    @BindView(R.id.movies_grid) GridView mGridView;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    private PopMovieAdapter adapter;
    private List<Movie> movies = new ArrayList<>();
    private FetchMovieDBTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String order = sharedPrefs.getString(
                getString(R.string.pref_sort_movies_key),
                getString(R.string.pref_order_popular));

        if("favorites".equals(order)){
            android.content.Loader<Object> loader = getLoaderManager().getLoader(CURSOR_LOADER_ID);
            if(loader == null){
                getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
            }else{
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
            }
            return;
        }

        if(!NetworkUtil.isOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry, No internet connection!", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
            return;
        }


        task = new FetchMovieDBTask(this);
        task.execute(order, Locale.getDefault().toString() , PAGE);
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

    @Override
    protected void onStop() {
        super.onStop();
        if(task != null){
            task.cancel(true);
        }
    }

    @Override
    public void onResult(Movie[] movies) {
        progressBar.setVisibility(View.GONE);
        if(adapter != null && movies!= null && movies.length > 0) {
            updateListView(Arrays.asList(movies));
        }
    }

    private void updateListView(List<Movie> movies) {
        adapter.clear();
        adapter.addAll(movies);
        MainActivity.this.movies.clear();
        MainActivity.this.movies.addAll(movies);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MovieProvider.Movies.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        progressBar.setVisibility(View.GONE);
        List<Movie> movies = MovieCursorUtil.toList(data);
        if(movies != null) {
            updateListView(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.clear();
        movies.clear();
    }
}