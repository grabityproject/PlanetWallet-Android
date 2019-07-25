package io.grabity.planetwallet.Widgets;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;


public class BarcodeFrameView extends RelativeLayout {


    private Paint paint;
    private Path path;

    private float width;
    private float height;

    private float borderlineWidth;
    private float borderlineHeight;

    private float borderSize;
    private int borderColor;


    public BarcodeFrameView( Context context ) {
        super( context );
    }

    public BarcodeFrameView( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public BarcodeFrameView( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );

        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.BarcodeFrameView, defStyleAttr, 0 );

        borderSize = a.getDimensionPixelSize( R.styleable.BarcodeFrameView_borderWidth, 2 );
        borderColor = a.getColor( R.styleable.BarcodeFrameView_borderColor, Color.TRANSPARENT );

        a.recycle( );
        this.viewInit( );
    }

    private void viewInit( ) {

        paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        paint.setColor( borderColor );
        paint.setStyle( Paint.Style.STROKE );
        paint.setAntiAlias( true );
        paint.setStrokeWidth( borderSize );
        paint.setStrokeCap( Paint.Cap.ROUND );
        paint.setStrokeJoin( Paint.Join.ROUND );

        path = new Path( );

        super.setBackgroundColor( Color.TRANSPARENT );


    }


    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );

        width = w;
        height = h;

        borderlineWidth = w * 0.12f;
        borderlineHeight = h * 0.12f;

        setMeasuredDimension( MeasureSpec.getSize( ( int ) width ), MeasureSpec.getSize( ( int ) height ) );

    }

    @SuppressLint( "DrawAllocation" )
    @Override
    protected void onDraw( Canvas canvas ) {

        for ( int i = 0; i < 4; i++ ) {

            canvas.drawLine( Utils.dpToPx( getContext( ), 4 ), borderlineWidth, Utils.dpToPx( getContext( ), 4 ), borderlineWidth / 2.0f, paint );
            path.reset( );
            path.moveTo( Utils.dpToPx( getContext( ), 4 ), borderlineWidth / 2.0f );
            path.quadTo( Utils.dpToPx( getContext( ), 4 ), Utils.dpToPx( getContext( ), 4 ), borderlineWidth / 2.0f, Utils.dpToPx( getContext( ), 4 ) );
            canvas.drawLine( borderlineWidth / 2.0f, Utils.dpToPx( getContext( ), 4 ), borderlineWidth, Utils.dpToPx( getContext( ), 4 ), paint );
            canvas.drawPath( path, paint );
            canvas.rotate( 90, width / 2.0f, height / 2.0f );

        }


        super.onDraw( canvas );


    }
}
