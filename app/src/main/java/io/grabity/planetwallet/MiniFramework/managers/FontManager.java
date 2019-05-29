package io.grabity.planetwallet.MiniFramework.managers;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by. JcobPark on 2018. 08. 29
 */
public class FontManager {

    private static FontManager fontManager;
    public static final int NORMAL = Typeface.NORMAL;
    public static final int BOLD = Typeface.BOLD;
    public static final int MEDIUM = 5;
    public static final int SEMIBOLD = 6;
    private HashMap< Integer, Typeface > fonts;

    private Context context;

    public FontManager( Context context ) {
        this.context = context;
        fonts = new HashMap<>( );
        fonts.put( NORMAL, Typeface.createFromAsset( context.getAssets( ), "fonts/WorkSans-Regular.otf" ) );
        fonts.put( BOLD, Typeface.createFromAsset( context.getAssets( ), "fonts/WorkSans-Bold.otf" ) );
        fonts.put( MEDIUM, Typeface.createFromAsset( context.getAssets( ), "fonts/WorkSans-Medium.otf" ) );
        fonts.put( SEMIBOLD, Typeface.createFromAsset( context.getAssets( ), "fonts/WorkSans-SemiBold.otf" ) );

    }

    public static void Init( Context context ) {
        if ( fontManager == null )
            fontManager = new FontManager( context );
    }

    public static FontManager getInstance( ) {
        return fontManager;
    }


    public Typeface getFont( int fontStyle ) {
        return fonts.get( fontStyle );
    }

    public Typeface getFont( ) {
        return fonts.get( NORMAL );
    }

    public Typeface getBoldFont( ) {
        return fonts.get( BOLD );
    }

    public Typeface getFont( String font ) {
        return Typeface.createFromAsset( context.getAssets( ), "fonts/" + font + ".ttf" );
    }

    public Typeface getFont( String font, boolean nottf ) {
        return Typeface.createFromAsset( context.getAssets( ), "fonts/" + font );
    }

}
