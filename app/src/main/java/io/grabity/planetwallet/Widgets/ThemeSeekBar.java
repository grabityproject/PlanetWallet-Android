package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;

import io.grabity.planetwallet.R;

public class ThemeSeekBar extends AppCompatSeekBar implements Themeable {

    private int defaultTheme;

    public ThemeSeekBar( Context context ) {
        super( context );
    }

    public ThemeSeekBar( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );


    }

    public ThemeSeekBar( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ThemeSeekBar, defStyleAttr, 0 );
        defaultTheme = a.getInt( R.styleable.ThemeSeekBar_defaultTheme, 0 );

        if ( defaultTheme > 0 ) {
            setTheme( false );
        }
        a.recycle( );
    }

    @Override
    public void setTheme( boolean theme ) {
        if ( defaultTheme > 0 ) {
            theme = ( defaultTheme == 2 ) != theme;
            if ( !theme ) {
                this.setProgressDrawable( getResources().getDrawable( R.drawable.seekbar_background_black ) );
            } else{
                this.setProgressDrawable( getResources().getDrawable( R.drawable.seekbar_background_white ) );
            }
        }
    }
}
