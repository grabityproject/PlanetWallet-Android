package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class StretchHorizontalImageView extends android.support.v7.widget.AppCompatImageView {


    public StretchHorizontalImageView( Context context ) {
        super( context );
    }

    public StretchHorizontalImageView( Context context, AttributeSet attrs ) {
        super( context, attrs );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        Drawable d = getDrawable( );
        if ( d != null ) {
            int height = MeasureSpec.getSize( heightMeasureSpec );
            int width = ( int ) Math.ceil( ( float ) height * ( float ) d.getIntrinsicWidth( ) / d.getIntrinsicHeight( ) );
            setMeasuredDimension( width, height );
        } else {
            super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        }

    }

}
