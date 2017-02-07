package br.com.alexandrenavarro.popularmovies.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.adapter.ReviewAdapter;
import br.com.alexandrenavarro.popularmovies.app.model.Review;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by alexandrenavarro on 2/6/17.
 */

public class ReviewActivity extends AppCompatActivity {

    private static final String LOG = ReviewActivity.class.getSimpleName();

    public static final String EXTRA_REVIEWS = "EXTRA_REVIEWS";

    @BindView(R.id.list_view_reviews) ListView mReviews;

    private List<Review> reviews = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.label_activity_review);
        reviews = getIntent().getParcelableArrayListExtra(EXTRA_REVIEWS);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        bind();
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

    private void bind(){
        mReviews.setAdapter(new ReviewAdapter(getApplicationContext(), reviews));
    }
}