package io.grabity.planetwallet.MiniFramework.wallet.managers;

import android.text.TextUtils;
import android.util.Log;

import com.pentasecurity.cryptowallet.JniWrapper;
import com.pentasecurity.cryptowallet.currencies.DefinedCurrency;
import com.pentasecurity.cryptowallet.currencies.eth.JniEthWalletAccountService;
import com.pentasecurity.cryptowallet.key.HDKeyPair;
import com.pentasecurity.cryptowallet.key.HDKeyPairService;
import com.pentasecurity.cryptowallet.key.JniHDKeyPairService;
import com.pentasecurity.cryptowallet.key.JniMnemonicService;
import com.pentasecurity.cryptowallet.key.MnemonicService;
import com.pentasecurity.cryptowallet.utils.PcwfUtils;
import com.pentasecurity.cryptowallet.wallet.WalletAccount;
import com.pentasecurity.cryptowallet.wallet.WalletAccountService;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.VO.Planet;


public class EthereumManager {

    private MnemonicService mnemonicService = new JniMnemonicService( );
    private HDKeyPairService hdKeyPairService = new JniHDKeyPairService( );
    private WalletAccountService ethWalletAccountService;

    private static EthereumManager instance;

    public static void init( ) {
        instance = new EthereumManager( );
    }

    public static EthereumManager getInstance( ) {
        return instance;
    }

    private EthereumManager( ) {
        ethWalletAccountService = new JniEthWalletAccountService( hdKeyPairService, KeyPairStore.getInstance( ) );
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
        int[] ethCoinAccountPath = PcwfUtils.getHDPath( "44H/60H/0H" );

        HDKeyPair ethCoinAccountKey = hdKeyPairService.deriveHDKeyPair( masterKey, ethCoinAccountPath );
        KeyPairStore.getInstance( ).saveKeyPair( ethCoinAccountKey, pinCode );
        HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( ethCoinAccountKey, PcwfUtils.getHDPath( "0/0" ) );
        String childKeyId = KeyPairStore.getInstance( ).saveKeyPair( childKeyPair, mnemonicPhrase, pinCode );
        WalletAccount account = ethWalletAccountService.createHDWalletAccount(
                ethCoinAccountKey.getId( ),
                CoinType.ETH.name( ),
                DefinedCurrency.of( CoinType.ETH.getCoinType( ) ),
                "0/0" );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.ETH.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.ETH.getPrecision( ) ) );
        planet.setPathIndex( -2 );
        planet.setKeyId( childKeyId );
        planet.setHide( "N" );
        planet.setSymbol( CoinType.ETH.getDefaultUnit( ) );
        Log.e( "ETH Import MN Planet info : ", planet.toString( ) );
        KeyPairStore.getInstance( ).deleteKeyPair( ethCoinAccountKey.getId( ) );
//        PlanetStore.getInstance( ).save( planet );


        return planet;
    }

    public Planet importPrivateKey( String privKey, char[] pinCode ) {
        //패턴체크
        if ( privKey.substring( 0, 2 ).equals( "0x" ) ) {
            Pattern p = Pattern.compile( "^0x[a-fA-F0-9]{64}$" );
            Matcher m = p.matcher( privKey );
            if ( !m.find( ) ) {
                return null;
            }
        } else {
            Pattern p = Pattern.compile( "^[a-fA-F0-9]{64}$" );
            Matcher m = p.matcher( privKey );
            if ( !m.find( ) ) {
                return null;
            }
        }


        byte[] privateKey = PcwfUtils.hexStringToByteArray( privKey );
        byte[] publicKey = JniWrapper.GenPubkeyFromPrikey( privateKey );

        HDKeyPair keyPair = new HDKeyPair( privateKey, publicKey );
        KeyPairStore.getInstance( ).saveKeyPair( keyPair, pinCode );

        WalletAccount account = ethWalletAccountService.createBasicAccount(
                keyPair.getId( ),
                CoinType.ETH.name( ),
                DefinedCurrency.of( CoinType.ETH.getCoinType( ) ) );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.ETH.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.ETH.getPrecision( ) ) );
        planet.setPathIndex( -1 );
        planet.setKeyId( keyPair.getId( ) );
        planet.setHide( "N" );
        planet.setSymbol( CoinType.ETH.getDefaultUnit( ) );
//        PlanetStore.getInstance( ).save( planet );

        return planet;
    }

    public Planet addPlanet( char[] pinCode ) {

        List< Planet > list = PlanetStore.getInstance( ).getPlanetList( "ETH" );
        int index = -1;
        for ( Planet planet : list ) {
            if ( planet.getPathIndex( ) != null ) {
                if ( index <= planet.getPathIndex( ) ) {
                    index = planet.getPathIndex( );
                }
            }
        }
        index = index + 1;
        HDKeyPair masterKeyPair = KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.ETH.getCoinType( ), pinCode );
        HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( masterKeyPair, PcwfUtils.getHDPath( "0/" + index ) );
        String childKeyId = KeyPairStore.getInstance( ).saveKeyPair( childKeyPair, pinCode );
        WalletAccount account = ethWalletAccountService.createHDWalletAccount(
                masterKeyPair.getId( ),
                CoinType.ETH.name( ),
                DefinedCurrency.of( CoinType.ETH.getCoinType( ) ),
                "0/" + index );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.ETH.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.ETH.getPrecision( ) ) );
        planet.setPathIndex( index );
        planet.setKeyId( childKeyId );
        planet.setHide( "N" );
        planet.setSymbol( CoinType.ETH.getDefaultUnit( ) );

