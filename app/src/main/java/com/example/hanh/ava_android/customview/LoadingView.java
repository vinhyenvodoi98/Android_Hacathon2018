package com.example.hanh.ava_android.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class LoadingView extends AppCompatImageView {

    private Context context;
    private Integer index = 0;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            index ++;
            invalidate();
            handler.postDelayed(runnable, 30);
        }
    };

    private Handler handler = new Handler();
    public LoadingView(Context context) {
        super(context);
        this.context = context;
        handler.postDelayed(runnable, 30);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        handler.postDelayed(runnable, 30);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        handler.postDelayed(runnable, 30);
    }

    private void show() {
        index = 0;
        setVisibility(VISIBLE);
    }

    private void hide() {
        setVisibility(GONE);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);


    }
}
