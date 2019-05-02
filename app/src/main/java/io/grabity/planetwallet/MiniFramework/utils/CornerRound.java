package io.grabity.planetwallet.MiniFramework.utils;

import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.View;

/**
 * Created by. JcobPark on 2018. 08. 29
 */
public class CornerRound {

    public static void radius( final View view, float... outerR ) {

        RectF inset = new RectF( 0, 0, 0, 0 );
        float[] innerR = new float[]{ 0, 0, 0, 0, 0, 0, 0, 0 };

        float outerMax = 0.0f;
        for ( float outer : outerR ) {
            outerMax = outerMax < outer ? outer : outerMax;
        }

        ShapeDrawable drawable = new ShapeDrawable( new RoundRectShape( outerR, inset, innerR ) );
        drawable.setIntrinsicWidth( ( int ) ( outerMax * 2f ) );
        drawable.setIntrinsicHeight( ( int ) ( outerMax * 2f ) );
        Drawable background = view.getBackground( );

        if ( background != null ) {

            if ( background.getClass( ).getSimpleName( ).equals( ColorDrawable.class.getSimpleName( ) ) ) {

                ColorDrawable colorDrawable = ( ColorDrawable ) view.getBackground( );
                drawable.getPaint( ).setColor( colorDrawable.getColor( ) );
                view.setBackground( drawable );

            } else if ( background.getClass( ).getSimpleName( ).equals( BitmapDrawable.class.getSimpleName( ) ) ) {

                BitmapDrawable bitmapDrawable = ( BitmapDrawable ) view.getBackground( );
                BitmapShader mBitmapShader = new BitmapShader( bitmapDrawable.getBitmap( ), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP );
                Paint paint = drawable.getPaint( );
                paint.setAntiAlias( true );
                paint.setShader( mBitmapShader );
                view.setBackground( drawable );

            } else if ( background.getClass( ).getSimpleName( ).equals( GradientDrawable.class.getSimpleName( ) ) ) {

                GradientDrawable gradientDrawable = ( GradientDrawable ) view.getBackground( );
                gradientDrawable.setCornerRadii( outerR );
                view.setBackground( gradientDrawable );

            }
        }

    }

}
