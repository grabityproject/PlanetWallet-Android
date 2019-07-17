package io.grabity.planetwallet.MiniFramework.managers;

import android.app.Activity;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;


/**
 * Created by. JcobPark on 2018. 08. 29
 */
public class KeyboardManager {

    private final static int KEYBOARD_VISIBLE_THRESHOLD_DP = 250;

    private static float keyboardHeight = 0;

    private static float initialHeight = 0;

    private static float oldHeight = 0;


    /**
     * Set keyboard visiblity change event listener.
     *
     * @param activity Activity
     * @param listener KeyboardVisibilityEventListener
     */
    public static void setEventListener(
            @NonNull final Activity activity,
            @NonNull final KeyboardVisibilityEventListener listener ) {

        final View activityRoot = getActivityRoot( activity );

        if ( activityRoot != null ) {
            activityRoot.getViewTreeObserver( ).addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener( ) {

                        private final Rect r = new Rect( );

                        private final int visibleThreshold = Math.round( KEYBOARD_VISIBLE_THRESHOLD_DP );

                        private boolean wasOpened = false;

                        @Override
                        public void onGlobalLayout( ) {
                            activityRoot.getWindowVisibleDisplayFrame( r );

                            int heightDiff = activityRoot.getRootView( ).getHeight( ) - r.height( );

                            if ( initialHeight == 0 ) {
                                initialHeight = heightDiff;
                                oldHeight = initialHeight;
                            }

                            boolean isOpen = heightDiff > visibleThreshold;

                            if ( isOpen == wasOpened ) {
                                // keyboard state has not changed
                                return;
                            }

                            wasOpened = isOpen;

                            if ( keyboardHeight == 0 && oldHeight != 0 ) {
                                listener.onVisibilityChanged( isOpen, oldHeight, heightDiff );
                            } else {
                                listener.onVisibilityChanged( isOpen, keyboardHeight, heightDiff );
                            }
                            keyboardHeight = heightDiff;

                        }
                    } );
        }
    }

    /**
     * Determine if keyboard is visible
     *
     * @param activity Activity
     * @return Whether keyboard is visible or not
     */
    public static boolean isKeyboardVisible( Activity activity ) {
        Rect r = new Rect( );

        View activityRoot = getActivityRoot( activity );
        int visibleThreshold = Math
                .round( KEYBOARD_VISIBLE_THRESHOLD_DP );

        activityRoot.getWindowVisibleDisplayFrame( r );

        int heightDiff = activityRoot.getRootView( ).getHeight( ) - r.height( );

        return heightDiff > visibleThreshold;
    }

    public static float keyboardHeight( Activity activity ) {
        return keyboardHeight;
    }

    private static View getActivityRoot( Activity activity ) {
        return ( ( ViewGroup ) activity.findViewById( android.R.id.content ) ).getChildAt( 0 );
    }

    public interface KeyboardVisibilityEventListener {

        void onVisibilityChanged( boolean isOpen, float oldHeight, float keyboardHeight );

    }
}


