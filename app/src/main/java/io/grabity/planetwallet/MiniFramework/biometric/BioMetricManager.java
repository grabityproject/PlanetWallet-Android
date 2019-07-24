package io.grabity.planetwallet.MiniFramework.biometric;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricConstants;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.grabity.planetwallet.MiniFramework.utils.PLog;

public class BioMetricManager {
    private Context context;
    private static BioMetricManager instance;
    private FingerprintManagerCompat fingerprintManagerCompat;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private Executor executor = Executors.newSingleThreadExecutor( );
    private FragmentActivity activity;

    private OnBioResultListener onBioResultListener;


    public BioMetricManager( Context context ) {
        this.context = context;
        this.fingerprintManagerCompat = FingerprintManagerCompat.from( context );
        this.activity = ( FragmentActivity ) context;
    }

    public BioMetricManager( Context context, OnBioResultListener onBioResultListener ) {
        this.context = context;
        this.fingerprintManagerCompat = FingerprintManagerCompat.from( context );
        this.activity = ( FragmentActivity ) context;
        this.onBioResultListener = onBioResultListener;
    }

    public static void init( Context context ) {
        instance = new BioMetricManager( context );
    }

    public static void init( Context context, OnBioResultListener onBioResultListener ) {
        instance = new BioMetricManager( context, onBioResultListener );
    }

    public static BioMetricManager getInstance( ) {
        return instance;
    }


    public boolean isHardwareCheck( ) {
        return fingerprintManagerCompat.isHardwareDetected( );
    }

    public boolean isFingerPrintCheck( ) {
        return fingerprintManagerCompat.hasEnrolledFingerprints( );
    }

    public boolean isFingerPermissionCheck( ) {
        if ( ActivityCompat.checkSelfPermission( context, Manifest.permission.USE_FINGERPRINT ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission( context, Manifest.permission.USE_BIOMETRIC ) == PackageManager.PERMISSION_GRANTED ) {
            return true;
        } else {
            return false;
        }

    }

    public void startAuth( ) {
        biometricPrompt = new BiometricPrompt( activity, executor, getAuthenticationCallback( ) );
        try {
            promptInfo = new BiometricPrompt.PromptInfo.Builder( )
                    .setTitle( "지문인식" )
                    .setSubtitle( "서브타이틀공간" )
                    .setDescription( "설명공간" )
                    .setNegativeButtonText( "취소" )
                    .build( );
            biometricPrompt.authenticate( promptInfo );
        } catch ( IllegalArgumentException e ) {

        }
    }

    public void stopAuth( ) {
        biometricPrompt.cancelAuthentication( );

    }

    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback( ) {
        return new BiometricPrompt.AuthenticationCallback( ) {
            @Override
            public void onAuthenticationError( int errorCode, @NonNull CharSequence errString ) {
                super.onAuthenticationError( errorCode, errString );
                PLog.e( "onAuthenticationError errorCode : " + errorCode );
                PLog.e( "onAuthenticationError errString : " + errString );
                if ( onBioResultListener != null ) {
                    onBioResultListener.onBioResult( false, errString );
                }
            }

            @Override
            public void onAuthenticationSucceeded( @NonNull BiometricPrompt.AuthenticationResult result ) {
                super.onAuthenticationSucceeded( result );
                if ( onBioResultListener != null ) {
                    onBioResultListener.onBioResult( true, null );
                }
            }

            @Override
            public void onAuthenticationFailed( ) {
                super.onAuthenticationFailed( );
                if ( onBioResultListener != null ) {
                    onBioResultListener.onBioResult( false, null );
                }
            }
        };
    }

    public interface OnBioResultListener {
        void onBioResult( boolean isResult, Object data );
    }
}
