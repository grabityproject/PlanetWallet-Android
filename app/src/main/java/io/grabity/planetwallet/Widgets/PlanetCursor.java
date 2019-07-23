package io.grabity.planetwallet.Widgets;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class PlanetCursor extends View {

    int cursorRenderTime;
    Paint paint;

    public PlanetCursor( Context context ) {
        super( context );
    }

    public PlanetCursor( Context context, @Nullable AttributeSet attrs ) {
        this( context, attrs, 0 );

    }

    public PlanetCursor( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        viewInit( );
    }

    void viewInit( ) {
        paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        paint.setStyle( Paint.Style.FILL );
        show( );
    }

    public int getCursorRenderTime( ) {
        return cursorRenderTime;
    }

    public void setCursorRenderTime( int cursorRenderTime ) {
        this.cursorRenderTime = cursorRenderTime;
        this.invalidate( );
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw( canvas );
        if ( cursorRenderTime > 50 ) {
            paint.setColor( Color.parseColor( "#FF0050" ) );
        } else {
            paint.setColor( Color.TRANSPARENT );
        }
        canvas.drawRect( new RectF( 0, 0, canvas.getWidth( ), canvas.getHeight( ) ), paint );
    }

    private void show( ) {
        try{
            ObjectAnimator animator = ObjectAnimator.ofInt( this, "cursorRenderTime", 0, 100 );
            animator.setDuration( 1000 );
            animator.setRepeatMode( ValueAnimator.RESTART );
            animator.setRepeatCount( ValueAnimator.INFINITE );
            animator.start( );
        }catch ( ClassCastException e ){

        }

    }


}
