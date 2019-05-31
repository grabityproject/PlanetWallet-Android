package io.grabity.planetwallet.VO.MainItems;

public class BTC implements MainItem {

    private String planetName;
    private String balance;
    private String date;

    public BTC( ) {
    }

    public String getPlanetName( ) {
        return planetName;
    }

    public void setPlanetName( String planetName ) {
        this.planetName = planetName;
    }

    public String getBalance( ) {
        return balance;
    }

    public void setBalance( String balance ) {
        this.balance = balance;
    }

    public String getDate( ) {
        return date;
    }

    public void setDate( String date ) {
        this.date = date;
    }

    @Override
    public CoinType getCoinType( ) {
        return CoinType.BTC;
    }
}
