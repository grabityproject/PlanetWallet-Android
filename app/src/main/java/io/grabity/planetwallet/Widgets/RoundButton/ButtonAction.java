package io.grabity.planetwallet.Widgets.RoundButton;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class ButtonAction implements View.OnTouchListener, ViewTreeObserver.OnWindowFocusChangeListener {

    public static final int ACTION_HIGHLIGHT = 0;
    public static final int ACTION_NORMAL = 1;
    public static final int ACTION_FOCUS_OUT = -1;


    private View view;
    private Rect rect;
    private boolean inside = false;
    private OnButtonActionListener onButtonActionListener;

    public static ButtonAction newInstance( View view ) {
        return new ButtonAction( view );
    }

    private ButtonAction( final View button ) {
        view = button;
        view.setOnTouchListener( this );
        view.getViewTreeObserver( ).addOnWindowFocusChangeListener( this );
    }

    @Override
    public boolean onTouch( View v, MotionEvent event ) {

        if ( event.getAction( ) == MotionEvent.ACTION_DOWN ) {
            inside = true;
            rect = new Rect( v.getLeft( ), v.getTop( ), v.getRight( ), v.getBottom( ) );

            if ( onButtonActionListener != null ) {
                onButtonActionListener.onAction( v, ACTION_HIGHLIGHT );
            }

        } else if ( event.getAction( ) == MotionEvent.ACTION_MOVE ) {
            if ( !rect.contains( v.getLeft( ) + ( int ) event.getX( ), v.getTop( ) + ( int ) event.getY( ) ) ) {
                if ( inside ) {
                    inside = false;
                    if ( onButtonActionListener != null ) {
                        onButtonActionListener.onAction( v, ACTION_NORMAL );
                    }
                }
            } else {
                if ( !inside ) {
                    inside = true;
                    if ( onButtonActionListener != null ) {
                        onButtonActionListener.onAction( v, ACTION_HIGHLIGHT );
                    }
                }
            }

        } else if ( event.getAction( ) == MotionEvent.ACTION_UP ) {
            if ( onButtonActionListener != null ) {
                onButtonActionListener.onAction( v, ACTION_NORMAL );
            }
        }

        return false;
    }

    public void setOnButtonActionListener( OnButtonActionListener onButtonActionListener ) {
        this.onButtonActionListener = onButtonActionListener;
    }

    @Override
    public void onWindowFocusChanged( boolean hasFocus ) {
        if ( !hasFocus ) {
            if ( onButtonActionListener != null ) {
                onButtonActionListener.onAction( view, ACTION_FOCUS_OUT );
            }
        }
    }

    public interface OnButtonActionListener {
        void onAction( View view, int action );
    }

}
