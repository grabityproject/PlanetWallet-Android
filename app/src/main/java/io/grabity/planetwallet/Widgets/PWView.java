package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import io.grabity.planetwallet.R;

public class PWView extends View implements Themeable {

    private Drawable themeBackground;
    private Drawable defaultBackground;

    public PWView( Context context ) {
        super( context );
    }

    public PWView( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public PWView( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.GlobalAttrDeclare, defStyleAttr, 0 );
        themeBackground = a.getDrawable( R.styleable.GlobalAttrDeclare_themeBackground );
        a.recycle( );
    }

    @Override
    public void setTheme( boolean theme ) {
        if ( themeBackground != null ) {
            if ( defaultBackground == null ) {
                defaultBackground = getBackground( ).getConstantState( ).newDrawable( );
            }

            if ( theme ) {
                setBackground( themeBackground );
            } else {
                setBackground( defaultBackground );
            }
        }
    }
}
