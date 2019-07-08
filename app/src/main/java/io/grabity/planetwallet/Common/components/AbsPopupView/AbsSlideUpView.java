package io.grabity.planetwallet.Common.components.AbsPopupView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.MissingFormatArgumentException;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.FontManager;
import io.grabity.planetwallet.Widgets.FontTextView;

/**
 * Created by. JcobPark on 2018. 08. 29
 */
public abstract class AbsSlideUpView implements PopupView, View.OnClickListener {

    private Context context;
    private View contentView;
    private View background;

    private ArrayList< View > dismissViews;

    public AbsSlideUpView( Context context ) {
        this.context = context;
        ( ( PlanetWalletActivity ) context ).addPopup( this );
        createView( );
    }

    public Context getContext( ) {
        return context;
    }

    public Activity getActivity( ) {
        return ( Activity ) context;
    }


    public void setContext( Context context ) {
        this.context = context;
    }

    private void createView( ) {

        background = new View( context );
        contentView = this.contentView( );

        if ( contentView != null ) {

            overrideFonts( contentView );

            {
                background.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
                background.setBackgroundColor( Color.parseColor( "#19000000" ) );
                background.setAlpha( 0.0f );
                background.setOnClickListener( this );
                ( ( ViewGroup ) ( ( Activity ) context ).findViewById( android.R.id.content ) ).addView( background );

            }

            contentView.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
            contentView.setY( getScreenHeight( this.context ) );
            ( ( ViewGroup ) ( ( Activity ) context ).findViewById( android.R.id.content ) ).addView( contentView );

        }

    }

    public abstract void onCreateView( );

    abstract protected View contentView( );

    protected void dismissView( ArrayList< View > views ) {
        this.dismissViews = views;
        for ( View v : views ) {
            v.setOnClickListener( this );
        }
    }

    protected void dismissView( View... views ) {
        this.dismissViews = new ArrayList<>( );
        for ( View v : views ) {
            this.dismissViews.add( v );
            v.setOnClickListener( this );
        }

    }

    public void setData( ) {

    }


    public void preAnimation( ) {

    }

    protected void overrideFonts( final View v ) {
        try {
            if ( v instanceof ViewGroup ) {
                ViewGroup vg = ( ViewGroup ) v;
                for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                    View child = vg.getChildAt( i );
                    overrideFonts( child );
                }
            } else if ( v instanceof FontTextView ) {
                ( ( TextView ) v ).setTypeface( FontManager.getInstance( ).getFont( ( ( FontTextView ) v ).getFontStyle( ) ) );
            } else if ( v instanceof TextView ) {
                ( ( TextView ) v ).setTypeface( FontManager.getInstance( ).getFont( ( ( TextView ) v ).getTypeface( ).getStyle( ) ) );
            }
        } catch ( Exception e ) {

        }
    }

    public void show( long duration, Interpolator interpolator ) {

        if ( contentView != null ) {
            onCreateView( );

            ObjectAnimator animator = ObjectAnimator.ofFloat( contentView, "y", getScreenHeight( this.context ) / 1.0f, 0.0f );
            animator.setDuration( duration );
            animator.setInterpolator( interpolator );
            animator.addListener( new Animator.AnimatorListener( ) {
                @Override
                public void onAnimationStart( Animator animation ) {
                    preAnimation( );
                }

                @Override
                public void onAnimationEnd( Animator animation ) {
                    AbsSlideUpView.this.setData( );
                }

                @Override
                public void onAnimationCancel( Animator animation ) {

                }

                @Override
                public void onAnimationRepeat( Animator animation ) {

                }
            } );
            animator.start( );
            background.animate( ).alpha( 1.0f ).setDuration( duration ).setInterpolator( interpolator ).start( );
        }

    }

    @Override
    public void onBackPressed( ) {
        dismiss( );
    }

    public void show( long duration ) {

        this.show( duration, new AccelerateDecelerateInterpolator( ) );

    }

    public void show( ) {

        this.show( 400, new AccelerateDecelerateInterpolator( ) );

    }

    public void dismiss( ) {
        this.dismiss( 400 );
    }

    public void dismiss( long duration ) {

        if ( contentView != null ) {
            ObjectAnimator animator = ObjectAnimator.ofFloat( contentView, "y", 0.0f, getScreenHeight( this.context ) / 1.0f );
            animator.setDuration( duration );
            animator.setInterpolator( new AccelerateDecelerateInterpolator( ) );

            ObjectAnimator animator2 = ObjectAnimator.ofFloat( background, "alpha", 1.0f, 0.0f );
            animator2.setDuration( duration );
            animator2.setInterpolator( new AccelerateDecelerateInterpolator( ) );

            AnimatorSet animatorSet = new AnimatorSet( );
            animatorSet.playTogether( animator, animator2 );
            animatorSet.setDuration( duration );
            animatorSet.addListener( new Animator.AnimatorListener( ) {
                @Override
                public void onAnimationStart( Animator animation ) {
                }

                @Override
                public void onAnimationEnd( Animator animation ) {

                    ( ( ViewGroup ) ( ( Activity ) context ).findViewById( android.R.id.content ) ).removeView( background );
                    ( ( ViewGroup ) ( ( Activity ) context ).findViewById( android.R.id.content ) ).removeView( contentView );

                }

                @Override
                public void onAnimationCancel( Animator animation ) {

                }

                @Override
                public void onAnimationRepeat( Animator animation ) {

                }
            } );
            animatorSet.start( );

        }

    }


    public < T extends View > T findViewById( int resId ) {
        if ( this.contentView != null ) return this.contentView.findViewById( resId );
        else return null;
    }

    @Override
    public void onClick( View v ) {

        if ( dismissViews != null ) {
            if ( dismissViews.contains( v ) )
                getActivity( ).onBackPressed( );
        }

        if ( v == background ) {
            getActivity( ).onBackPressed( );
        }
    }

    public View getContentView( ) {
        return contentView;
    }

    public View getBackground( ) {
        return background;
    }

    public static int getScreenWidth( Context context ) {
        Resources resources = context.getResources( );
        DisplayMetrics metrics = resources.getDisplayMetrics( );
        return ( int ) metrics.widthPixels;
    }

    public static int getScreenHeight( Context context ) {
        Resources resources = context.getResources( );
        DisplayMetrics metrics = resources.getDisplayMetrics( );
        return ( int ) metrics.heightPixels;
    }

    public String localized( int id, Object... formats ) {
        String returnValue;
        try {
            if ( formats.length > 0 ) {
                if ( getActivity( ).getResources( ).getString( id ).contains( "%s" ) ) {
                    returnValue = String.format( getActivity( ).getResources( ).getString( id ), formats );
                } else
                    returnValue = String.format( getActivity( ).getResources( ).getString( id ) );
            } else
                returnValue = String.format( getActivity( ).getResources( ).getString( id ) );

        } catch ( MissingFormatArgumentException e ) {
            returnValue = getActivity( ).getResources( ).getString( id ).replace( "%s", "" );
        }
        return returnValue;
    }
}