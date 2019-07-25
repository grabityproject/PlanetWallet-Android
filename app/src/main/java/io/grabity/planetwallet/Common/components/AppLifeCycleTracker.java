package io.grabity.planetwallet.Common.components;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Views.p1_Splash.Activity.SplashActivity;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeRegistrationActivity;

public class AppLifeCycleTracker implements Application.ActivityLifecycleCallbacks {

    private PlanetWalletApplication planetWalletApplication;
    private String beforeClassName;
    private int numStarted = 0;

    public AppLifeCycleTracker( PlanetWalletApplication planetWalletApplication ) {
        this.planetWalletApplication = planetWalletApplication;
    }

    @Override
    public void onActivityCreated( Activity activity, Bundle savedInstanceState ) {

    }

    @Override
    public void onActivityStarted( Activity activity ) {
        if ( numStarted == 0 ) {
            if ( planetWalletApplication.getPINCODE( ) == null ) {
                if ( !PinCodeCertificationActivity.class.equals( activity.getClass( ) ) )
                    ( ( PlanetWalletActivity ) activity ).sendAction( C.requestCode.PINCODE_IS_NULL, PinCodeCertificationActivity.class );
            }
        } else {
            if ( beforeClassName != null && planetWalletApplication.getPINCODE( ) == null ) {
                if ( PinCodeCertificationActivity.class.getName( ).equals( beforeClassName ) ) {
                    activity.finish( );
                }
            }
        }
        numStarted++;
        beforeClassName = activity.getClass( ).getName( );
    }

    @Override
    public void onActivityResumed( Activity activity ) {

    }

    @Override
    public void onActivityPaused( Activity activity ) {

    }

    @Override
    public void onActivityStopped( Activity activity ) {
        numStarted--;
        if ( numStarted == 0 ) {
            if ( planetWalletApplication != null ) {
                if ( PinCodeRegistrationActivity.class.equals( activity.getClass( ) ) ) return;
                if ( SplashActivity.class.equals( activity.getClass( ) ) ) return;
                planetWalletApplication.setPINCODE( null );
            }
        }
    }

    @Override
    public void onActivitySaveInstanceState( Activity activity, Bundle outState ) {

    }

    @Override
    public void onActivityDestroyed( Activity activity ) {

    }
}
