package com.customcode420.caffeinecutter;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

public class ProgressBarAnimation extends Animation {
    //Setting some private variables
    private ProgressBar progressBar;
    private float from;
    private float to;
    //Making constructor
    public ProgressBarAnimation(ProgressBar progressBar){
        super();
        this.progressBar = progressBar;
    }
    //Code to apply the transformation to the progress bar
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }
    //Method to set start and end of animation
    public void setStartEnd(float from, float to){
        this.from = from;
        this.to = to;
    }
}
