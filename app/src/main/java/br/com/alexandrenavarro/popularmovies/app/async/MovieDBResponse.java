package br.com.alexandrenavarro.popularmovies.app.async;

import br.com.alexandrenavarro.popularmovies.app.model.Movie;

/**
 * Created by alexandrenavarro on 23/01/17.
 */

public interface MovieDBResponse {

    void onResult(Movie[] movies);

}
