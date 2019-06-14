package io.grabity.planetwallet.Common.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.MiniFramework.managers.FontManager;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.SetTokenImageDownloader;
import io.grabity.planetwallet.MiniFramework.utils.Utils;


/**
 * Created by. JcobPark on 2018. 08. 29
 */
@SuppressLint( "Registered" )
public class PlanetWalletApplication extends MultiDexApplication {

    private boolean theme = false;


    @Override
    public void onCreate( ) {
        super.onCreate( );
        initLoader( );
    }

    protected void attachBaseContext( Context base ) {
        super.attachBaseContext( base );
        MultiDex.install( this );
    }

    public void initLoader( ) {

        this.theme = !Utils.getPreferenceData( this, C.pref.THEME, C.theme.DARK ).equals( C.theme.DARK );
        PLog.e( "initLoader theme : " + theme );


        if ( ImageLoader.getInstance( ).isInited( ) ) ImageLoader.getInstance( ).destroy( );
        FontManager.Init( this );
        DisplayImageOptions options = new DisplayImageOptions.Builder( )
                .cacheInMemory( true )
                .cacheOnDisk( true )
                .build( );

        ImageLoaderConfiguration config = new
                ImageLoaderConfiguration.Builder( getApplicationContext( ) )
                .threadPoolSize( 5 )
                .defaultDisplayImageOptions( options )
                .threadPriority( Thread.MIN_PRIORITY + 3 )
                .denyCacheImageMultipleSizesInMemory( )
                .diskCacheFileNameGenerator( new Md5FileNameGenerator( ) )
                .memoryCache( new LruMemoryCache( 2 * 1024 * 1024 ) )
                .memoryCacheSize( 2 * 1024 * 1024 )
                .imageDownloader( new SetTokenImageDownloader( this ) )
                .build( );
        ImageLoader.getInstance( ).init( config );
//        DataBaseManager.init( this );

        Security.addProvider( new org.spongycastle.jce.provider.BouncyCastleProvider( ) );

    }



    public boolean getCurrentTheme( ) {
        return theme;
    }

    public void setTheme( boolean theme ) {
        this.theme = theme;
        Utils.setPreferenceData( this, C.pref.THEME, theme ? C.theme.LIGHT : C.theme.DARK );
    }
}


