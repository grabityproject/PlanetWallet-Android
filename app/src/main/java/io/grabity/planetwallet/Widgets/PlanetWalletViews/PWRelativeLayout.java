package io.grabity.planetwallet.Widgets.PlanetWalletViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import io.grabity.planetwallet.MiniFramework.utils.CornerRound;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.Themeable;

public class PWRelativeLayout extends RelativeLayout implements Themeable {

    private Drawable themeBackground;
    private Drawable defaultBackground;

    private int cornerRadius;

    public PWRelativeLayout( Context context ) {
        super( context );
    }

    public PWRelativeLayout( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public PWRelativeLayout( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.GlobalAttrDeclare, defStyleAttr, 0 );
        themeBackground = a.getDrawable( R.styleable.GlobalAttrDeclare_themeBackground );
        cornerRadius = a.getDimensionPixelSize( R.styleable.GlobalAttrDeclare_cornerRadius, 0 );

        this.viewInit( );
        a.recycle( );
    }

    public void viewInit( ) {
        if ( cornerRadius > 0 ) {
            CornerRound.radius( this, 0, 0, 0, 0, cornerRadius, cornerRadius, cornerRadius, cornerRadius );
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
            this.viewInit( );
        }
    }

    public Integer getBackgroundColor( ) {
        if ( getBackground( ) instanceof ColorDrawable ) {
            return ( ( ColorDrawable ) getBackground( ) ).getColor( );
        }
        return null;
    }
}
