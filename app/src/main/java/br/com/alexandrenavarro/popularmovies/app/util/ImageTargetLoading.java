package br.com.alexandrenavarro.popularmovies.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by alexandrenavarro on 06/02/17.
 */

public class ImageTargetLoading implements Target {

    public static final String TAG = ImageTargetLoading.class.getSimpleName();

    private Context context;
    private final ImageView imageView;
    private final TextView textView;

    public ImageTargetLoading(Context context, ImageView imageView, TextView textView){
        this.context = context;
        this.imageView = imageView;
        this.textView = textView;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        assert imageView != null;
        imageView.setImageBitmap(bitmap);
        Palette.from(bitmap)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch textSwatch = palette.getDarkMutedSwatch();

                        if(textSwatch != null) {
                            textView.setBackgroundColor(textSwatch.getRgb());
                            textView.setTextColor(textSwatch.getBodyTextColor());
                        }else {
                            textView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_light));
                        }

                    }
                });
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.d(TAG, errorDrawable.toString());
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
