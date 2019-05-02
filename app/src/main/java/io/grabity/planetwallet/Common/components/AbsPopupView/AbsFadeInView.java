package io.grabity.planetwallet.Common.components.AbsPopupView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;

/**
 * Created by. JcobPark on 2018. 08. 29
 */
public abstract class AbsFadeInView implements PopupView, View.OnClickListener {

    private Context context;
    private View contentView;
    private View background;

    private ArrayList< View > dismissViews;

    public AbsFadeInView( Context context ) {
        this.context = context;
        ( ( PlanetWalletActivity ) context ).addPopup( this );
        viewInit( );
    }

    public Context getContext( ) {
        return context;
    }

    public void setContext( Context context ) {
        this.context = context;
    }

    private void viewInit( ) {

        background = new View( context );
        contentView = this.contentView( );

        if ( contentView != null ) {

            {
                background.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
                background.setBackgroundColor( Color.parseColor( "#80000000" ) );
                background.setAlpha( 0.0f );
                background.setOnClickListener( this );
                ( ( ViewGroup ) ( ( Activity ) context ).findViewById( android.R.id.content ) ).addView( background );

            }

            contentView.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
            contentView.setAlpha( 0.0f );
            contentView.setOnClickListener( this );
            ( ( ViewGroup ) ( ( Activity ) context ).findViewById( android.R.id.content ) ).addView( contentView );

        }


    }

    public View getBackground( ) {
        return background;
    }

    public void setBackground( View background ) {
        this.background = background;
    }

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

    public void show( long duration, Interpolator interpolator ) {

        if ( contentView != null ) {
            contentView.animate( ).alpha( 1.0f ).setDuration( duration ).setListener( new Animator.AnimatorListener( ) {
                @Override
                public void onAnimationStart( Animator animation ) {
                    AbsFadeInView.this.preAnimation( );
                }

                @Override
                public void onAnimationEnd( Animator animation ) {
                    AbsFadeInView.this.setData( );
                }

                @Override
                public void onAnimationCancel( Animator animation ) {

                }

                @Override
                public void onAnimationRepeat( Animator animation ) {

                }
            } ).setInterpolator( interpolator ).start( );
            background.animate( ).alpha( 1.0f ).setDuration( duration ).setInterpolator( interpolator ).start( );
        }

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
            ObjectAnimator animator = ObjectAnimator.ofFloat( contentView, "alpha", 1.0f, 0.0f );
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

    @Override
    public void onBackPressed( ) {
        dismiss( );
    }

    @Override
    public void onClick( View v ) {
        if ( dismissViews != null ) {
            if ( dismissViews.contains( v ) )
                this.dismiss( );
        }
        if ( v == background ) {
            this.dismiss( );
        }
    }
}
