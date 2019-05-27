package io.grabity.planetwallet.MiniFramework.managers;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by. JcobPark on 2018. 08. 29
 */
public class FontManager {

    private static FontManager fontManager;
    Typeface defFont;
    Typeface boldFont;
    private Context context;

    public FontManager( Context context ) {
        this.context = context;
//        this.defFont = Typeface.create( Typeface.SANS_SERIF, Typeface.NORMAL );
//        this.boldFont = Typeface.create( Typeface.SANS_SERIF, Typeface.BOLD );
        this.defFont = Typeface.create( Typeface.createFromAsset( context.getAssets() , "fonts/WorkSans-Regular.otf" ), Typeface.NORMAL );
        this.boldFont = Typeface.create( Typeface.createFromAsset( context.getAssets() , "fonts/WorkSans-Bold.otf" ), Typeface.BOLD );


    }

    public static void Init( Context context ) {
        if ( fontManager == null )
            fontManager = new FontManager( context );
    }

    public static FontManager getInstance( ) {
        return fontManager;
    }

    public Typeface getFont( ) {
        return defFont;
    }

    public Typeface getBoldFont( ) {
        return boldFont;
    }

    public Typeface getFont( String font ) {
        return Typeface.createFromAsset( context.getAssets( ), "fonts/" + font + ".ttf" );
    }

    public Typeface getFont( String font, boolean nottf ) {
        return Typeface.createFromAsset( context.getAssets( ), "fonts/" + font );
    }

}
