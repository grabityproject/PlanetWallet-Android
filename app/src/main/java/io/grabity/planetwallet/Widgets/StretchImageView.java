package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import io.grabity.planetwallet.R;

public class StretchImageView extends android.support.v7.widget.AppCompatImageView implements Themeable {

    private Drawable themeDrawable;
    private Drawable defaultDrawable;

    public StretchImageView( Context context ) {
        super( context );
    }

    public StretchImageView( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public StretchImageView( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.GlobalAttrDeclare, defStyleAttr, 0 );
        themeDrawable = a.getDrawable( R.styleable.GlobalAttrDeclare_themeSrc );
        a.recycle( );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        Drawable d = getDrawable( );

        if ( d != null ) {
            int width = MeasureSpec.getSize( widthMeasureSpec );
            int height = ( int ) Math.ceil( ( float ) width * ( float ) d.getIntrinsicHeight( ) / d.getIntrinsicWidth( ) );
            setMeasuredDimension( width, height );
        } else {
            super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        }

    }

    @Override
    public void setTheme( boolean theme ) {
        if ( themeDrawable != null ) {
            if ( defaultDrawable == null ) {
                defaultDrawable = getDrawable( ).getConstantState( ).newDrawable( );
            }
            if ( !theme ) {
                setImageDrawable( defaultDrawable );
            } else {
                setImageDrawable( themeDrawable );
            }
        }
    }
}