package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Search implements Serializable {

    Integer _id;
    String keyId;
    String name;
    String address;
    String symbol;

    public String getKeyId( ) {
        return keyId;
    }

    public void setKeyId( String keyId ) {
        this.keyId = keyId;
    }

    public Integer get_id( ) {
        return _id;
    }

    public void set_id( Integer _id ) {
        this._id = _id;
    }

    public String getName( ) {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getAddress( ) {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getSymbol( ) {
        return symbol;
    }

    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }
}
