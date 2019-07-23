package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;

public class ActionEditText extends androidx.appcompat.widget.AppCompatEditText implements Themeable {

    private int defaultTheme;

    private Paint paintBackgroundColor;
    private Paint paintBorderColor;

    private float cornerRadius;
    private float borderWidth;
    private float width;
    private float height;

    private int backgroundColor;
    private int borderColor;
    private int focusBorderColor;

    private boolean isFocus = false;

    public ActionEditText( Context context ) {
        super( context );
    }

    public ActionEditText( Context context, AttributeSet attrs ) {
        this( context, attrs, android.R.attr.editTextStyle );
    }

    public ActionEditText( Context context, AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );

        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.ActionEditText, defStyle, 0 );

        cornerRadius = a.getDimensionPixelSize( R.styleable.RoundEditText_cornerRadius, 0 );
        borderWidth = a.getDimensionPixelSize( R.styleable.RoundEditText_borderWidth, 0 );

        backgroundColor = a.getColor( R.styleable.RoundEditText_backgroundColor, Color.TRANSPARENT );
        borderColor = a.getColor( R.styleable.RoundEditText_borderColor, Color.TRANSPARENT );
        focusBorderColor = a.getColor( R.styleable.RoundEditText_focusBorderColor, Color.TRANSPARENT );

        defaultTheme = a.getInt( R.styleable.RoundEditText_defaultTheme, 0 );

        if ( defaultTheme > 0 ) {
            borderWidth = Utils.dpToPx( getContext( ), 1 );
            cornerRadius = Utils.dpToPx( getContext( ), 8 );
            setTheme( false );
        }

        a.recycle( );
        viewInit( );


    }

    public void viewInit( ) {
        paintBackgroundColor = new Paint( Paint.ANTI_ALIAS_FLAG );
        paintBackgroundColor.setColor( backgroundColor );
        paintBackgroundColor.setStyle( Paint.Style.FILL );

        paintBorderColor = new Paint( Paint.ANTI_ALIAS_FLAG );
        paintBorderColor.setColor( borderColor );
        paintBorderColor.setStyle( Paint.Style.STROKE );
        paintBorderColor.setStrokeWidth( borderWidth );

        setFocusable( true );


        super.setBackgroundColor( Color.TRANSPARENT );
    }

    @Override
    public InputConnection onCreateInputConnection( EditorInfo outAttrs ) {
        InputConnection conn = super.onCreateInputConnection( outAttrs );
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }

    @Override
    public void setBackgroundColor( int color ) {
        backgroundColor = color;
        invalidate( );
    }

    public int getBorderColor( ) {
        return borderColor;
    }

    public void setBorderColor( int borderColor ) {
        this.borderColor = borderColor;
        invalidate( );
    }

    public int getFocusBorderColor( ) {
        return focusBorderColor;
    }

    public void setFocusBorderColor( int focusBorderColor ) {
        this.focusBorderColor = focusBorderColor;
        invalidate( );
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        width = w;
        height = h;
        setMeasuredDimension( MeasureSpec.getSize( ( int ) width ), MeasureSpec.getSize( ( int ) height ) );
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        paintBackgroundColor.setColor( backgroundColor );
        paintBorderColor.setColor( isFocus ? focusBorderColor : borderColor );
//        @SuppressLint( "DrawAllocation" ) RectF rectF = new RectF( hScroll + borderWidth / 2.0f, vScroll + borderWidth / 2.0f, hScroll + width - borderWidth / 2.0f, vScroll + height - borderWidth / 2.0f );
        canvas.drawRoundRect( new RectF( borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f ), cornerRadius, cornerRadius, paintBackgroundColor );
        canvas.drawRoundRect( new RectF( borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f ), cornerRadius, cornerRadius, paintBorderColor );
        super.onDraw( canvas );
    }

    @Override
    protected void onFocusChanged( boolean focused, int direction, Rect previouslyFocusedRect ) {
        super.onFocusChanged( focused, direction, previouslyFocusedRect );
        isFocus = focused;
        invalidate( );
    }

    @Override
    public void setTheme( boolean theme ) {
        if ( defaultTheme > 0 ) {
            theme = ( defaultTheme == 2 ) != theme;
            if ( !theme ) { // Dark
                backgroundColor = Color.argb( 255, 17, 17, 23 );
                borderColor = Color.argb( 255, 30, 30, 40 );
                focusBorderColor = Color.argb( 255, 188, 189, 213 );
                setTextColor( Color.argb( 255, 255, 255, 255 ) );
                setHintTextColor( Color.argb( 255, 92, 89, 100 ) );
            } else { // Light
                backgroundColor = Color.argb( 255, 255, 255, 255 );
                borderColor = Color.argb( 255, 237, 237, 237 );
                focusBorderColor = Color.argb( 255, 0, 0, 0 );
                setTextColor( Color.argb( 255, 0, 0, 0 ) );
                setHintTextColor( Color.argb( 255, 170, 170, 170 ) );
            }
        }
    }
}
