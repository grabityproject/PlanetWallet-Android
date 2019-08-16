package io.grabity.planetwallet.MiniFramework.wallet.cointype;

public enum CoinType {
    NULL( null, null, null, null, null ),
    BTC( 0, "BitCoin", "BTC", "satoshi", 9 ),
    ETH( 60, "Ethereum", "ETH", "satoshi", 18 ),
    ERC20( -60, "ERC20 Token", null, null, null );

    private Integer coinType;
    private String coinName;
    private String defaultUnit;
    private String minimumUnit;
    private Integer precision;

    CoinType( Integer coinType, String coinName, String defaultUnit, String minimumUnit, Integer precision ) {
        this.coinType = coinType;
        this.coinName = coinName;
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

}
