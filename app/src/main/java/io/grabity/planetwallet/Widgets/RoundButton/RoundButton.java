package io.grabity.planetwallet.Widgets.RoundButton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import io.grabity.planetwallet.R;


/**
 * Created by. JcobPark on 2018. 08. 29
 */
public class RoundButton extends android.support.v7.widget.AppCompatButton implements View.OnClickListener {

    private int state;

    private Paint paintBack;
    private Paint paintBorder;

    private float cornerRadius;
    private float borderWidth;
    private int borderColorDisable;
    private int borderColorNormal;
    private int borderColorHighlight;
    private int backgroundColorDisable;
    private int backgroundColorNormal;
    private int backgroundColorHighlight;
    private int textColorDisable;
    private int textColorNormal;
    private int textColorHighlight;

    private int backgroundColorNormalGradientStart;
    private int backgroundColorNormalGradientEnd;


    float width;
    float height;
    float radius;


    public RoundButton( Context context ) {
        super( context );
        cornerRadius = -1;
        borderWidth = 0;

        borderColorDisable = Color.TRANSPARENT;
        borderColorNormal = Color.TRANSPARENT;
        borderColorHighlight = Color.TRANSPARENT;

        backgroundColorDisable = Color.TRANSPARENT;
        backgroundColorNormal = Color.TRANSPARENT;
        backgroundColorHighlight = Color.TRANSPARENT;

        textColorDisable = Color.WHITE;
        textColorNormal = Color.TRANSPARENT;
        textColorHighlight = Color.WHITE;

        backgroundColorNormalGradientStart = Color.TRANSPARENT;
        backgroundColorNormalGradientEnd = Color.TRANSPARENT;
        this.viewInit( );
    }

    public RoundButton( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public RoundButton( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.RoundButton, defStyleAttr, 0 );

        cornerRadius = a.getDimensionPixelSize( R.styleable.RoundButton_cornerRadius, -1 );
        borderWidth = a.getDimensionPixelSize( R.styleable.RoundButton_borderWidth, 0 );

        borderColorDisable = a.getColor( R.styleable.RoundButton_borderColorDisable, Color.TRANSPARENT );
        borderColorNormal = a.getColor( R.styleable.RoundButton_borderColorNormal, Color.TRANSPARENT );
        borderColorHighlight = a.getColor( R.styleable.RoundButton_borderColorHighlight, Color.TRANSPARENT );

        backgroundColorDisable = a.getColor( R.styleable.RoundButton_backgroundColorDisable, Color.TRANSPARENT );
        backgroundColorNormal = a.getColor( R.styleable.RoundButton_backgroundColorNormal, Color.TRANSPARENT );
        backgroundColorHighlight = a.getColor( R.styleable.RoundButton_backgroundColorHighlight, Color.TRANSPARENT );

        textColorDisable = a.getColor( R.styleable.RoundButton_textColorDisable, Color.WHITE );
        textColorNormal = a.getColor( R.styleable.RoundButton_textColorNormal, Color.WHITE );
        textColorHighlight = a.getColor( R.styleable.RoundButton_textColorHighlight, Color.WHITE );

        backgroundColorNormalGradientStart = a.getColor( R.styleable.RoundButton_backgroundColorNormalGradientStart, Color.TRANSPARENT );
        backgroundColorNormalGradientEnd = a.getColor( R.styleable.RoundButton_backgroundColorNormalGradientEnd, Color.TRANSPARENT );

        a.recycle( );
        this.viewInit( );
    }

    private void viewInit( ) {

        state = MotionEvent.ACTION_UP;
        paintBack = new Paint( Paint.ANTI_ALIAS_FLAG );
        paintBack.setColor( backgroundColorNormal );
        paintBack.setStyle( Paint.Style.FILL );

        paintBorder = new Paint( Paint.ANTI_ALIAS_FLAG );
        paintBorder.setColor( borderColorNormal );
        paintBorder.setStyle( Paint.Style.STROKE );
        paintBorder.setStrokeWidth( borderWidth );

        super.setBackgroundColor( Color.TRANSPARENT );
        super.setTextColor( textColorNormal );


    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        width = w;
        height = h;
        radius = cornerRadius;
        if ( cornerRadius == -1 )
            radius = height > width ? ( width - borderWidth ) / 2.0f : ( height - borderWidth ) / 2.0f;

        setMeasuredDimension( MeasureSpec.getSize( ( int ) width ), MeasureSpec.getSize( ( int ) height ) );
    }

    @Override
    public void setOnClickListener( OnClickListener l ) {
        super.setOnClickListener( l );
        this.setOnTouchListener( onTouchListener );
    }

    @Override
    public void setTextColor( int color ) {
        textColorNormal = color;
        textColorHighlight = color;
        super.setTextColor( color );
    }

    @Override
    public void setBackgroundColor( int color ) {
        backgroundColorNormal = color;
        backgroundColorHighlight = color;
        invalidate( );
    }

    public void setSuperBackgroundColor( int color ) {
        super.setBackgroundColor( color );
    }

    public void setSuperTextColor( int color ) {
        super.setTextColor( color );
    }

    public void setBackgroundColor( int color, int highlight ) {
        backgroundColorNormal = color;
        backgroundColorHighlight = highlight;
        invalidate( );
    }

    public void setBackgroundColorDisable( int backgroundColorDisable ) {
        this.backgroundColorDisable = backgroundColorDisable;
    }

