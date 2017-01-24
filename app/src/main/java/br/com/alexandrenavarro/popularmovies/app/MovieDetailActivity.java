package br.com.alexandrenavarro.popularmovies.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;

import br.com.alexandrenavarro.popularmovies.app.util.MovieDBImageURLBuilder;
import br.com.alexandrenavarro.popularmovies.app.util.PxConverter;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.alexandrenavarro.popularmovies.app.MainActivity.REQUEST_CODE_SETTINGS_UPDATE;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.txt_title) TextView mTxtTitle;
    @BindView(R.id.txt_synopsis) TextView mTxtSynopsis;
    @BindView(R.id.txt_year) TextView mTxtReleaseYear;
    @BindView(R.id.txt_rate) TextView mTxtRate;
    @BindView(R.id.imv_movie) ImageView mImvMovie;
    private Movie mMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ButterKnife.bind(this);

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
        Picasso.with(getApplicationContext()).
                load(MovieDBImageURLBuilder.buildURL(mMovie.getPosterPath()))
                .centerCrop().resize((int)PxConverter.convertDpToPixel(120f, getApplicationContext()),
                (int)PxConverter.convertDpToPixel(170f, getApplicationContext()))
                .into(new Target() {
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

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        mTxtTitle.setText(mMovie.getTitle());
        mTxtRate.setText(Double.toString(mMovie.getRating()));
        mTxtSynopsis.setText(mMovie.getSynopsis());
        if(mMovie.getReleaseDate() != null)
            mTxtReleaseYear.setText(Integer.toString(mMovie.getReleaseDate().get(Calendar.YEAR)));
    }
}