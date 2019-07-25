package io.grabity.planetwallet.MiniFramework.wallet.store;

import android.util.Log;

import com.pentasecurity.cryptowallet.exceptions.NoKeyException;
import com.pentasecurity.cryptowallet.key.HDKeyPair;
import com.pentasecurity.cryptowallet.key.HDKeyPairStore;
import com.pentasecurity.cryptowallet.storage.DefaultStorageCrypter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.grabity.planetwallet.MiniFramework.managers.DatabaseManager.PWDBManager;
import io.grabity.planetwallet.VO.KeyPair;
import io.grabity.planetwallet.VO.Planet;

public class KeyPairStore implements HDKeyPairStore {

    private static KeyPairStore instance;

    private Map< String, KeyPair > keyPairMap = new HashMap<>( );
    private final DefaultStorageCrypter storageCrypter;

    private final Map< String, Planet > items = new HashMap<>( );

    public static void init( DefaultStorageCrypter storageCrypter ) {
        instance = new KeyPairStore( storageCrypter );
    }

    public static KeyPairStore getInstance( ) {
        return instance;
    }

    private KeyPairStore( DefaultStorageCrypter storageCrypter ) {
        this.storageCrypter = storageCrypter;
        if ( PWDBManager.getInstance( ) != null ) {
            ArrayList< KeyPair > keyPairs = PWDBManager.getInstance( ).select( KeyPair.class );
            for ( KeyPair keyPair : keyPairs ) {
                this.keyPairMap.put( keyPair.getKeyId( ), keyPair );
            }
        }
    }

    public String saveKeyPair( HDKeyPair keyPair, char[] pinCode ) {
        String keyId = keyPair.getId( );
        if ( !keyPairMap.containsKey( keyId ) ) {
            byte[] privateKey = this.storageCrypter.doubleEncrypt( keyPair.getPrivateKey( ), pinCode );
            byte[] publicKey = this.storageCrypter.singleEncrypt( keyPair.getPublicKey( ) );
            byte[] chainCode = this.storageCrypter.singleEncrypt( keyPair.getChainCode( ) == null ? new byte[]{ } : keyPair.getChainCode( ) );

            KeyPair insertData = new KeyPair( );
            insertData.setKeyId( keyId );
            insertData.setValue( bufData( privateKey, publicKey, chainCode ) );
            insertData.setMaster( "-1" );
            PWDBManager.getInstance( ).insertData( insertData );
            keyPairMap.put( keyId, insertData );
        }
        return keyId;
    }

    /**
     * index 0 : privateKey
     * index 1 : publicKey
     * index 2 : chainCode
     * index 3 : mnemonic
     */
    public void changePWDBKeyPairs( char[] beforePinCode, char[] changePinCode ) {
        ArrayList< KeyPair > keyPairs = PWDBManager.getInstance( ).select( KeyPair.class );

        for ( int i = 0; i < keyPairs.size( ); i++ ) {

            byte[] privateKeyPlain = storageCrypter.doubleDecrypt( bytesFromBufData( hexStringToByteArray( keyPairs.get( i ).getValue( ) ), 0 ), beforePinCode );
            byte[] publicKeyPlain = storageCrypter.singleDecrypt( bytesFromBufData( hexStringToByteArray( keyPairs.get( i ).getValue( ) ), 1 ) );
            byte[] chainCodePlain = storageCrypter.singleDecrypt( bytesFromBufData( hexStringToByteArray( keyPairs.get( i ).getValue( ) ), 2 ) );
            byte[] mnemonicPlain = bytesFromBufData( hexStringToByteArray( keyPairs.get( i ).getValue( ) ), 3 ) == null ? null :
                    storageCrypter.doubleDecrypt( bytesFromBufData( hexStringToByteArray( keyPairs.get( i ).getValue( ) ), 3 ), beforePinCode );

            byte[] privateKeyDec = storageCrypter.doubleEncrypt( privateKeyPlain, changePinCode );
            byte[] publicKeyDec = storageCrypter.singleEncrypt( publicKeyPlain );
            byte[] chainCodeDec = storageCrypter.singleEncrypt( chainCodePlain );
            byte[] mnemonicDec = mnemonicPlain == null ? null : storageCrypter.doubleEncrypt( mnemonicPlain, changePinCode );


            String value = mnemonicDec == null ? bufData( privateKeyDec, publicKeyDec, chainCodeDec ) : bufData( privateKeyDec, publicKeyDec, chainCodeDec, mnemonicDec );

            keyPairs.get( i ).setValue( value );
            PWDBManager.getInstance( ).updateData( keyPairs.get( i ), "keyId = '" + keyPairs.get( i ).getKeyId( ) + "'" );

        }

        if ( PWDBManager.getInstance( ) != null ) {
            this.keyPairMap = new HashMap<>( );
            ArrayList< KeyPair > keyPairs2 = PWDBManager.getInstance( ).select( KeyPair.class );
            for ( KeyPair keyPair : keyPairs2 ) {
                this.keyPairMap.put( keyPair.getKeyId( ), keyPair );
            }
        }
    }


