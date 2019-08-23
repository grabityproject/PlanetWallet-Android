package io.grabity.planetwallet.MiniFramework.wallet.transaction;

import java.math.BigInteger;

import io.grabity.planetwallet.MiniFramework.networktask.NetworkInterface;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.MainItem;

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

    String fee;

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
        if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.BTC ) {
            return fee;
        } else {
            BigInteger gasFee = new BigInteger( gasPrice ).multiply( new BigInteger( gasLimit ) );
            return gasFee.toString( );
        }
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

            return BtcRawTx.generateRawTx( this, privateKey );

        } else if ( Utils.equals( CoinType.ETH.getCoinType( ), mainItem.getCoinType( ) ) ) { // ETH

            return EthRawTx.generateRawTx( this, privateKey );

        } else if ( Utils.equals( CoinType.ERC20.getCoinType( ), mainItem.getCoinType( ) ) ) {

            ERC20 erc20 = ( ERC20 ) mainItem;
            return Erc20RawTx.generateRawTx( this, erc20, privateKey );

        } else {

            return "0x";

        }

    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {

    }
}
