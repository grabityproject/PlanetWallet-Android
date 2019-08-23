package io.grabity.planetwallet.MiniFramework.wallet.transaction;

import com.google.gson.internal.LinkedTreeMap;
import com.pentasecurity.cryptowallet.utils.PcwfUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.ReturnVO;

public class Erc20RawTx {

    public static String generateRawTx( Transaction tx, ERC20 erc20, String privateKey ) {

        try {
            String[] nonceResult = new Get( null ).setDeviceKey( tx.getDeviceKey( ) ).execute( Route.URL( "nonce", "ETH", tx.getFromAddress( ) ), 0, 0, null ).get( );
            if ( nonceResult.length == 3 && Utils.equals( nonceResult[ 0 ], String.valueOf( 200 ) ) ) {
                ReturnVO returnVO = Utils.jsonToVO( nonceResult[ 1 ], ReturnVO.class );
                if ( returnVO.isSuccess( ) ) {
                    LinkedTreeMap result = ( LinkedTreeMap ) returnVO.getResult( );
                    tx.setNonce( String.valueOf( result.get( "nonce" ) ) );
                }
            }
        } catch ( ExecutionException | InterruptedException e ) {
            e.printStackTrace( );
        }

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                new BigInteger( tx.getNonce( ) ),
                new BigInteger( tx.getGasPrice( ) ),
                new BigInteger( tx.getGasLimit( ) ),
                erc20.getContract( ),
                erc20TransferEncode( new BigInteger( tx.getAmount( ) ), tx.getToAddress( ) )
        );

        Credentials credentials = Credentials.create( privateKey );
        byte[] signedMessage = TransactionEncoder.signMessage( rawTransaction, credentials );

        return PcwfUtils.byteArrayToHexString( signedMessage );
    }


    private static String erc20TransferEncode( BigInteger amount, String toAddress ) {
        String padding = "0000000000000000000000000000000000000000000000000000000000000000";
        String value = PcwfUtils.byteArrayToHexString( amount.toByteArray( ) );
        value = padding.substring( value.length( ) ) + value;
        String address = padding.substring( toAddress.replace( "0x", "" ).replace( "0X", "" ).length( ) ) + toAddress.replace( "0x", "" ).replace( "0X", "" );
        return "0xa9059cbb" + address + value;
    }


}
