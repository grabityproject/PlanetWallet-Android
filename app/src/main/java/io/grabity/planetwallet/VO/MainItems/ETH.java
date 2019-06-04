package io.grabity.planetwallet.VO.MainItems;

import io.grabity.planetwallet.R;

public class ETH implements MainItem {

    private String name;
    private String symbol;
    private String balance;
    private String address;
    private String decimal;

    private String price;

    private int iconRes = R.drawable.icon_eth;

    boolean isCheck = false;

    public ETH( ) {
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

    public String getBalance( ) {
        return balance;
    }

    public void setBalance( String balance ) {
        this.balance = balance;
    }

    public String getAddress( ) {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getDecimal( ) {
        return decimal;
    }

    public void setDecimal( String decimal ) {
        this.decimal = decimal;
    }

    public String getPrice( ) {
        return price;
    }

    public void setPrice( String price ) {
        this.price = price;
    }

    public int getIconRes( ) {
        return iconRes;
    }

    public void setIconRes( int iconRes ) {
        this.iconRes = iconRes;
    }

    public boolean isCheck( ) {
        return isCheck;
    }

    public void setCheck( boolean check ) {
        isCheck = check;
    }

    @Override
    public CoinType getCoinType( ) {
        return CoinType.ETH;
    }
}