    public String saveKeyPair( HDKeyPair keyPair, String phrase, char[] pinCode ) {
        Log.e( getClass( ).getSimpleName( ), "saveKeyPair with phrase" );
        String keyId = keyPair.getId( );
        byte[] privateKey = this.storageCrypter.doubleEncrypt( keyPair.getPrivateKey( ), pinCode );
        byte[] publicKey = this.storageCrypter.singleEncrypt( keyPair.getPublicKey( ) );
        byte[] chainCode = this.storageCrypter.singleEncrypt( keyPair.getChainCode( ) == null ? new byte[]{ } : keyPair.getChainCode( ) );
        byte[] phraseBytes = this.storageCrypter.doubleEncrypt( phrase.getBytes( ), pinCode );

        KeyPair insertData = new KeyPair( );
        insertData.setKeyId( keyId );
        insertData.setValue( bufData( privateKey, publicKey, chainCode, phraseBytes ) );
        insertData.setMaster( "-2" );

        if ( !keyPairMap.containsKey( keyId ) ) {
            PWDBManager.getInstance( ).insertData( insertData );
            keyPairMap.put( keyId, insertData );
        }


        return keyId;
    }

    public String saveMasterKeyPair( int coreCoinType, String phrase, HDKeyPair keyPair, char[] pinCode ) {
        String keyId = keyPair.getId( );
        if ( getMasterKeyPair( coreCoinType, pinCode ) == null ) {
            if ( !keyPairMap.containsKey( keyId ) ) {
                byte[] privateKey = this.storageCrypter.doubleEncrypt( keyPair.getPrivateKey( ), pinCode );
                byte[] publicKey = this.storageCrypter.singleEncrypt( keyPair.getPublicKey( ) );
                byte[] chainCode = this.storageCrypter.singleEncrypt( keyPair.getChainCode( ) == null ? new byte[]{ } : keyPair.getChainCode( ) );
                byte[] phraseBytes = this.storageCrypter.doubleEncrypt( phrase.getBytes( ), pinCode );
                KeyPair insertData = new KeyPair( );
                insertData.setKeyId( keyId );
                insertData.setValue( bufData( privateKey, publicKey, chainCode, phraseBytes ) );
                insertData.setMaster( String.valueOf( coreCoinType ) );
                PWDBManager.getInstance( ).insertData( insertData );
                keyPairMap.put( keyId, insertData );
            }
        }
        return keyId;
    }

    public HDKeyPair getMasterKeyPair( int coreCoinType, char[] pinCode ) {
        ArrayList< KeyPair > keyPairs = new ArrayList<>( keyPairMap.values( ) );
        for ( KeyPair keyPair : keyPairs ) {
            if ( keyPair.getMaster( ).equals( String.valueOf( coreCoinType ) ) ) {
                return getKeyPair( keyPair.getKeyId( ), pinCode );
            }
        }
        return null;
    }

