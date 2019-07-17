package io.grabity.planetwallet.Widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class RippleEffectView extends View implements Themeable {

    private View trigger;

    private int rippleColor = Color.WHITE;
    private Paint ripplePaint;

    private float percent = 0.0f;

    private float centerX = 0.0f;
    private float centerY = 0.0f;

    private int[] triggerLocation = new int[ 2 ];
    private int[] thisLocation = new int[ 2 ];

    private boolean isSetLocation = false;

    private float width = 0.0f;
    private float height = 0.0f;

    private OnRippleEffectListener onRippleEffectListener;

    private boolean isRippleOn = false;
    private boolean isRippleAnimationStatus = true;

    public boolean isRippleAnimationStatus( ) {
        return isRippleAnimationStatus;
    }

    public void setRippleAnimationStatus( boolean rippleAnimationStatus ) {
        isRippleAnimationStatus = rippleAnimationStatus;
    }

    public boolean isRippleOn( ) {
        return isRippleOn;
    }

    public void setRippleOn( boolean rippleOn ) {
        isRippleOn = rippleOn;
    }

    public RippleEffectView( Context context ) {
        super( context );
        viewInit( );
    }

    public RippleEffectView( Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
        viewInit( );
    }

    void viewInit( ) {
        ripplePaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        ripplePaint.setColor( rippleColor );
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        width = w;
        height = h;
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw( canvas );
        if ( trigger != null && !isSetLocation && trigger.getWidth( ) != 0 && trigger.getHeight( ) != 0 ) {
            getLocationOnScreen( thisLocation );
            trigger.getLocationOnScreen( triggerLocation );
            centerX = triggerLocation[ 0 ] - thisLocation[ 0 ] + trigger.getWidth( ) / 2.0f;
            centerY = triggerLocation[ 1 ] - thisLocation[ 1 ] + trigger.getHeight( ) / 2.0f;
            isSetLocation = true;
        }

        ripplePaint.setColor( rippleColor == Color.WHITE ? Color.argb( ( int ) ( 255.0f * percent / 100.0f ), 255, 255, 255 )
                : Color.argb( ( int ) ( 255.0f * percent / 100.0f ), 0, 0, 0 ) );
        canvas.drawCircle( centerX, centerY, ( float ) Math.sqrt( Math.pow( width, 2 ) + Math.pow( height, 2 ) ) * percent / 100.0f, ripplePaint );


    }

    @Override
    public void setTheme( boolean theme ) {
        if ( theme ) {
            rippleColor = Color.BLACK;
        } else {
            rippleColor = Color.WHITE;
        }
        ripplePaint.setColor( rippleColor );
        invalidate( );
    }

    public void ripple( boolean on ) {
        isRippleOn = on;
        ObjectAnimator animator = ObjectAnimator.ofFloat( this, "percent", on ? 100.0f : 0.0f ).setDuration( 400 );
        animator.setInterpolator( new LinearInterpolator( ) );
        animator.addListener( new Animator.AnimatorListener( ) {
            @Override
            public void onAnimationStart( Animator animation ) {

            }

            @Override
            public void onAnimationEnd( Animator animation ) {
                if ( onRippleEffectListener != null ) {
                    onRippleEffectListener.onRippleEffect( isRippleOn );
                }
            }

            @Override
            public void onAnimationCancel( Animator animation ) {

            }

            @Override
            public void onAnimationRepeat( Animator animation ) {

            }
        } );
        animator.start( );
    }

    public float getPercent( ) {
        return percent;
    }

    public void setPercent( float percent ) {
        this.percent = percent;
        if ( this.percent > 0 ){
            isRippleAnimationStatus = false;
        } else if ( this.percent == 0 ) {
            isRippleAnimationStatus = true;
        }
        invalidate( );
    }

    public View getTrigger( ) {
        return trigger;
    }

    public void setTrigger( View trigger ) {
        this.trigger = trigger;
        this.isSetLocation = false;
    }

    public OnRippleEffectListener getOnRippleEffectListener( ) {
        return onRippleEffectListener;
    }

    public void setOnRippleEffectListener( OnRippleEffectListener onRippleEffectListener ) {
        this.onRippleEffectListener = onRippleEffectListener;
    }

    public interface OnRippleEffectListener {
        void onRippleEffect( boolean on );
    }
}
