package br.com.alexandrenavarro.popularmovies.app;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;

import br.com.alexandrenavarro.popularmovies.app.util.MovieDBImageURLBuilder;
import br.com.alexandrenavarro.popularmovies.app.util.PxConverter;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private TextView mTxtTitle;
    private TextView mTxtSynopsis;
    private TextView mTxtReleaseYear;
    private TextView mTxtRate;
        private ImageView mImvMovie;
    private Movie mMovie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        mImvMovie = (ImageView) findViewById(R.id.imv_movie);
        mTxtRate = (TextView) findViewById(R.id.txt_rate);
        mTxtReleaseYear = (TextView) findViewById(R.id.txt_year);
        mTxtSynopsis = (TextView) findViewById(R.id.txt_synopsis);
        mTxtTitle = (TextView) findViewById(R.id.txt_title);

        if(savedInstanceState == null){
             mMovie = getIntent().getParcelableExtra(MainActivity.EXTRA_MOVIE);
            if(mMovie != null){
                bind();
            }
        }
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
