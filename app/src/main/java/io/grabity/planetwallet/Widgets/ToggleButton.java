package io.grabity.planetwallet.Widgets;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by. JcobPark on 2019. 05. 14
 */
public class ToggleButton extends View implements View.OnClickListener, Themeable {

    private float width;
    private float height;

    private Canvas originalCanvas;
    private Paint shaderPaint;

    private Paint circlePaint;
    private Paint maskPaint;

    private RectF clipRectF;

    private boolean isOn = false;

    private float percent = 0.0f;

    private OnToggleListener onToggleListener;

    private int defaultTheme = 0;

    public ToggleButton( Context context ) {
        super( context );
        viewInit( );
    }

    public ToggleButton( Context context, @Nullable AttributeSet attrs ) {
        super( context, attrs );
        viewInit( );
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        width = w;
        height = h;
        clipRectF = new RectF( 0f, 0f, width, height );
        setPaints( );
    }

    void viewInit( ) {
        circlePaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        maskPaint = new Paint( Paint.ANTI_ALIAS_FLAG );

        circlePaint.setColor( Color.WHITE );
        maskPaint.setColor( Color.BLACK );

        setOnClickListener( this );
    }


    private void setPaints( ) {
        Bitmap original = Bitmap.createBitmap( ( int ) width, ( int ) height, Bitmap.Config.ARGB_8888 );
        originalCanvas = new Canvas( original );
        Shader shader = new BitmapShader( original, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP );
        shaderPaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        shaderPaint.setShader( shader );
    }


    @Override
    protected void onDraw( Canvas canvas ) {
        float gap = height * ( 5.0f / 30.0f );
        float radius = height * ( 10.0f / 30.0f );
        float maskGap = height * ( 13.0f / 30.0f );

        maskPaint.setColor( defaultTheme == 1 ? Color.argb(
                255,
                ( int ) ( 255.0f * percent / 100.0f ),
                ( int ) ( 0.0f * percent / 100.0f ),
                ( int ) ( 80.0f * percent / 100.0f ) ) :
                Color.argb(
                        255,
                        ( int ) ( 92.0f + ( ( 255.0f - 92.0f ) * percent / 100.0f ) ),
                        ( int ) ( 89.0f + ( ( 0.0f - 89.0f ) * percent / 100.0f ) ),
                        ( int ) ( 100.0f + ( ( 80.0f - 100.0f ) * percent / 100.0f ) ) )
        );

        originalCanvas.drawRoundRect( clipRectF, height / 2.0f, height / 2.0f, maskPaint );
        originalCanvas.drawCircle( gap + radius + ( percent / 100.0f ) * ( width - 2 * ( gap + radius ) ), height / 2.0f, radius, circlePaint );
        originalCanvas.drawCircle( maskGap + radius + ( percent / 100.0f ) * ( width - maskGap ), height / 2.0f, radius, maskPaint );

        canvas.drawRoundRect( clipRectF, height / 2.0f, height / 2.0f, shaderPaint );
    }


    @Override
    public void onClick( View v ) {
        this.isOn = !this.isOn;
        if ( onToggleListener != null ) onToggleListener.onToggle( this, this.isOn );
        if ( this.isOn ) {
            ObjectAnimator animator = ObjectAnimator.ofFloat( this, "percent", 100.0f );
            animator.setDuration( 300 );
            animator.start( );
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat( this, "percent", 0.0f );
            animator.setDuration( 300 );
            animator.start( );
        }
    }

    public boolean isOn( ) {
        return isOn;
    }

    public void setOn( boolean on, boolean animate ) {
        isOn = on;
        if ( animate ) {
            ObjectAnimator animator = ObjectAnimator.ofFloat( this, "percent", on ? 100.0f : 0.0f );
            animator.setDuration( 300 );
            animator.start( );
        } else {
            percent = on ? 100.0f : 0.0f;
            invalidate( );
        }
    }

    public void setOn( boolean on ) {
        setOn( on, false );
    }

    public float getPercent( ) {
        return percent;
    }

    public void setPercent( float percent ) {
        this.percent = percent;
        invalidate( );
    }

    public OnToggleListener getOnToggleListener( ) {
        return onToggleListener;
    }

    public void setOnToggleListener( OnToggleListener onToggleListener ) {
        this.onToggleListener = onToggleListener;
    }

    public interface OnToggleListener {
        void onToggle( ToggleButton toggleButton, boolean isOn );
    }

    @Override
    public void setTheme( boolean theme ) {
        if ( !theme ) {
            defaultTheme = 1;
        } else {
            defaultTheme = 2;
        }

    }
}
