package io.grabity.planetwallet.MiniFramework.wallet.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.pentasecurity.cryptowallet.crypto.HsmKeyCrypter;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public class KeyStoreCrypter implements HsmKeyCrypter {

    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final byte[] DEFAULT_IV = "PlanetWalletPCWF".getBytes( );

    private final String[] TRANSFORMATION = { KeyProperties.KEY_ALGORITHM_AES, KeyProperties.BLOCK_MODE_CBC, KeyProperties.ENCRYPTION_PADDING_PKCS7 };
    private final String keystoreAlias;

    public KeyStoreCrypter( String keystoreAlias ) {
        this.keystoreAlias = keystoreAlias;
        try {
            if ( getHsmKey( keystoreAlias ) == null ) {
                setHsmKey( );
            }
        } catch ( RuntimeException var3 ) {
            setHsmKey( );
        }
    }

    public byte[] encrypt( String alias, byte[] src ) {
        try {
            Cipher cipher = Cipher.getInstance( transformation( TRANSFORMATION ) );
            cipher.init( Cipher.ENCRYPT_MODE, getHsmKey( alias ).getSecretKey( ), new IvParameterSpec( DEFAULT_IV ) );
            return cipher.doFinal( src );
        } catch ( Throwable var4 ) {
            throw new RuntimeException( var4 );
        }
    }

    public byte[] decrypt( String alias, byte[] encrypted ) {
        try {
            Cipher cipher = Cipher.getInstance( transformation( TRANSFORMATION ) );
            cipher.init( Cipher.DECRYPT_MODE, getHsmKey( alias ).getSecretKey( ), new IvParameterSpec( DEFAULT_IV ) );
            return cipher.doFinal( encrypted );
        } catch ( Throwable var4 ) {
            throw new RuntimeException( var4 );
        }
    }

    private KeyStore.SecretKeyEntry getHsmKey( String alias ) {
        try {
            KeyStore keyStore = KeyStore.getInstance( KEYSTORE_PROVIDER );
            keyStore.load( null );
            KeyStore.Entry entry = keyStore.getEntry( alias, null );
            return ( KeyStore.SecretKeyEntry ) entry;
        } catch ( Throwable var4 ) {
            throw new RuntimeException( var4 );
        }
    }

    private void setHsmKey( ) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance( KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_PROVIDER );
            KeyGenParameterSpec keyGenParameterSpec =
                    new KeyGenParameterSpec
                            .Builder( keystoreAlias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT )
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

}