    public String getPhrase( int coreCoinType, char[] pinCode ) {
        ArrayList< KeyPair > keyPairs = new ArrayList<>( keyPairMap.values( ) );
        for ( KeyPair keyPair : keyPairs ) {
            if ( keyPair.getMaster( ).equals( String.valueOf( coreCoinType ) ) ) {
                byte[] phrase = this.storageCrypter.doubleDecrypt( bytesFromBufData( hexStringToByteArray( keyPair.getValue( ) ), 3 ), pinCode );
                return new String( phrase );
            }
        }
        return null;
    }

    public String getPhrase( String keyId, char[] pinCode ) {
        KeyPair keyPair = keyPairMap.get( keyId );
        if ( keyPair != null ) {
            byte[] phrase = this.storageCrypter.doubleDecrypt( bytesFromBufData( hexStringToByteArray( keyPair.getValue( ) ), 3 ), pinCode );
            return new String( phrase );
        }
        return null;
    }

    public HDKeyPair getKeyPair( String keyId, char[] pinCode ) throws NoKeyException {
        if ( keyPairMap.containsKey( keyId ) ) {
            if ( Objects.requireNonNull( keyPairMap.get( keyId ) ).getValue( ) != null ) {
                byte[] privateKey = this.storageCrypter.doubleDecrypt( bytesFromBufData( hexStringToByteArray( Objects.requireNonNull( keyPairMap.get( keyId ) ).getValue( ) ), 0 ), pinCode );
                return this.genHDKeyPair( keyId, privateKey );
            }
        }
        return null;
    }

    public HDKeyPair getKeyPair( String keyId ) throws NoKeyException {
        return this.genHDKeyPair( keyId, ( byte[] ) null );
    }

    //add
    public void generateKeyPairDelete( String keyId ) {
        if ( keyPairMap.get( keyId ) != null ) {
            this.keyPairMap.remove( keyId );

            KeyPair keyPair = new KeyPair( );
            keyPair.setKeyId( keyId );
            PWDBManager.getInstance( ).deleteData( keyPair );
        }
    }

    public void deleteKeyPair( String keyId ) {

        if ( keyPairMap.get( keyId ) != null ) {
            if ( Integer.parseInt( keyPairMap.get( keyId ).getMaster( ) ) < 0 ) {
                this.keyPairMap.remove( keyId );
            }
            KeyPair keyPair = new KeyPair( );
            keyPair.setKeyId( keyId );
            PWDBManager.getInstance( ).deleteData( keyPair, "keyId='" + keyId + "'" + " AND master < 0" );
        }
    }

    public ArrayList< KeyPair > all( ) {
        return new ArrayList<>( keyPairMap.values( ) );
    }

    private HDKeyPair genHDKeyPair( String keyId, byte[] privateKey ) {
        if ( keyPairMap.containsKey( keyId ) ) {
            if ( Objects.requireNonNull( keyPairMap.get( keyId ) ).getValue( ) != null ) {
                byte[] publicKey = bytesFromBufData( hexStringToByteArray( Objects.requireNonNull( keyPairMap.get( keyId ) ).getValue( ) ), 1 );
                if ( publicKey != null && publicKey.length != 0 ) {
                    publicKey = this.storageCrypter.singleDecrypt( publicKey );
                    byte[] chainCode = this.storageCrypter.singleDecrypt( bytesFromBufData( hexStringToByteArray( Objects.requireNonNull( keyPairMap.get( keyId ) ).getValue( ) ), 2 ) );
                    return new HDKeyPair( privateKey, publicKey, chainCode );
                } else {
                    throw new NoKeyException( keyId );
                }
            }
        }
        return null;
    }


    private byte[] bytesFromBufData( byte[] bufData, int index ) {
        int before = 0;
        int count = 0;
        while ( true ) {
            if ( count == index ) {
                return Arrays.copyOfRange( bufData, before + 1, before + 1 + ( bufData[ before ] & 0xFF ) );
            }
            before = before + 1 + ( bufData[ before ] & 0xFF );
            count++;
            if ( before >= bufData.length ) {
                return null;
            }
        }
    }

    private String bufData( byte[]... datas ) {
        StringBuilder builder = new StringBuilder( );
        for ( byte[] data : datas ) {
            builder.append( String.format( "%02X", data.length ) ).append( byteArrayToHexString( data ) );
        }
        return builder.toString( );
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
