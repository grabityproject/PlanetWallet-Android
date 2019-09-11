package io.grabity.planetwallet.Common.components;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pentasecurity.cryptowallet.crypto.HsmKeyCrypter;
import com.pentasecurity.cryptowallet.crypto.PinBasedKeyCrypter;
import com.pentasecurity.cryptowallet.crypto.PinBasedKeyCrypterImpl;
import com.pentasecurity.cryptowallet.storage.DefaultStorageCrypter;

import java.security.Security;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.MiniFramework.fcm.OnMessagingListener;
import io.grabity.planetwallet.MiniFramework.managers.DatabaseManager.PWDBManager;
import io.grabity.planetwallet.MiniFramework.managers.FontManager;
import io.grabity.planetwallet.MiniFramework.utils.SetTokenImageDownloader;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.crypto.KeyStoreCrypter;
import io.grabity.planetwallet.MiniFramework.wallet.managers.BitCoinManager;
import io.grabity.planetwallet.MiniFramework.wallet.managers.EthereumManager;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;


/**
 * Created by. JcobPark on 2018. 08. 29
 */
@SuppressLint( "Registered" )
public class PlanetWalletApplication extends MultiDexApplication {


    private String deviceKey = null;
    private char[] PINCODE = null;
    private ArrayList< OnMessagingListener > messagingListeners = new ArrayList<>( );
    private LinkedList< Activity > activityStack = new LinkedList<>( );

    private Activity topActivity = null;

    static {
        System.loadLibrary( "pallet_core-0.1.0-x64_shared" );
    }

    private String keystoreAlias = "TEST_WALLET";
    private PinBasedKeyCrypter pinBasedKeyCrypter;
    private HsmKeyCrypter hsmKeyCrypter;
    private DefaultStorageCrypter storageCrypter;

    private boolean theme = false;


    @Override
    public void onCreate( ) {
        super.onCreate( );
        initLoader( );
        registerActivityLifecycleCallbacks( new AppLifeCycleTracker( this ) );
    }

    protected void attachBaseContext( Context base ) {
        super.attachBaseContext( base );
        MultiDex.install( this );
    }

    public void initLoader( ) {
        Security.addProvider( new org.spongycastle.jce.provider.BouncyCastleProvider( ) );
        PWDBManager.init( this );
        initModules( );

        this.theme = !Utils.getPreferenceData( this, C.pref.THEME, C.theme.DARK ).equals( C.theme.DARK );
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

    }


    public boolean getCurrentTheme( ) {
        return theme;
    }

    public void setTheme( boolean theme ) {
        this.theme = theme;
        Utils.setPreferenceData( this, C.pref.THEME, theme ? C.theme.LIGHT : C.theme.DARK );
    }


    public void initModules( ) {
        pinBasedKeyCrypter = new PinBasedKeyCrypterImpl( );
        hsmKeyCrypter = new KeyStoreCrypter( keystoreAlias );
        storageCrypter = new DefaultStorageCrypter( keystoreAlias, pinBasedKeyCrypter, hsmKeyCrypter );
        KeyValueStore.init( this, storageCrypter );
        KeyPairStore.init( storageCrypter );
        EthereumManager.init( );
        BitCoinManager.init( );
    }

    public char[] getPINCODE( ) {
        return PINCODE;
    }

    public void setPINCODE( @Nullable char[] PINCODE ) {
        this.PINCODE = PINCODE;
        C.PINCODE = this.PINCODE;
    }


    public void addOnMessagingListener( OnMessagingListener listener ) {
        if ( messagingListeners == null ) messagingListeners = new ArrayList<>( );
        messagingListeners.add( listener );
    }

    public ArrayList< OnMessagingListener > getMessagingListeners( ) {
        return messagingListeners;
    }


    public String getDeviceKey( ) {
        return deviceKey;
    }

    public void setDeviceKey( String deviceKey ) {
        this.deviceKey = deviceKey;
        C.DEVICE_KEY = deviceKey;
    }

    public void recordActvityStack( Activity activity ) {
        if ( activityStack != null ) {
            activityStack.offer( activity );

        }
    }

    public void removeStack( ) {
        if ( activityStack != null ) {
            activityStack.pollLast( );

        }
    }

    public void setTopActivity( Activity activity ) {
        topActivity = activity;
    }

    public Activity getTopActivity( ) {
        return topActivity;
    }

    public void removeAllStack( ) {
        if ( activityStack != null ) {
            while ( activityStack.size( ) > 0 ) {
                Objects.requireNonNull( activityStack.poll( ) ).onBackPressed( );
            }
        }
    }

}


