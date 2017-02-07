package br.com.alexandrenavarro.popularmovies.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.alexandrenavarro.popularmovies.app.R;
import br.com.alexandrenavarro.popularmovies.app.model.Review;

/**
 * Created by alexandrenavarro on 2/6/17.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(@NonNull Context context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ReviewAdapter.ViewHolder viewHolder;

        if(convertView != null && convertView.getTag() != null && convertView.getTag() instanceof ReviewAdapter.ViewHolder){
            viewHolder = (ReviewAdapter.ViewHolder) convertView.getTag();
        }else {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_list_item, parent, false);
            viewHolder = new ReviewAdapter.ViewHolder();
            viewHolder.comment = (TextView) convertView.findViewById(R.id.txt_item_review);
            convertView.setTag(viewHolder);
        }

        viewHolder.comment.setText(getItem(position).getContent());

        return convertView;
    }

    static class ViewHolder{
        TextView comment;
    }
}
