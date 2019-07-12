package io.grabity.planetwallet.Common.components;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Arrays;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.Views.p1_Splash.Activity.SplashActivity;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeRegistrationActivity;

public class AppLifeCycleTracker implements Application.ActivityLifecycleCallbacks {

    private PlanetWalletApplication planetWalletApplication;
    private int numStarted = 0;

    public AppLifeCycleTracker( PlanetWalletApplication planetWalletApplication ) {
        this.planetWalletApplication = planetWalletApplication;
    }

    @Override
    public void onActivityCreated( Activity activity, Bundle savedInstanceState ) {

    }

    @Override
    public void onActivityStarted( Activity activity ) {
        // 최초 앱 설치시 getPINCODE Log    PINCODE : []
        if ( numStarted == 0 ) {
            if ( planetWalletApplication.getPINCODE( ) == null ) {
                //add
//                if ( PinCodeRegistrationActivity.class.equals( activity.getClass( ) ) ) return;

                if ( !PinCodeCertificationActivity.class.equals( activity.getClass( ) ) )
                    ( ( PlanetWalletActivity ) activity ).sendAction( C.requestCode.PINCODE_IS_NULL, PinCodeCertificationActivity.class );
            }

        }
        numStarted++;
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
                //add
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
