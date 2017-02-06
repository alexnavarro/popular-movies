package br.com.alexandrenavarro.popularmovies.app.async;

import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.model.Review;
import br.com.alexandrenavarro.popularmovies.app.model.Video;

/**
 * Created by alexandrenavarro on 05/02/17.
 */

public interface OnResponse {

    void onResponseReview(List<Review> response);
    void onResponseVideo(List<Video> response);
}
