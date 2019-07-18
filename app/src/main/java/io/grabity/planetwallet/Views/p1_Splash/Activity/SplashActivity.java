package io.grabity.planetwallet.Views.p1_Splash.Activity;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.fcm.OnMessagingListener;
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Device;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeRegistrationActivity;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;


public class SplashActivity extends PlanetWalletActivity implements Animator.AnimatorListener, OnMessagingListener {

    private ViewMapper viewMapper;

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
//        PLog.e( result );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        getPlanetWalletApplication().addOnMessagingListener( this );
        FirebaseInstanceId.getInstance( ).getInstanceId( )
                .addOnCompleteListener( new OnCompleteListener< InstanceIdResult >( ) {
                    @Override
                    public void onComplete( @NonNull Task< InstanceIdResult > task ) {
                        if ( !task.isSuccessful( ) ) {
                            PLog.e( "getInstanceId failed", task.getException( ) );
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult( ).getToken( );
                        PLog.e( "token : " + token );
                        if( Utils.getPreferenceData( SplashActivity.this, C.pref.FCM_TOKEN, "" ).equals( "" ) ){
                            Utils.setPreferenceData( SplashActivity.this, C.pref.FCM_TOKEN, token );
                            new Post( SplashActivity.this::onReceive ).action( Route.URL( "device", "android" ), 0, 0, new Device( token ) );
                        }else{
                            if( !Utils.getPreferenceData( SplashActivity.this, C.pref.FCM_TOKEN, "" ).equals( token ) ){
                                Utils.setPreferenceData( SplashActivity.this, C.pref.FCM_TOKEN, token );
                                new Post( SplashActivity.this::onReceive ).action( Route.URL( "device", "android" ), 0, 0, new Device( token ) );
                            }
                        }

                        if( KeyValueStore.getInstance().getValue( C.pref.DEVICE_KEY ) == null ){

                            new Post( SplashActivity.this::onReceive ).action( Route.URL( "device", "android" ), 0, 0, new Device( token ) );

                        }else{

                            getPlanetWalletApplication().setDeviceKey( KeyValueStore.getInstance().getValue( C.pref.DEVICE_KEY ) );

                        }


                    }
                } );
    }


    @Override
    protected void onResume( ) {
        super.onResume( );
    }

    public void sendActionSwitch( ) {

        if( getPlanetWalletApplication().getDeviceKey() == null ){

            new Handler( ).postDelayed( this::sendActionSwitch, 500 );

        }else{
            if ( KeyValueStore.getInstance( ).getValue( C.pref.PASSWORD ) == null ) {
                new Handler( ).postDelayed( ( ) -> {
                    sendAction( PinCodeRegistrationActivity.class );
                    finish( );
                }, 500 );
            } else {
                new Handler( ).postDelayed( ( ) -> {
                    sendAction( PinCodeCertificationActivity.class );
                    finish( );
                }, 500 );
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
        KeyValueStore.getInstance().setValue( C.pref.DEVICE_KEY,  remoteMessage.getData().get( "device_key" ) );
        getPlanetWalletApplication().setDeviceKey( KeyValueStore.getInstance().getValue( C.pref.DEVICE_KEY ) );
    }

    public class ViewMapper {

        LottieAnimationView loadingView;

        public ViewMapper( ) {
            loadingView = findViewById( R.id.lottie_splash );
        }

    }


}