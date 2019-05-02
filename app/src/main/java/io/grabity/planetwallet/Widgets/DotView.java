package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import io.grabity.planetwallet.R;

public class DotView extends View {

    private int dot_color = Color.WHITE;
    private float width = 0;
    private float height = 0;
    private Paint paint;

    public DotView( Context context ) {
        super( context );
        viewInit( );
    }

    public DotView( Context context, @Nullable AttributeSet attrs ) {
        this( context, attrs, 0 );
    }


    public DotView( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.DotView, defStyleAttr, 0 );
        dot_color = a.getColor( R.styleable.DotView_dotColor, Color.WHITE );
        a.recycle( );
        viewInit( );
    }

    private void viewInit( ) {
        paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        paint.setStyle( Paint.Style.FILL );
        paint.setColor( dot_color );
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        width = w;
        height = h;
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw( canvas );
        if ( width == 0 && height == 0 ) {
            width = canvas.getWidth( );
            height = canvas.getHeight( );
        }
        paint.setColor( dot_color );
        canvas.drawCircle( width / 2.0f, height / 2.0f, width >= height ? width / 2.0f : height / 2.0f, paint );

    }

    public void setDotColor( int color ) {
        this.dot_color = color;
        invalidate( );
    }
}
