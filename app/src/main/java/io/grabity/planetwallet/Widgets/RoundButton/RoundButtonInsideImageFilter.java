package io.grabity.planetwallet.Widgets.RoundButton;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class RoundButtonInsideImageFilter implements ButtonAction.OnButtonActionListener {

    private RoundButton roundButton;
    private ImageView imageView;

    public static RoundButtonInsideImageFilter set( RoundButton roundButton, ImageView imageView ) {
        return new RoundButtonInsideImageFilter( roundButton, imageView );
    }

    private RoundButtonInsideImageFilter( RoundButton roundButton, ImageView imageView ) {
        this.roundButton = roundButton;
        this.imageView = imageView;
        ButtonAction.newInstance( roundButton ).setOnButtonActionListener( this );
    }

    @Override
    public void onAction( View view, int action ) {
        if ( action == ButtonAction.ACTION_HIGHLIGHT ) {
            imageView.getDrawable( ).setColorFilter( roundButton.getTextColorHighlight( ), PorterDuff.Mode.SRC_ATOP );
            roundButton.setState( MotionEvent.ACTION_DOWN );
            roundButton.setSuperBackgroundColor( Color.TRANSPARENT );
            roundButton.setSuperTextColor( roundButton.getTextColorHighlight( ) );
            if ( roundButton.getTextColorHighlight( ) == roundButton.getTextColorNormal( ) ) {
                roundButton.invalidate( );
            }

        } else {
            imageView.getDrawable( ).clearColorFilter( );
            roundButton.setState( MotionEvent.ACTION_UP );
            roundButton.setSuperBackgroundColor( Color.TRANSPARENT );
            roundButton.setSuperTextColor( roundButton.getTextColorNormal( ) );
            if ( roundButton.getTextColorHighlight( ) == roundButton.getTextColorNormal( ) ) {
                roundButton.invalidate( );
            }

        }
    }
}
