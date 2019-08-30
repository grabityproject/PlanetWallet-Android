package io.grabity.planetwallet.MiniFramework.wallet.managers;

import android.text.TextUtils;
import android.util.Log;

import com.pentasecurity.cryptowallet.JniWrapper;
import com.pentasecurity.cryptowallet.currencies.DefinedCurrency;
import com.pentasecurity.cryptowallet.currencies.btc.JniBtcWalletAccountService;
import com.pentasecurity.cryptowallet.key.HDKeyPair;
import com.pentasecurity.cryptowallet.key.HDKeyPairService;
import com.pentasecurity.cryptowallet.key.JniHDKeyPairService;
import com.pentasecurity.cryptowallet.key.JniMnemonicService;
import com.pentasecurity.cryptowallet.key.MnemonicService;
import com.pentasecurity.cryptowallet.utils.PcwfUtils;
import com.pentasecurity.cryptowallet.wallet.WalletAccount;
import com.pentasecurity.cryptowallet.wallet.WalletAccountService;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grabity.planetwallet.MiniFramework.utils.Base58.Base58;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.VO.Planet;

public class BitCoinManager {

    private MnemonicService mnemonicService = new JniMnemonicService( );
    private HDKeyPairService hdKeyPairService = new JniHDKeyPairService( );
    private WalletAccountService btcWalletAccountService;

    private static BitCoinManager instance;

    public static void init( ) {
        instance = new BitCoinManager( );
    }

    public static BitCoinManager getInstance( ) {
        return instance;
    }

    private BitCoinManager( ) {
        btcWalletAccountService = new JniBtcWalletAccountService( hdKeyPairService, KeyPairStore.getInstance( ) );
    }

    public Planet importMnemonic( String mnemonicPhrase, String passphrase, char[] pinCode ) {
        if ( passphrase == null ) passphrase = "";
        try {
            JniWrapper.CheckMnemonicValid( mnemonicPhrase );

        } catch ( Exception e ) {
            PLog.e( "Not 니모닉" );
            return null;
        }

        List< String > mnemonic = Arrays.asList( mnemonicPhrase.trim( ).split( " " ) );
        byte[] seed = mnemonicService.createSeed( mnemonic, passphrase );
        HDKeyPair masterKey = hdKeyPairService.deriveHDMasterKey( seed );
        int[] btcCoinAccountPath = PcwfUtils.getHDPath( "44H/0H/0H" );

        HDKeyPair btcCoinAccountKey = hdKeyPairService.deriveHDKeyPair( masterKey, btcCoinAccountPath );
        KeyPairStore.getInstance( ).saveKeyPair( btcCoinAccountKey, pinCode );
        HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( btcCoinAccountKey, PcwfUtils.getHDPath( "0/0" ) );
        String childKeyId = KeyPairStore.getInstance( ).saveKeyPair( childKeyPair, mnemonicPhrase, pinCode );
        WalletAccount account = btcWalletAccountService.createHDWalletAccount(
                btcCoinAccountKey.getId( ),
                CoinType.BTC.name( ),
//                DefinedCurrency.of( CoinType.BTC.getCoinType( ) ),
                DefinedCurrency.of( 251658240 ),
                "0/0" );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.BTC.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.BTC.getPrecision( ) ) );
        planet.setPathIndex( -2 );
        planet.setKeyId( childKeyId );
        planet.setHide( "N" );
        planet.setSymbol( CoinType.BTC.getDefaultUnit( ) );
        Log.e( getClass( ).getSimpleName( ), planet.toString( ) );


        KeyPairStore.getInstance( ).deleteKeyPair( btcCoinAccountKey.getId( ) );
