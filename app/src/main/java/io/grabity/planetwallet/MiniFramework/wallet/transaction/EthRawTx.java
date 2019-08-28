package io.grabity.planetwallet.MiniFramework.wallet.transaction;

import com.google.gson.internal.LinkedTreeMap;
import com.pentasecurity.cryptowallet.utils.PcwfUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;

public class EthRawTx {

    public static String generateRawTx( Tx tx, String deviceKey, String privateKey ) {

        String gasPrice = "20";
        String gasLimit = "21000";

        if ( tx.getGasPrice( ) != null ) gasPrice = tx.getGasPrice( );
        if ( tx.getGasLimit( ) != null ) gasLimit = tx.getGasLimit( );

        estimateFee( tx );

        if ( tx.getNonce( ) == null ) {
            try {
                String[] nonceResult = new Get( null ).setDeviceKey( deviceKey ).execute( Route.URL( "nonce", "ETH", tx.getFrom( ) ), 0, 0, null ).get( );
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
        }

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                new BigInteger( tx.getNonce( ) ),
                new BigInteger( gasPrice ),
                new BigInteger( gasLimit ),
                tx.getTo( ),
                new BigInteger( tx.getAmount( ) ),
                ""
        );

        Credentials credentials = Credentials.create( privateKey );
        byte[] signedMessage = TransactionEncoder.signMessage( rawTransaction, credentials );

        return PcwfUtils.byteArrayToHexString( signedMessage );
    }

    public static String estimateFee( Tx tx ) {

        String gasPrice = "20";
        String gasLimit = "21000";

        if ( tx.getGasPrice( ) != null ) gasPrice = tx.getGasPrice( );
        if ( tx.getGasLimit( ) != null ) gasLimit = tx.getGasLimit( );

        tx.setFee( new BigDecimal( gasPrice ).multiply( new BigDecimal( gasLimit ) ).toBigInteger( ).toString( 10 ) );
        return tx.getFee();
    }
}
