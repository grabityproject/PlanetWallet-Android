package io.grabity.planetwallet.VO.MainItems;

public class ERC20 extends ETH implements MainItem {

    private String name;
    private String symbol;
    private String balance;
    private String address;
    private String decimal;

    private String price;

    private int iconRes;

    public ERC20( ) {

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

    @Override
    public CoinType getCoinType( ) {
        return CoinType.ERC20;
    }
}