//        PlanetStore.getInstance( ).save( planet );

        return planet;
    }

    public Planet importPrivateKey( String privKey, char[] pinCode ) {
        PLog.e( "input PrivateKey : " + privKey );
        byte[] privateKeyWif = null;
        byte[] checksum = null;
        try {
            // Base58 check
            if ( privKey.length( ) < 10 ) return null;
            JniWrapper.GenBase58CheckDecode( privKey );
            privateKeyWif = Base58.decode( privKey );
            // compressed 조사는 여기서 L, K , 5
            if ( privKey.substring( 0, 1 ).equals( "L" ) || privKey.substring( 0, 1 ).equals( "K" ) ) {

                checksum = Arrays.copyOfRange( privateKeyWif, privateKeyWif.length - 4, privateKeyWif.length );
                privateKeyWif = Arrays.copyOfRange( privateKeyWif, 0, privateKeyWif.length - 4 );

                byte[] doubleSha256 = Utils.sha256Binary( Utils.sha256Binary( privateKeyWif ) );
                String s = PcwfUtils.byteArrayToHexString( doubleSha256 );

                if ( s.substring( 0, 8 ).equals( PcwfUtils.byteArrayToHexString( checksum ) ) ) {
                    privateKeyWif = Arrays.copyOfRange( privateKeyWif, 1, privateKeyWif.length - 1 );
                }
            } else if ( privKey.substring( 0, 1 ).equals( "5" ) ) {

                checksum = Arrays.copyOfRange( privateKeyWif, privateKeyWif.length - 4, privateKeyWif.length );
                privateKeyWif = Arrays.copyOfRange( privateKeyWif, 0, privateKeyWif.length - 4 );

                byte[] doubleSha256 = Utils.sha256Binary( Utils.sha256Binary( privateKeyWif ) );
                String s = PcwfUtils.byteArrayToHexString( doubleSha256 );

                if ( s.substring( 0, 8 ).equals( PcwfUtils.byteArrayToHexString( checksum ) ) ) {
                    privateKeyWif = Arrays.copyOfRange( privateKeyWif, 1, privateKeyWif.length );
                }
            }

        } catch ( Exception e ) {
            PLog.e( "Not WIF" );
        }

        //WIF가 아닌경우 HEX 체크
        if ( privateKeyWif == null ) {
            Pattern p = Pattern.compile( "^[a-fA-F0-9]{64}$" );
            Matcher m = p.matcher( privKey );
            if ( !m.find( ) ) {
                return null;
            }
        }


        byte[] privateKey = privateKeyWif != null ? privateKeyWif : PcwfUtils.hexStringToByteArray( privKey );
        byte[] publicKey = JniWrapper.GenPubkeyFromPrikey( privateKey );

        HDKeyPair keyPair = new HDKeyPair( privateKey, publicKey );
        KeyPairStore.getInstance( ).saveKeyPair( keyPair, pinCode );

        WalletAccount account = btcWalletAccountService.createBasicAccount(
                keyPair.getId( ),
                CoinType.BTC.name( ),
//                DefinedCurrency.of( CoinType.BTC.getCoinType( ) ),
                DefinedCurrency.of( 251658240 )
        );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        PLog.e( "BitCoin Manager PrKey Import address : " + planet.getAddress( ) );
        planet.setCoinType( CoinType.BTC.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.BTC.getPrecision( ) ) );
        planet.setPathIndex( -1 );
        planet.setKeyId( keyPair.getId( ) );
        planet.setHide( "N" );
        planet.setSymbol( CoinType.BTC.getDefaultUnit( ) );
