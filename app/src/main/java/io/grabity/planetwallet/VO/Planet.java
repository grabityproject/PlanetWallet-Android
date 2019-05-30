package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Planet implements Serializable {

    /**
     * 필요한 부분만 작성
     */

    String address;
    String currency;
    String walletName;

    boolean isCheck;

    public Planet( ) {

    }

    public Planet( String address, String currency, String walletName ) {
        this.address = address;
        this.currency = currency;
        this.walletName = walletName;
    }

    public Planet( String address, String currency, String walletName, boolean isCheck ) {
        this.address = address;
        this.currency = currency;
        this.walletName = walletName;
        this.isCheck = isCheck;
    }

    public String getAddress( ) {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
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

    public boolean isCheck( ) {
        return isCheck;
    }

    public void setCheck( boolean check ) {
        isCheck = check;
    }
}
