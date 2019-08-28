package io.grabity.planetwallet.MiniFramework.wallet.transaction;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Tx;

public class Transaction {

    MainItem mainItem;
    String deviceKey;
    Tx tx;

    public static Transaction create( MainItem mainItem ) {
        return new Transaction( mainItem );
    }

    private Transaction( MainItem mainItem ) {
        this.mainItem = mainItem;
    }

    public Transaction setTx( Tx tx ) {
        this.tx = tx;
        return this;
    }

    public String getDeviceKey( ) {
        return deviceKey;
    }

    public Transaction setDeviceKey( String deviceKey ) {
        this.deviceKey = deviceKey;
        return this;
    }

    public String getRawTransaction( String privateKey ) {

        if ( tx != null && deviceKey != null && privateKey != null ) {

            if ( Utils.equals( CoinType.BTC.getCoinType( ), mainItem.getCoinType( ) ) ) {

                return BtcRawTx.generateRawTx( tx, deviceKey, privateKey );

            } else if ( Utils.equals( CoinType.ETH.getCoinType( ), mainItem.getCoinType( ) ) ) { // ETH

                return EthRawTx.generateRawTx( tx, deviceKey, privateKey );

            } else if ( Utils.equals( CoinType.ERC20.getCoinType( ), mainItem.getCoinType( ) ) ) {

                return Erc20RawTx.generateRawTx( tx, mainItem, deviceKey, privateKey );

            }
        }

        return "0x";
    }

    public String estimateFee( ) {

        if ( tx != null && deviceKey != null ) {

            if ( Utils.equals( CoinType.BTC.getCoinType( ), mainItem.getCoinType( ) ) ) {

                return BtcRawTx.estimateFee( tx, deviceKey );

            } else if ( Utils.equals( CoinType.ETH.getCoinType( ), mainItem.getCoinType( ) ) ) { // ETH

                return EthRawTx.estimateFee( tx );

            } else if ( Utils.equals( CoinType.ERC20.getCoinType( ), mainItem.getCoinType( ) ) ) {

                return Erc20RawTx.estimateFee( tx );

            }
        }

        return "0x";
    }
}
