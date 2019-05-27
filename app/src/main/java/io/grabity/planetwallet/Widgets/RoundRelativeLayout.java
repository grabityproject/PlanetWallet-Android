package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import io.grabity.planetwallet.R;


/**
 * Created by. JcobPark on 2018. 08. 29
 */
public class RoundRelativeLayout extends RelativeLayout {

    private Context context;

    private Paint paintBack;
    private Paint paintBorder;

    private float corner_radius;
    private float border_width;
    private int border_color_normal;
    private int background_color_normal;

    float width;
    float height;
    float radius;


    public RoundRelativeLayout ( Context context ) {
        super( context );
        this.context = context;

        corner_radius = -1;
        border_width = 0;

        border_color_normal = Color.TRANSPARENT;
        background_color_normal = Color.TRANSPARENT;

        this.viewInit( );
    }

    public RoundRelativeLayout ( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public RoundRelativeLayout ( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        this.context = context;
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.RoundRelativeLayout, defStyleAttr, 0 );

        corner_radius = a.getDimensionPixelSize( R.styleable.RoundRelativeLayout_cornerRadius , -1 );

        border_width = a.getDimensionPixelSize( R.styleable.RoundRelativeLayout_borderWidth, 0 );

        border_color_normal = a.getColor( R.styleable.RoundRelativeLayout_borderColor, Color.TRANSPARENT );
        background_color_normal = a.getColor( R.styleable.RoundRelativeLayout_backgroundColor, Color.TRANSPARENT );

        a.recycle( );
        this.viewInit( );
    }

    private void viewInit( ) {
        paintBack = new Paint( Paint.ANTI_ALIAS_FLAG );
        paintBack.setColor( background_color_normal );
        paintBack.setStyle( Paint.Style.FILL );

        paintBorder = new Paint( Paint.ANTI_ALIAS_FLAG );
        paintBorder.setColor( border_color_normal );
        paintBorder.setStyle( Paint.Style.STROKE );
        paintBorder.setStrokeWidth( border_width );

        super.setBackgroundColor( Color.TRANSPARENT );
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        width = w;
        height = h;
        radius = corner_radius;
        if ( corner_radius == -1 )
            radius = height > width ? ( width - border_width ) / 2.0f : ( height - border_width ) / 2.0f;

        setMeasuredDimension( MeasureSpec.getSize( ( int ) width ), MeasureSpec.getSize( ( int ) height ) );
    }

    @Override
    public void setBackgroundColor( int color ) {
        background_color_normal = color;
        invalidate( );
    }


    @Override
    protected void dispatchDraw( Canvas canvas ) {
        radius = corner_radius;
        if ( corner_radius == -1 )
            radius = height > width ? ( width - border_width ) / 2.0f : ( height - border_width ) / 2.0f;


        paintBack.setColor( background_color_normal );
        paintBorder.setColor( border_color_normal );

        canvas.drawRoundRect( new RectF( border_width / 2.0f, border_width / 2.0f, width - border_width / 2.0f, height - border_width / 2.0f ), radius, radius, paintBack );
        canvas.drawRoundRect( new RectF( border_width / 2.0f, border_width / 2.0f, width - border_width / 2.0f, height - border_width / 2.0f ), radius, radius, paintBorder );

        super.dispatchDraw( canvas );
    }

    public float getCorner_radius( ) {
        return corner_radius;
    }

    public void setCorner_radius( float corner_radius ) {
        this.corner_radius = corner_radius;
    }

    public float getBorder_width( ) {
        return border_width;
    }

    public void setBorder_width( float border_width ) {
        this.border_width = border_width;
    }

    public int getBorder_color_normal( ) {
        return border_color_normal;
    }

    public void setBorder_color_normal( int border_color_normal ) {
        this.border_color_normal = border_color_normal;
        invalidate();
    }

    public int getBackground_color_normal( ) {
        return background_color_normal;
    }

    public void setBackground_color_normal( int background_color_normal ) {
        this.background_color_normal = background_color_normal;
        invalidate( );
    }

}
