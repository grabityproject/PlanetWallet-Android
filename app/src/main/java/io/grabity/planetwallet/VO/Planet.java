package io.grabity.planetwallet.VO;

import java.io.Serializable;
import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.VO.MainItems.CoinType;
import io.grabity.planetwallet.VO.MainItems.MainItem;

public class Planet implements Serializable {

    /**
     * 필요한 부분만 작성
     */

    private CoinType coinType;
    private String address;
    private String name;

    private int iconRes;

    private ArrayList< MainItem > items;

    public Planet( ) {

    }

    public CoinType getCoinType( ) {
        return coinType;
    }

    public void setCoinType( CoinType coinType ) {
        this.coinType = coinType;
    }

    public String getAddress( ) {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getName( ) {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public ArrayList< MainItem > getItems( ) {
        if ( items == null ) items = new ArrayList<>( );
        return items;
    }

    public void setItems( ArrayList< MainItem > items ) {
        this.items = items;
    }

    public int getIconRes( ) {
        return iconRes;
    }

    public void setIconRes( int iconRes ) {
        this.iconRes = iconRes;
    }
}
