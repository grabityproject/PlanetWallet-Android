package io.grabity.planetwallet.Widgets.PlanetWalletViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import io.grabity.planetwallet.MiniFramework.utils.CornerRound;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.Themeable;

public class PWRelativeLayout extends RelativeLayout implements Themeable {

    private Drawable themeBackground;
    private Drawable defaultBackground;

    private int cornerRadius;
    private int cornerRound;

    private boolean lt;
    private boolean rt;
    private boolean rb;
    private boolean lb;

    private int backgroundColor;

    public PWRelativeLayout( Context context ) {
        super( context );
    }

    public PWRelativeLayout( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public PWRelativeLayout( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.PWRelativeLayout, defStyleAttr, 0 );
        themeBackground = a.getDrawable( R.styleable.PWRelativeLayout_themeBackground );
        if ( getBackground( ) != null )
            defaultBackground = getBackground( ).getConstantState( ).newDrawable( );
        cornerRadius = a.getDimensionPixelSize( R.styleable.PWRelativeLayout_cornerRadius, 0 );
        setCornerRound( a.getInt( R.styleable.PWRelativeLayout_cornerRound, 0 ) );
        a.recycle( );

        setUp( );
    }


    public void setCornerRound( int cornerRound ) {
        this.cornerRound = cornerRound;

        String cornerHex = String.format( "%04X", cornerRound );
        lt = Character.getNumericValue( cornerHex.charAt( 0 ) ) == 1;
        rt = Character.getNumericValue( cornerHex.charAt( 1 ) ) == 1;
        rb = Character.getNumericValue( cornerHex.charAt( 2 ) ) == 1;
        lb = Character.getNumericValue( cornerHex.charAt( 3 ) ) == 1;
    }

    public void setUp( ) {
        if ( getBackground( ) instanceof ColorDrawable ) {
            backgroundColor = ( ( ColorDrawable ) getBackground( ) ).getColor( );
        }
        if ( cornerRadius > 0 ) {
            CornerRound.radius(
                    this,

                    lt ? cornerRadius : 0, lt ? cornerRadius : 0,
                    rt ? cornerRadius : 0, rt ? cornerRadius : 0,
                    rb ? cornerRadius : 0, rb ? cornerRadius : 0,
                    lb ? cornerRadius : 0, lb ? cornerRadius : 0 );
        }
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

    @Override
    public void setBackground( Drawable background ) {
        super.setBackground( background );
        setUp( );
    }

    public Integer getBackgroundColor( ) {
        return backgroundColor;
    }
}