//        PlanetStore.getInstance( ).save( planet );

        return planet;
    }


    public Planet addPlanet( char[] pinCode ) {

        List< Planet > list = PlanetStore.getInstance( ).getPlanetList( CoinType.BTC.name( ) );
        int index = -1;
        for ( Planet planet : list ) {
            if ( planet.getPathIndex( ) != null ) {
                if ( index <= planet.getPathIndex( ) ) {
                    index = planet.getPathIndex( );
                }
            }
        }
        index = index + 1;
        HDKeyPair masterKeyPair = KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.BTC.getCoinType( ), pinCode );
        HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( masterKeyPair, PcwfUtils.getHDPath( "0/" + index ) );
        String childKeyId = KeyPairStore.getInstance( ).saveKeyPair( childKeyPair, pinCode );
        WalletAccount account = btcWalletAccountService.createHDWalletAccount(
                masterKeyPair.getId( ),
                CoinType.BTC.name( ),
//                DefinedCurrency.of( CoinType.BTC.getCoinType( ) ),
                DefinedCurrency.of( 251658240 ),
                "0/" + index );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.BTC.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.BTC.getPrecision( ) ) );
        planet.setPathIndex( index );
        planet.setKeyId( childKeyId );
        planet.setHide( "N" );
        planet.setSymbol( CoinType.BTC.getDefaultUnit( ) );

//        PlanetStore.getInstance( ).save( planet );

        return planet;
    }


    public Planet addPlanet( int index, char[] pinCode ) {

        HDKeyPair masterKeyPair = KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.BTC.getCoinType( ), pinCode );
        HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( masterKeyPair, PcwfUtils.getHDPath( "0/" + index ) );
        String childKeyId = KeyPairStore.getInstance( ).saveKeyPair( childKeyPair, pinCode );
        WalletAccount account = btcWalletAccountService.createHDWalletAccount(
                masterKeyPair.getId( ),
                CoinType.BTC.name( ),
//                DefinedCurrency.of( CoinType.BTC.getCoinType( ) ),
                DefinedCurrency.of( 251658240 ),
                "0/" + index );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.BTC.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.BTC.getPrecision( ) ) );
        planet.setPathIndex( index );
        planet.setKeyId( childKeyId );
        planet.setHide( "N" );
        planet.setSymbol( CoinType.BTC.getDefaultUnit( ) );

//        PlanetStore.getInstance( ).save( planet );

        return planet;
    }

    public void generateMaster( char[] pinCode ) {
        int entropySize = 128;
        List< String > mnemonic = mnemonicService.generateMnemonic( entropySize );
        byte[] seed = mnemonicService.createSeed( mnemonic, "" );
        HDKeyPair masterKey = hdKeyPairService.deriveHDMasterKey( seed );
        {
            int[] btcCoinAccountPath = PcwfUtils.getHDPath( "44H/0H/0H" );
            HDKeyPair btcCoinAccountKey = hdKeyPairService.deriveHDKeyPair( masterKey, btcCoinAccountPath );
            HDKeyPair masterKeyPair = KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.BTC.getCoinType( ), pinCode );
            if ( masterKeyPair != null ) {
                HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( masterKeyPair, PcwfUtils.getHDPath( "0/0" ) );

                KeyPairStore.getInstance( ).generateKeyPairDelete( masterKeyPair.getId( ) );
                KeyPairStore.getInstance( ).generateKeyPairDelete( childKeyPair.getId( ) );


                PlanetStore.getInstance( ).delete( childKeyPair.getId( ) );
            }

            KeyPairStore.getInstance( ).saveMasterKeyPair( CoinType.BTC.getCoinType( ), TextUtils.join( " ", mnemonic ), btcCoinAccountKey, pinCode );

        }

    }

    private Planet walletAccountToPlanet( String ketId, WalletAccount account, CoinType currencyInfo ) {
        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.BTC.getCoinType( ) );
        planet.setDecimals( String.valueOf( currencyInfo.getPrecision( ) ) );
        planet.setPathIndex( 0 );
        planet.setKeyId( ketId );
        planet.setHide( "N" );
        planet.setSymbol( currencyInfo.getDefaultUnit( ) );
        return planet;
    }

    public boolean validateAddress( String address ) {
        //test bitcoin testNet + mainNet address
        Pattern p = Pattern.compile( "^[13nNmM][a-km-zA-HJ-NP-Z1-9]{25,34}$" );
        Matcher m = p.matcher( address );

        return m.find( );
//        return btcWalletAccountService.validateAddress( address );
    }


}
