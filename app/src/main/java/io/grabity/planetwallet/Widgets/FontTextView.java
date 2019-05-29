package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

import io.grabity.planetwallet.R;

public class FontTextView extends android.support.v7.widget.AppCompatTextView implements Themeable {

    private int defaultTheme;

    private Integer themeTextColor;
    private Integer defaultTextColor;

    private int fontStyle;

    public FontTextView( Context context ) {
        super( context );
    }

    public FontTextView( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public FontTextView( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.FontTextView, defStyleAttr, 0 );
        defaultTheme = a.getInt( R.styleable.FontTextView_defaultTheme, 0 );
        themeTextColor = a.getColor( R.styleable.FontTextView_themeTextColor, Color.TRANSPARENT );
        fontStyle = a.getInt( R.styleable.FontTextView_typefaceStyle, 0 );
        if ( getTypeface( ).getStyle( ) != Typeface.NORMAL ) {
            fontStyle = getTypeface( ).getStyle( );
        }
        defaultTheme = a.getInt( R.styleable.RoundButton_defaultTheme, 0 );
        if ( defaultTheme > 0 ) {
            setTheme( false );
        }
        a.recycle( );
    }

    public int getFontStyle( ) {
        return fontStyle;
    }

    public void setFontStyle( int fontStyle ) {
        this.fontStyle = fontStyle;
    }

    @Override
    public void setTheme( boolean theme ) {
        if ( themeTextColor != null ) {
            if ( defaultTextColor == null ) {
                defaultTextColor = getCurrentTextColor( );
            }
            theme = ( defaultTheme == 2 ) != theme;
            if ( theme ) {
                setTextColor( themeTextColor );
            } else {
                setTextColor( defaultTextColor );
            }
        }
    }
}
