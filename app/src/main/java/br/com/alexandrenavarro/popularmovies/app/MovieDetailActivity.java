package br.com.alexandrenavarro.popularmovies.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.async.FetchReviews;
import br.com.alexandrenavarro.popularmovies.app.async.FetchVideos;
import br.com.alexandrenavarro.popularmovies.app.async.OnResponse;
import br.com.alexandrenavarro.popularmovies.app.model.Movie;
import br.com.alexandrenavarro.popularmovies.app.model.Review;
import br.com.alexandrenavarro.popularmovies.app.model.Video;
import br.com.alexandrenavarro.popularmovies.app.util.ImageTargetLoading;
import br.com.alexandrenavarro.popularmovies.app.util.MovieDBImageURLBuilder;
import br.com.alexandrenavarro.popularmovies.app.util.NetworkUtil;
import br.com.alexandrenavarro.popularmovies.app.util.PxConverter;
import br.com.alexandrenavarro.popularmovies.app.view.VideoView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static br.com.alexandrenavarro.popularmovies.app.MainActivity.PAGE;
import static br.com.alexandrenavarro.popularmovies.app.MainActivity.REQUEST_CODE_SETTINGS_UPDATE;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class MovieDetailActivity extends AppCompatActivity implements OnResponse, View.OnClickListener{

    private static final String TAG_LOG = MovieDetailActivity.class.getSimpleName();

    @BindView(R.id.txt_title) TextView mTxtTitle;
    @BindView(R.id.txt_synopsis) TextView mTxtSynopsis;
    @BindView(R.id.txt_year) TextView mTxtReleaseYear;
    @BindView(R.id.txt_rate) TextView mTxtRate;
    @BindView(R.id.txt_review) TextView mTxtReview;
    @BindView(R.id.txt_more) TextView mTxtMore;
    @BindView(R.id.imv_movie) ImageView mImvMovie;
    @BindView(R.id.container_trailers) LinearLayout mContainerTrailers;

    private Movie mMovie;
    private FetchReviews fetchReviews;
    private FetchVideos fetchVideos;
    private List<Review> reviews = new ArrayList<>();
    private List<Video> videos = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mMovie = getIntent().getParcelableExtra(MainActivity.EXTRA_MOVIE);
        if(mMovie != null){
            bind();
        }

        if(savedInstanceState == null){
            fetchReviews = new FetchReviews(this, mMovie.getId());
            fetchVideos = new FetchVideos(this, mMovie.getId());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(fetchReviews != null && !fetchReviews.isCancelled())
            fetchReviews.cancel(true);
        if(fetchVideos != null && !fetchVideos.isCancelled())
            fetchVideos.cancel(true);
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

        if(savedInstanceState == null) {
            fetchVideos();
            fetchReviews();
        }
    }

    private void fetchReviews() {
        if(!NetworkUtil.isOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry, No internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        if(fetchReviews == null){
            fetchReviews = new FetchReviews(this, mMovie.getId());
        }

        fetchReviews.execute("en_US" , PAGE);

    }

    private void fetchVideos() {
        if(!NetworkUtil.isOnline(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Sorry, No internet connection!", Toast.LENGTH_LONG).show();
            return;
        }

        if(fetchVideos == null)
            fetchVideos = new FetchVideos(this, mMovie.getId());

        fetchVideos.execute("en_US");
    }

    public void bind(){
        String path = MovieDBImageURLBuilder.buildURL(mMovie.getPosterPath());
        Log.d(TAG_LOG, path);
        Picasso.with(getApplicationContext()).
                load(path)
                .centerCrop().resize((int)PxConverter.convertDpToPixel(120f, getApplicationContext()),
                (int)PxConverter.convertDpToPixel(170f, getApplicationContext()))
                .into(new ImageTargetLoading(getApplicationContext(), mImvMovie, mTxtTitle));

        mTxtTitle.setText(mMovie.getTitle());
        mTxtRate.setText(Double.toString(mMovie.getRating()));
        mTxtSynopsis.setText(mMovie.getSynopsis());
        if(mMovie.getReleaseDate() != null)
            mTxtReleaseYear.setText(Integer.toString(mMovie.getReleaseDate().get(Calendar.YEAR)));

    }

    @Override
    public void onResponseReview(List<Review> response) {
        reviews = response;
        if(reviews != null && reviews.size() > 0){
            String content = response.get(0).getContent();
            content = content.length() > 300 ? content.substring(0, 300) : content;
            mTxtReview.setText(content);
            mTxtMore.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResponseVideo(List<Video> response) {
        videos = response;
        if(videos != null && videos.size() > 0) {
            for( int i = 0; i < videos.size() ; i ++ ) {
                VideoView videoView = new VideoView(getApplicationContext(), videos.get(i));
                videoView.setClickable(true);
                videoView.setOnClickListener(this);

                if(i == videos.size() -1){
                    videoView.hidBottomSeparator();
                }

                mContainerTrailers.addView(videoView);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SETTINGS_UPDATE && resultCode == RESULT_OK){
            setResult(RESULT_OK);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick(R.id.txt_more)
    public void moreTaped(TextView textView) {
        final Intent intent = new Intent(this, ReviewActivity.class);
        intent.putParcelableArrayListExtra(ReviewActivity.EXTRA_REVIEWS, (ArrayList<? extends Parcelable>) reviews);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
       Video video = ((VideoView) v).getVideo();
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getId()));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + video.getId()));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}