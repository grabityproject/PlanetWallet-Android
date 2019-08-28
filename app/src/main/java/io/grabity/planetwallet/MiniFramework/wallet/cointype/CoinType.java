package io.grabity.planetwallet.MiniFramework.wallet.cointype;

import io.grabity.planetwallet.MiniFramework.utils.Utils;

public enum CoinType {
    NULL( null, null, null, null, null, null ),
    BTC( 0, "BTC", "BitCoin", "BTC", "satoshi", 8 ),
    ETH( 60, "ETH", "Ethereum", "ETH", "satoshi", 18 ),
    ERC20( -60, "ETH", "ERC20 Token", null, null, null );

    private Integer coinType;
    private String coinName;
    private String defaultUnit;
    private String minimumUnit;
    private Integer precision;
    private String parent;

    CoinType( Integer coinType, String parent, String coinName, String defaultUnit, String minimumUnit, Integer precision ) {
        this.coinType = coinType;
        this.coinName = coinName;
        this.parent = parent;
        this.defaultUnit = defaultUnit;
        this.minimumUnit = minimumUnit;
        this.precision = precision;
    }

    public Integer getCoinType( ) {
        return coinType;
    }

    public String getCoinName( ) {
        return coinName;
    }

    public Integer getPrecision( ) {
        return precision;
    }

    public String getMinimumUnit( ) {
        return minimumUnit;
    }

    public String getDefaultUnit( ) {
        return defaultUnit;
    }

    public String getParent( ) {
        return parent;
    }

    public static CoinType of( Integer coinType ) {
        if ( coinType == 0 ) {
            return CoinType.BTC;
        } else if ( coinType == 60 ) {
            return CoinType.ETH;
        } else if ( coinType == -60 ) {
            return CoinType.ERC20;
        }
        return CoinType.NULL;
    }

    public static CoinType of( String symbol ) {
        if ( Utils.equals( CoinType.BTC.name( ), symbol ) ) {
            return CoinType.BTC;
        } else if ( Utils.equals( CoinType.ETH.name( ), symbol ) ) {
            return CoinType.ETH;
        } else if ( Utils.equals( CoinType.ERC20.name( ), symbol ) ) {
            return CoinType.ERC20;
        }
        return CoinType.NULL;
    }

}
