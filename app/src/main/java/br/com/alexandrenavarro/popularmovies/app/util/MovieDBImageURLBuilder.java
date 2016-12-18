package br.com.alexandrenavarro.popularmovies.app.util;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class MovieDBImageURLBuilder {

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

    public static String buildURL(String path){
        return BASE_IMAGE_URL + "w185" + path;
    }
}
