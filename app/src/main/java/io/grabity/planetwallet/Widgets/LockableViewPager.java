package io.grabity.planetwallet.Widgets;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class LockableViewPager extends ViewPager {

    private boolean enabled;

    public LockableViewPager( Context context ) {
        super( context );
        this.enabled = true;
    }

    public LockableViewPager( Context context, AttributeSet attrs ) {
        super( context, attrs );
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent( MotionEvent event ) {
        if ( this.enabled ) return super.onTouchEvent( event );
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent( MotionEvent event ) {
        if ( this.enabled ) return super.onInterceptTouchEvent( event );
        return false;
    }

    public void setLockPager( boolean lock ) {
        this.enabled = !lock;
    }

}
