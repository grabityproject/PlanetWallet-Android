package io.grabity.planetwallet.VO.MainItems;

import java.io.Serializable;

public class BTC implements MainItem, Serializable {

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
    public Integer getCoinType( ) {
        return 0;
    }
}
