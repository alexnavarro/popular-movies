package br.com.alexandrenavarro.popularmovies.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;

import br.com.alexandrenavarro.popularmovies.app.util.MovieDBImageURLBuilder;
import br.com.alexandrenavarro.popularmovies.app.util.PxConverter;

import static br.com.alexandrenavarro.popularmovies.app.MainActivity.REQUEST_CODE_SETTINGS_UPDATE;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG_LOG = MovieDetailActivity.class.getSimpleName();


    private TextView mTxtTitle;
    private TextView mTxtSynopsis;
    private TextView mTxtReleaseYear;
    private TextView mTxtRate;
    private ImageView mImvMovie;
    private Movie mMovie;
    private Target target = new CustomTarget();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        mImvMovie = (ImageView) findViewById(R.id.imv_movie);
        mTxtRate = (TextView) findViewById(R.id.txt_rate);
        mTxtReleaseYear = (TextView) findViewById(R.id.txt_year);
        mTxtSynopsis = (TextView) findViewById(R.id.txt_synopsis);
        mTxtTitle = (TextView) findViewById(R.id.txt_title);

        mMovie = getIntent().getParcelableExtra(MainActivity.EXTRA_MOVIE);
        if(mMovie != null){
            bind();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

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