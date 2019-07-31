package io.grabity.planetwallet.MiniFramework.wallet.transaction;

import com.google.gson.internal.LinkedTreeMap;
import com.pentasecurity.cryptowallet.utils.PcwfUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;

import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.networktask.NetworkInterface;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.ReturnVO;

public class Transaction implements NetworkInterface {

    MainItem mainItem;

    String deviceKey;
    String fromAddress;
    String toAddress;
    String amount;
    String gasPrice;
    String gasLimit;
    String nonce;
    String data;

    public Transaction( MainItem mainItem ) {
        this.mainItem = mainItem;
    }

    public String getFromAddress( ) {
        return fromAddress;
    }

    public Transaction from( String fromAddress ) {
        this.fromAddress = fromAddress;
        return this;
    }

    public String getToAddress( ) {
        return toAddress;
    }

    public Transaction to( String toAddress ) {
        this.toAddress = toAddress;
        return this;
    }

    public String getAmount( ) {
        return amount;
    }

    public Transaction value( String amount ) {
        this.amount = amount;
        return this;
    }

    public String getGasPrice( ) {
        return gasPrice;
    }

    public Transaction gasPrice( String gasPrice ) {
        this.gasPrice = gasPrice;
        return this;
    }

    public String getGasLimit( ) {
        return gasLimit;
    }

    public Transaction gasLimit( String gasLimit ) {
        this.gasLimit = gasLimit;
        return this;
    }

    public String getNonce( ) {
        return nonce;
    }

    public void setNonce( String nonce ) {
        this.nonce = nonce;
    }

    public Transaction nonce( String nonce ) {
        this.nonce = nonce;
        return this;
    }

    public String getData( ) {
        return data;
    }

    public Transaction setData( String data ) {
        this.data = data;
        return this;
    }

    public String getDeviceKey( ) {
        return deviceKey;
    }

    public Transaction setDeviceKey( String deviceKey ) {
        this.deviceKey = deviceKey;
        return this;
    }

    public String getFee( ) {
        BigInteger gasFee = new BigInteger( gasPrice ).multiply( new BigInteger( gasLimit ) );
        return gasFee.toString( );
    }

    public String getSymbol( ) {
        if ( Utils.equals( mainItem.getCoinType( ), CoinType.BTC.getCoinType( ) ) ) {
            return CoinType.BTC.name( );
        } else if ( Utils.equals( mainItem.getCoinType( ), CoinType.ETH.getCoinType( ) ) ) {
            return CoinType.ETH.name( );
        } else if ( Utils.equals( mainItem.getCoinType( ), CoinType.ERC20.getCoinType( ) ) ) {
            return ( ( ERC20 ) mainItem ).getSymbol( );
        }
        return null;
    }

    public String getRawTransaction( String privateKey ) {

        if ( Utils.equals( CoinType.BTC.getCoinType( ), mainItem.getCoinType( ) ) ) {

            return "0x";

        } else if ( Utils.equals( CoinType.ETH.getCoinType( ), mainItem.getCoinType( ) ) ) { // ETH

            try {
                String[] nonceResult = new Get( this ).setDeviceKey( getDeviceKey( ) ).execute( Route.URL( "nonce", "ETH", getFromAddress( ) ), 0, 0, null ).get( );
                if ( nonceResult.length == 3 && Utils.equals( nonceResult[ 0 ], String.valueOf( 200 ) ) ) {
                    ReturnVO returnVO = Utils.jsonToVO( nonceResult[ 1 ], ReturnVO.class );
                    if ( returnVO.isSuccess( ) ) {
                        LinkedTreeMap result = ( LinkedTreeMap ) returnVO.getResult( );
                        setNonce( String.valueOf( result.get( "nonce" ) ) );
                    }
                }
            } catch ( ExecutionException | InterruptedException e ) {
                e.printStackTrace( );
            }

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    new BigInteger( getNonce( ) ),
                    new BigInteger( getGasPrice( ) ),
                    new BigInteger( getGasLimit( ) ),
                    getToAddress( ),
                    new BigInteger( getAmount( ) ),
                    ""
            );

            Credentials credentials = Credentials.create( privateKey );
            byte[] signedMessage = TransactionEncoder.signMessage( rawTransaction, credentials );

            return PcwfUtils.byteArrayToHexString( signedMessage );

        } else if ( Utils.equals( CoinType.ERC20.getCoinType( ), mainItem.getCoinType( ) ) ) {

            ERC20 erc20 = ( ERC20 ) mainItem;
            try {
                String[] nonceResult = new Get( this ).setDeviceKey( getDeviceKey( ) ).execute( Route.URL( "nonce", "ETH", getFromAddress( ) ), 0, 0, null ).get( );
                if ( nonceResult.length == 3 && Utils.equals( nonceResult[ 0 ], String.valueOf( 200 ) ) ) {
                    ReturnVO returnVO = Utils.jsonToVO( nonceResult[ 1 ], ReturnVO.class );
                    if ( returnVO.isSuccess( ) ) {
                        LinkedTreeMap result = ( LinkedTreeMap ) returnVO.getResult( );
                        setNonce( String.valueOf( result.get( "nonce" ) ) );
                    }
                }
            } catch ( ExecutionException | InterruptedException e ) {
                e.printStackTrace( );
            }

            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    new BigInteger( getNonce( ) ),
                    new BigInteger( getGasPrice( ) ),
                    new BigInteger( getGasLimit( ) ),
                    erc20.getContract( ),
                    erc20TransferEncode( new BigInteger( getAmount( ) ), getToAddress( ) )
            );

            Credentials credentials = Credentials.create( privateKey );
            byte[] signedMessage = TransactionEncoder.signMessage( rawTransaction, credentials );

            return PcwfUtils.byteArrayToHexString( signedMessage );

        } else {

            return "0x";

        }

    }

    protected String erc20TransferEncode( BigInteger amount, String toAddress ) {
        String padding = "0000000000000000000000000000000000000000000000000000000000000000";

        String value = PcwfUtils.byteArrayToHexString( amount.toByteArray( ) );
        value = padding.substring( value.length( ) ) + value;
        String address = padding.substring( toAddress.replace( "0x", "" ).replace( "0X", "" ).length( ) ) + toAddress.replace( "0x", "" ).replace( "0X", "" );

        return "0xa9059cbb" + address + value;
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {

    }
}
