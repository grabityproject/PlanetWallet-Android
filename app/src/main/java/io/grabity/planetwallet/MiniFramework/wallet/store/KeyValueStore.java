package io.grabity.planetwallet.MiniFramework.wallet.store;

import android.content.Context;

import com.pentasecurity.cryptowallet.exceptions.DecryptionErrorException;
import com.pentasecurity.cryptowallet.storage.DefaultStorageCrypter;

public class KeyValueStore {

    private static KeyValueStore instance;

    private Context context;
    private final DefaultStorageCrypter storageCrypter;

    public static void init( Context context, DefaultStorageCrypter storageCrypter ) {
        instance = new KeyValueStore( context, storageCrypter );
    }

    public static KeyValueStore getInstance( ) {
        return instance;
    }

    private KeyValueStore( Context context, DefaultStorageCrypter storageCrypter ) {
        this.context = context;
        this.storageCrypter = storageCrypter;
    }

    public void setValue( String key, String value ) {
        if ( context != null && key != null && value != null ) {
            boolean remove = context.getSharedPreferences( "KeyValueStore", Context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
            boolean save = context.getSharedPreferences( "KeyValueStore", Context.MODE_PRIVATE ).edit( ).putString( key, byteArrayToHexString( this.storageCrypter.singleEncrypt( value.getBytes( ) ) ) ).commit( );
        }
    }

    public void setValue( String key, String value, char[] pinCode ) {
        if ( context != null && key != null && value != null ) {
            boolean remove = context.getSharedPreferences( "KeyValueStore", Context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
            boolean save = context.getSharedPreferences( "KeyValueStore", Context.MODE_PRIVATE ).edit( ).putString( key, byteArrayToHexString( this.storageCrypter.doubleEncrypt( value.getBytes( ), pinCode ) ) ).commit( );
        }
    }

    public String getValue( String key, char[] pinCode ) {
        if ( context != null && key != null ) {
            String encryptString = context.getSharedPreferences( "KeyValueStore", Context.MODE_PRIVATE ).getString( key, null );
            if ( encryptString != null ) {
                try {
                    return new String( storageCrypter.doubleDecrypt( hexStringToByteArray( encryptString ), pinCode ) );
                } catch ( DecryptionErrorException e ) {
                    return null;
                }
            }
        }
        return null;
    }


    public String getValue( String key ) {
        if ( context != null && key != null ) {
            String encryptString = context.getSharedPreferences( "KeyValueStore", Context.MODE_PRIVATE ).getString( key, null );
            if ( encryptString != null ) {
                return new String( storageCrypter.singleDecrypt( hexStringToByteArray( encryptString ) ) );
            }
        }
        return null;
    }

    public void deleteValue( String key ) {
        if ( context != null && key != null ) {
            context.getSharedPreferences( "KeyValueStore", Context.MODE_PRIVATE ).edit( ).remove( key ).apply( );
        }
    }

    private String byteArrayToHexString( byte[] var0 ) {
        StringBuilder var1 = new StringBuilder( );
        int var2 = ( var0 = var0 ).length;

        for ( int var3 = 0; var3 < var2; ++var3 ) {
            byte var4 = var0[ var3 ];
            var1.append( String.format( "%02x", var4 ) );
        }

        return var1.toString( );
    }

    private byte[] hexStringToByteArray( String var0 ) {
        if ( var0 != null && var0.length( ) != 0 && var0.length( ) > 1 && var0.charAt( 0 ) == '0' && var0.charAt( 1 ) == 'x' ) {
            var0 = var0.substring( 2 );
        } else {
            var0 = var0;
        }

        int var1;
        if ( ( var1 = var0.length( ) ) == 0 ) {
            return new byte[ 0 ];
        } else {
            byte[] var2;
            byte var3;
            if ( var1 % 2 != 0 ) {
                ( var2 = new byte[ var1 / 2 + 1 ] )[ 0 ] = ( byte ) Character.digit( var0.charAt( 0 ), 16 );
                var3 = 1;
            } else {
                var2 = new byte[ var1 / 2 ];
                var3 = 0;
            }

            for ( int var4 = var3; var4 < var1; var4 += 2 ) {
                var2[ ( var4 + 1 ) / 2 ] = ( byte ) ( ( Character.digit( var0.charAt( var4 ), 16 ) << 4 ) + Character.digit( var0.charAt( var4 + 1 ), 16 ) );
            }

            return var2;
        }
    }


}
