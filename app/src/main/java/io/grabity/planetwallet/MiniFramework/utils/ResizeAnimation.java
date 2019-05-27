package io.grabity.planetwallet.MiniFramework.utils;

import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by. JcobPark on 2018. 08. 29
 */
public class ResizeAnimation {

    View view;
    ValueAnimator va;

    public ResizeAnimation init( final View view, long duration, int widthFrom, int widthTo, int heightFrom, int heightTo ) {

        this.view = view;
        int valueMax = widthTo - widthFrom > heightTo - heightFrom ? widthTo - widthFrom : heightTo - heightFrom;
        va = ValueAnimator.ofInt( heightFrom, heightTo );
        va.setDuration( duration );
        va.addUpdateListener( new ValueAnimator.AnimatorUpdateListener( ) {
            public void onAnimationUpdate( ValueAnimator animation ) {
                Integer value = ( Integer ) animation.getAnimatedValue( );
                ResizeAnimation.this.view.getLayoutParams( ).height = value.intValue( );
                ResizeAnimation.this.view.requestLayout( );
            }
        } );

        return this;
    }

    public ResizeAnimation init( final View view, long duration, int heightFrom, int heightTo ) {

        this.view = view;
        va = ValueAnimator.ofInt( heightFrom, heightTo );
        va.setDuration( duration );
        va.addUpdateListener( new ValueAnimator.AnimatorUpdateListener( ) {
            public void onAnimationUpdate( ValueAnimator animation ) {
                Integer value = ( Integer ) animation.getAnimatedValue( );
                ResizeAnimation.this.view.getLayoutParams( ).height = value.intValue( );
                ResizeAnimation.this.view.requestLayout( );
            }
        } );

        return this;
    }

    public void start( ) {
        if ( va != null )
            va.start( );
    }
}
