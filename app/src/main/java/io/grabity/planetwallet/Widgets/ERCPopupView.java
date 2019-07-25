package io.grabity.planetwallet.Widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import io.grabity.planetwallet.MiniFramework.utils.Utils;

public class ERCPopupView extends View implements View.OnTouchListener {

    private ObjectAnimator animator;
    private View view;
    private View standardView;
    private int[] thisLocations = new int[ 2 ];

    private boolean isOpen = false;
    private boolean settingY = false;

    private float beforTouchY = 0;

    public ERCPopupView( Context context ) {
        super( context );
        this.viewInit( );
    }

    public ERCPopupView( Context context, View view ) {
        super( context );
        this.view = view;
        this.viewInit( );
    }

    void viewInit( ) {
        ( ( ViewGroup ) ( ( Activity ) this.getContext( ) ).findViewById( android.R.id.content ) ).addView( view );
        view.setY( Utils.getScrennHeight( this.getContext( ) ) / 1.0f );
        view.setOnTouchListener( this );

        findViewAndSetTheme( view, false );
    }

    public void standardView( View v ) {
        this.standardView = v;
    }

    public void show( ) {
        animator = ObjectAnimator.ofFloat( view, "y", Utils.getScrennHeight( this.getContext( ) ) / 1.0f, 0.0f );
        animator.setDuration( 200 );
        animator.setInterpolator( new AccelerateDecelerateInterpolator( ) );
        animator.start( );
        animator.addListener( new Animator.AnimatorListener( ) {
            @Override
            public void onAnimationStart( Animator animation ) {

            }

            @Override
            public void onAnimationEnd( Animator animation ) {
                isOpen = true;
            }

            @Override
            public void onAnimationCancel( Animator animation ) {

            }

            @Override
            public void onAnimationRepeat( Animator animation ) {

            }
        } );
    }

    public void dismiss( ) {
        animator = ObjectAnimator.ofFloat( view, "y", 0.0f, Utils.getScrennHeight( this.getContext( ) ) / 1.0f );
        animator.setDuration( 200 );
        animator.setInterpolator( new AccelerateDecelerateInterpolator( ) );
        animator.start( );
        animator.addListener( new Animator.AnimatorListener( ) {
            @Override
            public void onAnimationStart( Animator animation ) {

            }

            @Override
            public void onAnimationEnd( Animator animation ) {

            }

            @Override
            public void onAnimationCancel( Animator animation ) {
                isOpen = false;
            }

            @Override
            public void onAnimationRepeat( Animator animation ) {

            }
        } );

    }

    public boolean isOpen( ) {
        return isOpen;
    }


    protected void findViewAndSetTheme( final View v, boolean theme ) {
        try {
            if ( v instanceof ViewGroup ) {
                ViewGroup vg = ( ViewGroup ) v;
                if ( v instanceof Themeable ) {
                    ( ( Themeable ) v ).setTheme( theme );
                }
                for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                    View child = vg.getChildAt( i );
                    findViewAndSetTheme( child, theme );
                }
            } else if ( v instanceof Themeable ) {
                ( ( Themeable ) v ).setTheme( theme );
            }

        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }

    @Override
    public boolean onTouch( View v, MotionEvent event ) {
        //Todo 일단보류
//        if ( event.getAction( ) == MotionEvent.ACTION_DOWN ) {
//            if ( !settingY ) {
//                beforTouchY = event.getRawY( );
//                settingY = true;
//            }
//        } else if ( event.getAction( ) == MotionEvent.ACTION_MOVE ) {
//
//            float moveY = ( ( view.getHeight( ) - standardView.getHeight( ) ) + event.getRawY( ) ) - beforTouchY;
//            if ( view.getHeight( ) - standardView.getHeight( ) <= moveY ) {
//                standardView.setY( moveY );
//            }
//
//        } else if ( event.getAction( ) == MotionEvent.ACTION_UP || event.getAction( ) == MotionEvent.ACTION_CANCEL ) {
//            beforTouchY = 0.0f;
//            settingY = false;
//
//            this.dismiss( );
//        }


        return true;
    }
}
