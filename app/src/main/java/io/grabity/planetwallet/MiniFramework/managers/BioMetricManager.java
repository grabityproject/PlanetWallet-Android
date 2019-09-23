package io.grabity.planetwallet.MiniFramework.managers;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.fragment.app.FragmentActivity;

import java.security.KeyStore;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.R;

public class BioMetricManager {

    private static BioMetricManager instance = new BioMetricManager( );

    private final String[] TRANSFORMATION = { KeyProperties.KEY_ALGORITHM_AES, KeyProperties.BLOCK_MODE_CBC, KeyProperties.ENCRYPTION_PADDING_PKCS7 };
    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private final String ALIAS = "bio_key";
    private final String ALIAS_ENC = "bio_enc";
    private static final byte[] DEFAULT_IV = "PlanetWalletPCWF".getBytes( );

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private Executor executor = Executors.newSingleThreadExecutor( );

    private OnBioAuthListener onBioAuthListener;

    private Context context;

    public static BioMetricManager getInstance( ) {
        if ( instance == null ) return instance = new BioMetricManager( );
        return instance;
    }

    public boolean isHardwareCheck( Context context ) {
        this.context = context;
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from( context );
        return fingerprintManagerCompat.isHardwareDetected( );
    }

    public boolean isFingerPrintCheck( Context context ) {
        this.context = context;
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from( context );
        return fingerprintManagerCompat.hasEnrolledFingerprints( );
    }

    public void startAuth( FragmentActivity activity ) {
        if ( isHardwareCheck( activity ) && isFingerPrintCheck( activity ) ) {
            biometricPrompt = new BiometricPrompt( activity, executor, getAuthenticationCallback( ) );
            try {
                promptInfo = new BiometricPrompt.PromptInfo.Builder( )
                        .setTitle( localized( R.string.bio_metric_manager_title ) )
                        .setNegativeButtonText( localized( R.string.bio_metric_manager_cancel_title ) )
                        .build( );
                biometricPrompt.authenticate( promptInfo );
            } catch ( IllegalArgumentException e ) {

            }
        }
    }

    public String localized( int id ) {
        if ( context == null ) return "";
        try {
            return ( ( PlanetWalletActivity ) context ).localized( id );
        } catch ( ClassCastException e ) {
            return "";
        }
    }

    public void stopAuth( ) {
        if ( biometricPrompt != null ) {
            biometricPrompt.cancelAuthentication( );
        }
    }

    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback( ) {
        return new BiometricPrompt.AuthenticationCallback( ) {
            @Override
            public void onAuthenticationError( int errorCode, @NonNull CharSequence errString ) {
                super.onAuthenticationError( errorCode, errString );
                if ( onBioAuthListener != null ) {
                    onBioAuthListener.onBioAuth( false, null );
                }
            }

            @Override
            public void onAuthenticationSucceeded( @NonNull BiometricPrompt.AuthenticationResult result ) {
                super.onAuthenticationSucceeded( result );
                if ( onBioAuthListener != null ) {
                    onBioAuthListener.onBioAuth( true, getKey( ) );
                }
            }

            @Override
            public void onAuthenticationFailed( ) {
                super.onAuthenticationFailed( );
                if ( onBioAuthListener != null ) {
                    onBioAuthListener.onBioAuth( false, null );
                }
            }
        };
    }

    public OnBioAuthListener getOnBioAuthListener( ) {
        return onBioAuthListener;
    }

    public BioMetricManager setOnBioAuthListener( OnBioAuthListener onBioAuthListener ) {
        this.onBioAuthListener = onBioAuthListener;
        return this;
    }

    public void saveKey( char[] PINCODE ) {
        try {
            Cipher cipher = Cipher.getInstance( transformation( TRANSFORMATION ) );
            cipher.init( Cipher.ENCRYPT_MODE, secretKey( ), new IvParameterSpec( DEFAULT_IV ) );
            byte[] enc = cipher.doFinal( String.valueOf( PINCODE ).getBytes( ) );
            KeyValueStore.getInstance( ).setValue( ALIAS_ENC, Base64.encodeToString( enc, Base64.DEFAULT ) );
        } catch ( Throwable var4 ) {
            var4.printStackTrace( );
        }
    }

    public char[] getKey( ) {
        try {
            Cipher cipher = Cipher.getInstance( transformation( TRANSFORMATION ) );
            cipher.init( Cipher.DECRYPT_MODE, secretKey( ), new IvParameterSpec( DEFAULT_IV ) );
            byte[] dec = Base64.decode( KeyValueStore.getInstance( ).getValue( ALIAS_ENC ), Base64.DEFAULT );
            byte[] result = cipher.doFinal( dec );
            char[] items = new char[ result.length ];
            for ( int i = 0; i < result.length; i++ ) {
                items[ i ] = ( char ) result[ i ];
            }
            return items;
        } catch ( Throwable var4 ) {
            return null;
        }
    }

    public void removeKey( ) {
        KeyValueStore.getInstance( ).deleteValue( ALIAS_ENC );
    }

    public SecretKey secretKey( ) {
        try {
            KeyStore keyStore = KeyStore.getInstance( KEYSTORE_PROVIDER );
            keyStore.load( null );
            KeyStore.Entry entry = keyStore.getEntry( ALIAS, null );
            return ( ( KeyStore.SecretKeyEntry ) entry ).getSecretKey( );
        } catch ( Throwable var4 ) {
            throw new RuntimeException( var4 );
        }
    }

    public void generateSecretKey( ) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance( KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER );
            KeyGenParameterSpec keyGenParameterSpec =
                    new KeyGenParameterSpec
                            .Builder( ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT )
                            .setBlockModes( KeyProperties.BLOCK_MODE_CBC )
                            .setEncryptionPaddings( KeyProperties.ENCRYPTION_PADDING_PKCS7 )
                            .setRandomizedEncryptionRequired( false ).build( );
            keyGenerator.init( keyGenParameterSpec );
            keyGenerator.generateKey( );
        } catch ( Throwable err ) {
            throw new RuntimeException( err );
        }
    }


    private String transformation( String... ciphers ) {
        if ( ciphers.length == 0 ) return "";
        StringBuilder cipherStringBuilder = new StringBuilder( );
        for ( String cipher : ciphers ) {
            cipherStringBuilder.append( cipher ).append( "/" );
        }
        cipherStringBuilder.deleteCharAt( cipherStringBuilder.length( ) - 1 );
        return cipherStringBuilder.toString( );
    }

    public interface OnBioAuthListener {
        void onBioAuth( boolean isResult, char[] data );
    }
}
