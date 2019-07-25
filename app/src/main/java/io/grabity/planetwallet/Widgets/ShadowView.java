package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.R;


/**
 * Created by. JcobPark on 2018. 08. 29
 */
public class ShadowView extends View {

    private int direction;
    private int startColor;
    private int endColor;

    private GradientDrawable g;
    private GradientDrawable.Orientation orientation;


    public ShadowView( Context context ) {
        super( context );
        startColor = Color.parseColor( "#22000000" );
        endColor = Color.parseColor( "#00000000" );
        direction = 1;
        viewInit( );
    }

    public ShadowView( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public ShadowView( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ShadowView, defStyleAttr, 0 );
        startColor = a.getColor( R.styleable.ShadowView_start, Color.parseColor( "#22000000" ) );
        endColor = a.getColor( R.styleable.ShadowView_end, Color.parseColor( "#00000000" ) );
        direction = a.getInt( R.styleable.ShadowView_direction, 1 );
        a.recycle( );
        viewInit( );
    }

    void viewInit( ) {

        orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        switch ( direction ) { // 1~7
            case 1: {
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            }
            case 2: {
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            }
            case 3: {
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            }
            case 4: {
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            }
            case 5: {
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            }
            case 6: {
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            }
            case 7: {
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
            }
        }
        g = new GradientDrawable( orientation, new int[]{ startColor, endColor } );
        g.setShape( GradientDrawable.RECTANGLE );
        setBackground( g );
    }

    public int getStartColor( ) {
        return startColor;
    }


    public int getEndColor( ) {
        return endColor;
    }


    public void setShadowColor( int startColor, int endColor ) {
        this.startColor = startColor;
        this.endColor = endColor;

        g = new GradientDrawable( orientation, new int[]{ getStartColor( ), getEndColor( ) } );
        setBackground( g );

    }
}
