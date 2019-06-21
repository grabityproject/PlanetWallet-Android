package io.grabity.planetwallet.Common.components;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;

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
        if ( numStarted == 0 ) {
            if ( planetWalletApplication.getPINCODE( ) == null ) {
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
