package br.com.alexandrenavarro.popularmovies.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;
import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.async.FetchReviews;
import br.com.alexandrenavarro.popularmovies.app.async.FetchVideos;
import br.com.alexandrenavarro.popularmovies.app.async.OnResponse;
import br.com.alexandrenavarro.popularmovies.app.model.Movie;
import br.com.alexandrenavarro.popularmovies.app.model.Review;
import br.com.alexandrenavarro.popularmovies.app.model.Video;
import br.com.alexandrenavarro.popularmovies.app.util.MovieDBImageURLBuilder;
import br.com.alexandrenavarro.popularmovies.app.util.NetworkUtil;
import br.com.alexandrenavarro.popularmovies.app.util.PxConverter;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.alexandrenavarro.popularmovies.app.MainActivity.REQUEST_CODE_SETTINGS_UPDATE;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class MovieDetailActivity extends AppCompatActivity implements OnResponse{

    private static final String TAG_LOG = MovieDetailActivity.class.getSimpleName();

    @BindView(R.id.txt_title) TextView mTxtTitle;
    @BindView(R.id.txt_synopsis) TextView mTxtSynopsis;
    @BindView(R.id.txt_year) TextView mTxtReleaseYear;
    @BindView(R.id.txt_rate) TextView mTxtRate;
    @BindView(R.id.imv_movie) ImageView mImvMovie;
    @BindView(R.id.recycler_view_videos) RecyclerView mVideos;
    @BindView(R.id.list_view_reviews) ListView mReviews;

    private Movie mMovie;
    private FetchReviews fetchReviews;
    private FetchVideos fetchVideos;
    private Target target = new CustomTarget();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ButterKnife.bind(this);

        mMovie = getIntent().getParcelableExtra(MainActivity.EXTRA_MOVIE);
        if(mMovie != null){
            bind();
            fetchReviews = new FetchReviews(this, mMovie.getId());
            fetchVideos = new FetchVideos(this, mMovie.getId());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivityForResult(new Intent(getApplicationContext(), SettingsActivity.class), REQUEST_CODE_SETTINGS_UPDATE);
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fetchVideos();
        fetchReviews();
    }

    private void fetchReviews() {
        if(!NetworkUtil.isOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry, No internet connection!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void fetchVideos() {
        if(!NetworkUtil.isOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry, No internet connection!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void bind(){
        String path = MovieDBImageURLBuilder.buildURL(mMovie.getPosterPath());
        Log.d(TAG_LOG, path);
        Picasso.with(getApplicationContext()).
                load(path)
                .centerCrop().resize((int)PxConverter.convertDpToPixel(120f, getApplicationContext()),
                (int)PxConverter.convertDpToPixel(170f, getApplicationContext()))
                .into(target);

        mTxtTitle.setText(mMovie.getTitle());
        mTxtRate.setText(Double.toString(mMovie.getRating()));
        mTxtSynopsis.setText(mMovie.getSynopsis());
        if(mMovie.getReleaseDate() != null)
            mTxtReleaseYear.setText(Integer.toString(mMovie.getReleaseDate().get(Calendar.YEAR)));
    }

    @Override
    public void onResponseReview(List<Review> response) {

    }

    @Override
    public void onResponseVideo(List<Video> response) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SETTINGS_UPDATE && resultCode == RESULT_OK){
            setResult(RESULT_OK);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class CustomTarget implements Target{

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            assert mImvMovie != null;
            mImvMovie.setImageBitmap(bitmap);
            Palette.from(bitmap)
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            Palette.Swatch textSwatch = palette.getDarkMutedSwatch();

                            if(textSwatch != null) {
                                mTxtTitle.setBackgroundColor(textSwatch.getRgb());
                                mTxtTitle.setTextColor(textSwatch.getBodyTextColor());
                            }else {
                                mTxtTitle.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_orange_light));
                            }

                        }
                    });
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d(TAG_LOG, errorDrawable.toString());
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}