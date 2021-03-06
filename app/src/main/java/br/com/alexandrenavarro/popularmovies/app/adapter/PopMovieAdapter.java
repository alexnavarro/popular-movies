package br.com.alexandrenavarro.popularmovies.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.R;
import br.com.alexandrenavarro.popularmovies.app.model.Movie;
import br.com.alexandrenavarro.popularmovies.app.util.MovieDBImageURLBuilder;

/**
 * Created by alexandrenavarro on 18/12/16.
 */

public class PopMovieAdapter extends ArrayAdapter<Movie>{

    private static final String LOG_TAG = PopMovieAdapter.class.getSimpleName();

    public PopMovieAdapter(Context context, List<Movie> androidFlavors) {
        super(context, 0, androidFlavors);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie popMovie = getItem(position);
        ViewHolder viewHolder;

        if(convertView != null && convertView.getTag() != null && convertView.getTag() instanceof ViewHolder){
            viewHolder = (ViewHolder) convertView.getTag();
        }else {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.pop_movie_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.movieView = (ImageView) convertView.findViewById(R.id.imv_movie);
            convertView.setTag(viewHolder);
        }

        Picasso.with(getContext()).
                load(MovieDBImageURLBuilder.buildURL(popMovie.getPosterPath()))
                .centerCrop()
                .fit().into(viewHolder.movieView);

        return convertView;
    }

    static class ViewHolder{
        ImageView movieView;
    }
}