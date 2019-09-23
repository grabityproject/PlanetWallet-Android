package io.grabity.planetwallet.Views.p1_Splash.Activity;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.fcm.OnMessagingListener;
import io.grabity.planetwallet.MiniFramework.managers.SyncManager;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Device;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Version;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeRegistrationActivity;


public class SplashActivity extends PlanetWalletActivity implements Animator.AnimatorListener, OnMessagingListener, SyncManager.OnSyncListener {

    private ViewMapper viewMapper;

    private boolean isSync = false;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        getPlanetWalletApplication( ).setPINCODE( new char[]{ } );
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }


    @Override
    protected void setData( ) {
        super.setData( );
        getPlanetWalletApplication( ).addOnMessagingListener( this );

        new Get( this ).action( Route.URL( "version", "android" ), 0, 1, null );

    }


    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.loadingView.addAnimatorListener( this );
        viewMapper.loadingView.setAnimation( !getCurrentTheme( ) ? "lottie/splash_black.json" : "lottie/splash_white.json" );
        viewMapper.loadingView.setRepeatCount( 0 );
        viewMapper.loadingView.playAnimation( );
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( !error ) {
            if ( resultCode == 1 && statusCode == 200 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Version.class );
                if ( returnVO.isSuccess( ) ) {
                    Version v = ( Version ) returnVO.getResult( );
                    try {
                        Double localVersion = Double.parseDouble( Utils.getAppVersion( this ) );
                        Double recentVersion = Double.parseDouble( v.getVersion( ) );

                        getPlanetWalletApplication( ).setRecentVersion( v.getVersion( ) );
                        getPlanetWalletApplication( ).setPlayStoreUrl( v.getUrl( ) );

                        if ( localVersion < recentVersion && Utils.equals( v.getForce_update( ), "Y" ) ) {
                            return;
                        }

                    } catch ( NumberFormatException e ) {
                        e.printStackTrace( );
                    }
                }
            }
        }

        SyncManager.getInstance( ).syncPlanet( this );
        FirebaseInstanceId.getInstance( ).getInstanceId( )
                .addOnCompleteListener( task -> {
                    if ( !task.isSuccessful( ) ) {
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult( ).getToken( );
                    if ( Utils.getPreferenceData( SplashActivity.this, C.pref.FCM_TOKEN, "" ).equals( "" ) ) {
                        Utils.setPreferenceData( SplashActivity.this, C.pref.FCM_TOKEN, token );
                        new Post( SplashActivity.this::onReceive ).action( Route.URL( "device", "android" ), 0, 0, new Device( token ) );
                    } else {
                        if ( !Utils.getPreferenceData( SplashActivity.this, C.pref.FCM_TOKEN, "" ).equals( token ) ) {
                            Utils.setPreferenceData( SplashActivity.this, C.pref.FCM_TOKEN, token );
                            new Post( SplashActivity.this::onReceive ).action( Route.URL( "device", "android" ), 0, 0, new Device( token ) );
                        }
                    }

                    if ( KeyValueStore.getInstance( ).getValue( C.pref.DEVICE_KEY ) == null ) {

                        new Post( SplashActivity.this::onReceive ).action( Route.URL( "device", "android" ), 0, 0, new Device( token ) );

                    } else {

                        getPlanetWalletApplication( ).setDeviceKey( KeyValueStore.getInstance( ).getValue( C.pref.DEVICE_KEY ) );


                    }

                } );

    }


    @Override
    protected void onResume( ) {
        super.onResume( );
    }

    public void sendActionSwitch( ) {
        if ( getPlanetWalletApplication( ).getDeviceKey( ) == null || !isSync ) {

            new Handler( ).postDelayed( this::sendActionSwitch, 500 );

        } else {

            if ( KeyValueStore.getInstance( ).getValue( C.pref.PASSWORD ) == null ) {
                sendAction( PinCodeRegistrationActivity.class );
                finish( );
            } else {
                sendAction( PinCodeCertificationActivity.class );
                finish( );
            }
        }

    }

    @Override
    public void onAnimationStart( Animator animation ) {
    }

    @Override
    public void onAnimationEnd( Animator animation ) {
        sendActionSwitch( );
    }

    @Override
    public void onAnimationCancel( Animator animation ) {
    }

    @Override
    public void onAnimationRepeat( Animator animation ) {
    }

    @Override
    public void onMessage( RemoteMessage remoteMessage ) {
        KeyValueStore.getInstance( ).setValue( C.pref.DEVICE_KEY, remoteMessage.getData( ).get( "device_key" ) );
        getPlanetWalletApplication( ).setDeviceKey( KeyValueStore.getInstance( ).getValue( C.pref.DEVICE_KEY ) );
    }

    @Override
    public void onSyncComplete( SyncManager.SyncType syncType, boolean complete, boolean isUpdated ) {
        isSync = true;
    }

    public class ViewMapper {

        LottieAnimationView loadingView;

        public ViewMapper( ) {
            loadingView = findViewById( R.id.lottie_splash );
        }

    }


}