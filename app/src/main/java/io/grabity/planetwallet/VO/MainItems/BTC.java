package io.grabity.planetwallet.VO.MainItems;

import java.io.Serializable;

import io.grabity.planetwallet.R;

public class BTC implements MainItem, Serializable {

    private String planetName;
    private String balance;
    private String date;

    private int iconRes = R.drawable.icon_bit;

    public BTC( ) {
    }


    public int getIconRes( ) {
        return iconRes;
    }

    public void setIconRes( int iconRes ) {
        this.iconRes = iconRes;
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
