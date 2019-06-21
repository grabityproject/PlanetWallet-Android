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

import io.grabity.planetwallet.MiniFramework.utils.PLog;
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
                DefinedCurrency.of( CoinType.BTC.getCoinType( ) ),
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

        byte[] privateKey = PcwfUtils.hexStringToByteArray( privKey );
        byte[] publicKey = JniWrapper.GenPubkeyFromPrikey( privateKey );

        HDKeyPair keyPair = new HDKeyPair( privateKey, publicKey );
        KeyPairStore.getInstance( ).saveKeyPair( keyPair, pinCode );

        WalletAccount account = btcWalletAccountService.createBasicAccount(
                keyPair.getId( ),
                CoinType.BTC.name( ),
                DefinedCurrency.of( CoinType.BTC.getCoinType( ) ) );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.BTC.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.BTC.getPrecision( ) ) );
        planet.setPathIndex( -2 );
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
                DefinedCurrency.of( CoinType.BTC.getCoinType( ) ),
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
                DefinedCurrency.of( CoinType.BTC.getCoinType( ) ),
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
                KeyPairStore.getInstance( ).deleteKeyPair( masterKeyPair.getId( ) );
                KeyPairStore.getInstance( ).deleteKeyPair( childKeyPair.getId( ) );
                PlanetStore.getInstance( ).delete( childKeyPair.getId( ) );
            }

            KeyPairStore.getInstance( ).saveMasterKeyPair( CoinType.BTC.getCoinType( ), TextUtils.join( " ", mnemonic ), btcCoinAccountKey, pinCode );

//            masterKeyPair = KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.BTC.getCoinType( ), pinCode );
//            HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( masterKeyPair, PcwfUtils.getHDPath( "0/0" ) );
//            String childKeyId = KeyPairStore.getInstance( ).saveKeyPair( childKeyPair, pinCode );
//            WalletAccount account = btcWalletAccountService.createHDWalletAccount(
//                    masterKeyPair.getId( ),
//                    CoinType.BTC.name( ),
//                    DefinedCurrency.of( CoinType.BTC.getCoinType( ) ),
//                    "0/0" );
//            PlanetStore.getInstance( ).save( walletAccountToPlanet( childKeyId, account, CoinType.BTC ) );
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

}
