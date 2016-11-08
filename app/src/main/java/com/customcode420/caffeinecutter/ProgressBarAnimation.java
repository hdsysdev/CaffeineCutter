package com.customcode420.caffeinecutter;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;


public class ProgressBarAnimation extends Animation {
    private ProgressBar progressBar;
    private float from;
    private float to;

    public ProgressBarAnimation(ProgressBar progressBar){
        super();
        this.progressBar = progressBar;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }
    public void setStartEnd(float from, float to){
        this.from = from;
        this.to = to;
    }
}
