package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Coin implements Serializable {

    /**
     * 필요한 부분만 작성
     */

    //eth + btc
    String coin;
    String balance;
    String coinName;
    int icon;

    // eth
    String currency;

    // btn
    String walletName;
    String transferTime;


    //eth data mapping
    public Coin( String coin, int icon, String balance, String coinName, String currency ) {
        this.coin = coin;
        this.balance = balance;
        this.coinName = coinName;
        this.icon = icon;
        this.currency = currency;
    }

    //btc data mappnig

    public Coin( String coin, String balance, String walletName, String transferTime, int icon ) {
        this.coin = coin;
        this.balance = balance;
        this.icon = icon;
        this.walletName = walletName;
        this.transferTime = transferTime;
    }

    public Coin ( String coin , int icon) {
        this.coin = coin;
        this.icon = icon;
    }

    public String getCoin( ) {
        return coin;
    }

    public void setCoin( String coin ) {
        this.coin = coin;
    }

    public String getBalance( ) {
        return balance;
    }

    public void setBalance( String balance ) {
        this.balance = balance;
    }

    public String getCoinName( ) {
        return coinName;
    }

    public void setCoinName( String coinName ) {
        this.coinName = coinName;
    }

    public int getIcon( ) {
        return icon;
    }

    public void setIcon( int icon ) {
        this.icon = icon;
    }

    public String getCurrency( ) {
        return currency;
    }

    public void setCurrency( String currency ) {
        this.currency = currency;
    }

    public String getWalletName( ) {
        return walletName;
    }

    public void setWalletName( String walletName ) {
        this.walletName = walletName;
    }

    public String getTransferTime( ) {
        return transferTime;
    }

    public void setTransferTime( String transferTime ) {
        this.transferTime = transferTime;
    }
}
