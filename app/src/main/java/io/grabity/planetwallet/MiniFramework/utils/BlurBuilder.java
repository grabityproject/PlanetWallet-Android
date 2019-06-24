package io.grabity.planetwallet.MiniFramework.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class BlurBuilder {
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    public static Bitmap blur( Context context, Bitmap image ) {
        int width = Math.round( image.getWidth( ) * BITMAP_SCALE );
        int height = Math.round( image.getHeight( ) * BITMAP_SCALE );

        Bitmap inputBitmap = Bitmap.createScaledBitmap( image, width, height, false );
        Bitmap outputBitmap = Bitmap.createBitmap( inputBitmap );

        RenderScript rs = RenderScript.create( context );
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
        Allocation tmpIn = Allocation.createFromBitmap( rs, inputBitmap );
        Allocation tmpOut = Allocation.createFromBitmap( rs, outputBitmap );
        theIntrinsic.setRadius( BLUR_RADIUS );
        theIntrinsic.setInput( tmpIn );
        theIntrinsic.forEach( tmpOut );
        tmpOut.copyTo( outputBitmap );

        return outputBitmap;
    }

    public static Bitmap blur( Context context, Bitmap image, float scale, float radius ) throws IllegalArgumentException {
        int width = Math.round( image.getWidth( ) * scale );
        int height = Math.round( image.getHeight( ) * scale );

        Bitmap inputBitmap = Bitmap.createScaledBitmap( image, width, height, false );
        Bitmap outputBitmap = Bitmap.createBitmap( inputBitmap );

        RenderScript rs = RenderScript.create( context );
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
        Allocation tmpIn = Allocation.createFromBitmap( rs, inputBitmap );
        Allocation tmpOut = Allocation.createFromBitmap( rs, outputBitmap );
        theIntrinsic.setRadius( radius );
        theIntrinsic.setInput( tmpIn );
        theIntrinsic.forEach( tmpOut );
        tmpOut.copyTo( outputBitmap );

        return outputBitmap;
    }

    public static Bitmap blur( Context context, Bitmap image, float radius ) {
        Bitmap outputBitmap = Bitmap.createBitmap( image );

        RenderScript rs = RenderScript.create( context );
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
        Allocation tmpIn = Allocation.createFromBitmap( rs, image );
        Allocation tmpOut = Allocation.createFromBitmap( rs, outputBitmap );
        theIntrinsic.setRadius( radius );
        theIntrinsic.setInput( tmpIn );
        theIntrinsic.forEach( tmpOut );
        tmpOut.copyTo( outputBitmap );

        return outputBitmap;
    }
}
