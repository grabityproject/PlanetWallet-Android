package io.grabity.planetwallet.VO.MainItems;

import java.io.Serializable;

import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;

public class ERC20 extends ETH implements MainItem, Serializable {

    private String _id;
    private String keyId;
    private String contract;
    private String name;
    private String symbol;
    private String balance;
    private String address;
    private String decimals;

    private String price;
    private String hide;

    private String img_path;

    public ERC20( ) {

    }

    public String get_id( ) {
        return _id;
    }

    public void set_id( String _id ) {
        this._id = _id;
    }

    public String getKeyId( ) {
        return keyId;
    }

    public void setKeyId( String keyId ) {
        this.keyId = keyId;
    }

    public String getContract( ) {
        return contract;
    }

    public void setContract( String contract ) {
        this.contract = contract;
    }

    @Override
    public String getName( ) {
        return name;
    }

    @Override
    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String getSymbol( ) {
        return symbol;
    }

    @Override
    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }

    @Override
    public String getBalance( ) {
//        if ( balance == null ) return "0";
//        return balance;
        return "100";
    }

    @Override
    public void setBalance( String balance ) {
        this.balance = balance;
    }

    @Override
    public String getAddress( ) {
        return address;
    }

    @Override
    public void setAddress( String address ) {
        this.address = address;
    }

    public String getDecimals( ) {
        return decimals;
    }

    public void setDecimals( String decimals ) {
        this.decimals = decimals;
    }

    @Override
    public String getPrice( ) {
        return price;
    }

    @Override
    public void setPrice( String price ) {
        this.price = price;
    }

    public String getHide( ) {
        return hide;
    }

    public void setHide( String hide ) {
        this.hide = hide;
    }

    public String getImg_path( ) {
        return img_path;
    }

    public void setImg_path( String img_path ) {
        this.img_path = img_path;
    }

    @Override
    public Integer getCoinType( ) {
        return CoinType.ERC20.getCoinType( );
    }

    @Override
    public String toString( ) {
        return "{ keyId = " + getKeyId( ) + ", name=" + getName( ) + ", hide=" + getHide( ) + ", contract=" + getContract( ) + ", symbol=" + getSymbol( ) + ", imgPath=" + getImg_path( ) + " }";
    }
}