    @Override
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        if ( !enabled ) {
            RoundButton.super.setTextColor( textColorDisable );
            if ( textColorNormal == textColorDisable ) {
                invalidate( );
            }
        } else {
            RoundButton.super.setTextColor( textColorNormal );
            if ( textColorNormal == textColorDisable ) {
                invalidate( );
            }
        }
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        radius = cornerRadius;
        if ( cornerRadius == -1 )
            radius = height > width ? ( width - borderWidth ) / 2.0f : ( height - borderWidth ) / 2.0f;


        if ( !this.isEnabled( ) ) {

            paintBack.setColor( backgroundColorDisable );
            paintBorder.setColor( borderColorDisable );

            canvas.drawRoundRect( new RectF( borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f ), radius, radius, paintBack );
            canvas.drawRoundRect( new RectF( borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f ), radius, radius, paintBorder );
            RoundButton.super.setTextColor( textColorDisable );

            super.onDraw( canvas );
            return;
        }

        if ( isSelected( ) ) {
            state = MotionEvent.ACTION_DOWN;
        }

        switch ( state ) {

            case MotionEvent.ACTION_DOWN: {

                paintBack.setColor( backgroundColorHighlight );
                paintBorder.setColor( borderColorHighlight );

                canvas.drawRoundRect( new RectF( borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f ), radius, radius, paintBack );
                canvas.drawRoundRect( new RectF( borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f ), radius, radius, paintBorder );

            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {

                paintBack.setColor( backgroundColorNormal );
                paintBorder.setColor( borderColorNormal );

                if ( backgroundColorNormalGradientStart != Color.TRANSPARENT && backgroundColorNormalGradientEnd != Color.TRANSPARENT ) {
                    LinearGradient gradient = new LinearGradient( 0, 0, width, 0, backgroundColorNormalGradientStart, backgroundColorNormalGradientEnd, Shader.TileMode.CLAMP );
                    paintBack.setDither( true );
                    paintBack.setShader( gradient );
                }

                canvas.drawRoundRect( new RectF( borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f ), radius, radius, paintBack );
                canvas.drawRoundRect( new RectF( borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f ), radius, radius, paintBorder );

            }
            break;

        }

        super.onDraw( canvas );


    }


    public float getCornerRadius( ) {
        return cornerRadius;
    }

    public void setCornerRadius( float cornerRadius ) {
        this.cornerRadius = cornerRadius;
    }

    public float getBorderWidth( ) {
        return borderWidth;
    }

    public void setBorderWidth( float borderWidth ) {
        this.borderWidth = borderWidth;
    }

    public int getBorderColorDisable( ) {
        return borderColorDisable;
    }

    public void setBorderColorDisable( int borderColorDisable ) {
        this.borderColorDisable = borderColorDisable;
    }

    public int getBorderColorNormal( ) {
        return borderColorNormal;
    }

    public void setBorderColorNormal( int borderColorNormal ) {
        this.borderColorNormal = borderColorNormal;
    }

    public int getBorderColorHighlight( ) {
        return borderColorHighlight;
    }

    public void setBorderColorHighlight( int borderColorHighlight ) {
        this.borderColorHighlight = borderColorHighlight;
    }

    public int getBackgroundColorDisable( ) {
        return backgroundColorDisable;
    }

    public int getBackgroundColorNormal( ) {
        return backgroundColorNormal;
    }

    public void setBackgroundColorNormal( int backgroundColorNormal ) {
        this.backgroundColorNormal = backgroundColorNormal;
        invalidate( );
    }

    public int getBackgroundColorHighlight( ) {
        return backgroundColorHighlight;
    }

    public void setBackgroundColorHighlight( int backgroundColorHighlight ) {
        this.backgroundColorHighlight = backgroundColorHighlight;
    }

    public int getTextColorDisable( ) {
        return textColorDisable;
    }

    public void setTextColorDisable( int textColorDisable ) {
        this.textColorDisable = textColorDisable;
    }

    public int getTextColorNormal( ) {
        return textColorNormal;
    }

    public void setTextColorNormal( int textColorNormal ) {
        this.textColorNormal = textColorNormal;
    }

    public int getTextColorHighlight( ) {
        return textColorHighlight;
    }

    public void setTextColorHighlight( int textColorHighlight ) {
        this.textColorHighlight = textColorHighlight;
    }


    public int getBackgroundColorNormalGradientStart( ) {
        return backgroundColorNormalGradientStart;
    }

    public void setBackgroundColorNormalGradientStart( int backgroundColorNormalGradientStart ) {
        this.backgroundColorNormalGradientStart = backgroundColorNormalGradientStart;
    }

    public int getBackgroundColorNormalGradientEnd( ) {
        return backgroundColorNormalGradientEnd;
    }

    public void setBackgroundColorNormalGradientEnd( int backgroundColorNormalGradientEnd ) {
        this.backgroundColorNormalGradientEnd = backgroundColorNormalGradientEnd;
    }


    private OnTouchListener onTouchListener = new OnTouchListener( ) {
        @Override
        public boolean onTouch( View v, MotionEvent event ) {
            switch ( event.getAction( ) ) {

                case MotionEvent.ACTION_DOWN: {
                    state = event.getAction( );
                    RoundButton.super.setBackgroundColor( Color.TRANSPARENT );
                    RoundButton.super.setTextColor( textColorHighlight );
                    if ( textColorHighlight == textColorNormal ) {
                        invalidate( );
                    }

                }
                break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP: {
                    state = event.getAction( );
                    RoundButton.super.setBackgroundColor( Color.TRANSPARENT );
                    RoundButton.super.setTextColor( textColorNormal );
                    if ( textColorHighlight == textColorNormal ) {
                        invalidate( );
                    }

                }
                break;
            }
            return false;
        }
    };

    public void setState( int state ) {
        this.state = state;
    }

    @Override
    public void onClick( View v ) {
        // do not disturb
    }
}
