package br.com.alexandrenavarro.popularmovies.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.adapter.ReviewAdapter;
import br.com.alexandrenavarro.popularmovies.app.async.FetchReviews;
import br.com.alexandrenavarro.popularmovies.app.async.OnResponse;
import br.com.alexandrenavarro.popularmovies.app.model.Movie;
import br.com.alexandrenavarro.popularmovies.app.model.Review;
import br.com.alexandrenavarro.popularmovies.app.model.Video;
import br.com.alexandrenavarro.popularmovies.app.util.NetworkUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.com.alexandrenavarro.popularmovies.app.MainActivity.PAGE;

/**
 * Created by alexandrenavarro on 2/6/17.
 */

public class ReviewActivity extends AppCompatActivity implements OnResponse {

    private static final String LOG = ReviewActivity.class.getSimpleName();

    @BindView(R.id.list_view_reviews) ListView mReviews;

    private List<Review> reviews = new ArrayList<>();
    private Movie mMovie;
    private FetchReviews fetchReviews;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.label_activity_review);
        mMovie = getIntent().getParcelableExtra(MainActivity.EXTRA_MOVIE);
        if(mMovie != null){
            fetchReviews = new FetchReviews(this, mMovie.getId());
            bind();
        }

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        fetchReviews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onResponseReview(List<Review> response) {
        this.reviews.clear();
        this.reviews.addAll(response);
        ((ArrayAdapter)mReviews.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onResponseVideo(List<Video> response) {

    }

    private void bind(){
        mReviews.setAdapter(new ReviewAdapter(getApplicationContext(), reviews));
    }
}