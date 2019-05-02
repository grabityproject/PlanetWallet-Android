package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class StretchImageView extends android.support.v7.widget.AppCompatImageView {


    public StretchImageView( Context context ) {
        super( context );
    }

    public StretchImageView( Context context, AttributeSet attrs ) {
        super( context, attrs );
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

}