//        PlanetStore.getInstance( ).save( planet );
        return planet;
    }

    public Planet addPlanet( int index, char[] pinCode ) {

        HDKeyPair masterKeyPair = KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.ETH.getCoinType( ), pinCode );
        HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( masterKeyPair, PcwfUtils.getHDPath( "0/" + index ) );
        String childKeyId = KeyPairStore.getInstance( ).saveKeyPair( childKeyPair, pinCode );
        WalletAccount account = ethWalletAccountService.createHDWalletAccount(
                masterKeyPair.getId( ),
                CoinType.ETH.name( ),
                DefinedCurrency.of( CoinType.ETH.getCoinType( ) ),
                "0/" + index );

        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( CoinType.ETH.getCoinType( ) );
        planet.setDecimals( String.valueOf( CoinType.ETH.getPrecision( ) ) );
        planet.setPathIndex( index );
        planet.setKeyId( childKeyId );
        planet.setHide( "N" );
        planet.setSymbol( CoinType.ETH.getDefaultUnit( ) );

//        PlanetStore.getInstance( ).save( planet );
        return planet;
    }

    public void generateMaster( char[] pinCode ) {
        int entropySize = 128;
        List< String > mnemonic = mnemonicService.generateMnemonic( entropySize );
        byte[] seed = mnemonicService.createSeed( mnemonic, "" );
        HDKeyPair masterKey = hdKeyPairService.deriveHDMasterKey( seed );
        {
            int[] ethCoinAccountPath = PcwfUtils.getHDPath( "44H/60H/0H" );
            HDKeyPair ethCoinAccountKey = hdKeyPairService.deriveHDKeyPair( masterKey, ethCoinAccountPath );
            HDKeyPair masterKeyPair = KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.ETH.getCoinType( ), pinCode );
            if ( masterKeyPair != null ) {
                HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( masterKeyPair, PcwfUtils.getHDPath( "0/0" ) );

                //add
//                KeyPairStore.getInstance( ).keyPairMapRemove( masterKeyPair.getId( ) );
//                KeyPairStore.getInstance( ).keyPairMapRemove( childKeyPair.getId( ) );
//
//                PWDBManager.getInstance( ).deleteData( new KeyPair( ), "keyId='" + masterKeyPair.getId( ) + "'" );
//                PWDBManager.getInstance( ).deleteData( new KeyPair( ), "keyId='" + childKeyPair.getId( ) + "'" );


//                KeyPairStore.getInstance( ).deleteKeyPair( masterKeyPair.getId( ) );
//                KeyPairStore.getInstance( ).deleteKeyPair( childKeyPair.getId( ) );


                KeyPairStore.getInstance( ).generateKeyPairDelete( masterKeyPair.getId( ) );
                KeyPairStore.getInstance( ).generateKeyPairDelete( childKeyPair.getId( ) );

                //기존
//                ArrayList< Planet > ethPlanets = PlanetStore.getInstance().getPlanetList( "ETH" );
//                PLog.e( "ETH Planets size : " + ethPlanets.size( ) );
//                if ( ethPlanets.size( ) == 0 ) {
//                    KeyPairStore.getInstance( ).generateKeyPairDelete( masterKeyPair.getId( ) );
//                    KeyPairStore.getInstance( ).generateKeyPairDelete( childKeyPair.getId( ) );
//                } else {
//                    KeyPairStore.getInstance( ).deleteKeyPair( masterKeyPair.getId( ) );
//                    KeyPairStore.getInstance( ).deleteKeyPair( childKeyPair.getId( ) );
//                }
                PlanetStore.getInstance( ).delete( childKeyPair.getId( ) );
            }

            KeyPairStore.getInstance( ).saveMasterKeyPair( CoinType.ETH.getCoinType( ), TextUtils.join( " ", mnemonic ), ethCoinAccountKey, pinCode );

//            masterKeyPair = KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.ETH.getCoinType( ), pinCode );
//            HDKeyPair childKeyPair = hdKeyPairService.deriveHDKeyPair( masterKeyPair, PcwfUtils.getHDPath( "0/0" ) );
//            String childKeyId = KeyPairStore.getInstance( ).saveKeyPair( childKeyPair, pinCode );
//            WalletAccount account = ethWalletAccountService.createHDWalletAccount(
//                    masterKeyPair.getId( ),
//                    CoinType.ETH.name( ),
//                    DefinedCurrency.of( CoinType.ETH.getCoinType( ) ),
//                    "0/0" );
//
//            PlanetStore.getInstance( ).save( walletAccountToPlanet( childKeyId, account, CoinType.ETH ) );
        }

    }

    private Planet walletAccountToPlanet( String ketId, WalletAccount account, CoinType currencyInfo ) {
        Planet planet = new Planet( );
        planet.setAddress( account.getAddress( ) );
        planet.setCoinType( currencyInfo.getCoinType( ) );
        planet.setDecimals( String.valueOf( currencyInfo.getPrecision( ) ) );
        planet.setPathIndex( 0 );
        planet.setKeyId( ketId );
        planet.setHide( "N" );
        planet.setSymbol( currencyInfo.getDefaultUnit( ) );
        return planet;
    }

    public boolean validateAddress( String address ) {

        return ethWalletAccountService.validateAddress( address );
    }

    //add web3j source 참고
    public boolean isValidAddress( String address ) {
        if ( address == null || address.length( ) < 2 ) return false;
        if ( address.charAt( 0 ) == '0' && address.charAt( 1 ) == 'x' ) {
            address = address.substring( 2 );
        }
        try {
            new BigInteger( address, 16 );
        } catch ( NumberFormatException e ) {
            return false;
        }

        return address.length( ) == 160 >> 2;
    }

}
