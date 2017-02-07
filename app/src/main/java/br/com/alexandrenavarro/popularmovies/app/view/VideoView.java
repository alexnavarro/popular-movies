package br.com.alexandrenavarro.popularmovies.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import br.com.alexandrenavarro.popularmovies.app.R;
import br.com.alexandrenavarro.popularmovies.app.model.Video;

/**
 * Created by alexandrenavarro on 07/02/17.
 */

public class VideoView extends FrameLayout {

    private Video video;
    private View bottom;

    public VideoView(Context context) {
        super(context);
        initView();
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public VideoView(Context context, Video video){
        super(context);
        this.video = video;
        initView();
    }

    private void initView(){
        inflate(getContext(), R.layout.trailer_list_item, this);
        TextView txtName = (TextView) findViewById(R.id.txt_name);
        bottom =  findViewById(R.id.view_bottom);
        txtName.setText(video.getName());
    }

    public Video getVideo() {
        return video;
    }

    public void hidBottomSeparator(){
        bottom.setVisibility(GONE);
    }
}
