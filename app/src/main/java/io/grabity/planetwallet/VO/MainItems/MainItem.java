package io.grabity.planetwallet.VO.MainItems;

import java.io.Serializable;

public class MainItem implements Serializable {

    Integer _id;
    String keyId;

    Integer coinType;

    String hide;
    String balance;
    String name;
    String symbol;
    String decimals;
    String img_path;
    String contract;

    boolean check;

    public MainItem( ) {
    }

    public Integer get_id( ) {
        return _id;
    }

    public void set_id( Integer _id ) {
        this._id = _id;
    }

    public String getKeyId( ) {
        return keyId;
    }

    public void setKeyId( String keyId ) {
        this.keyId = keyId;
    }

    public Integer getCoinType( ) {
        return coinType;
    }

    public void setCoinType( Integer coinType ) {
        this.coinType = coinType;
    }

    public String getHide( ) {
        return hide;
    }

    public void setHide( String hide ) {
        this.hide = hide;
    }

    public String getBalance( ) {
        if ( balance == null ) return "0";
        return balance;
    }

    public void setBalance( String balance ) {
        this.balance = balance;
    }

    public String getName( ) {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getSymbol( ) {
        return symbol;
    }

    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }

    public String getDecimals( ) {
        return decimals;
    }

    public void setDecimals( String decimals ) {
        this.decimals = decimals;
    }

    public String getImg_path( ) {
        return img_path;
    }

    public void setImg_path( String img_path ) {
        this.img_path = img_path;
    }

    public String getContract( ) {
        return contract;
    }

    public void setContract( String contract ) {
        this.contract = contract;
    }

    public boolean isCheck( ) {
        return check;
    }

    public void setCheck( boolean check ) {
        this.check = check;
    }
}
