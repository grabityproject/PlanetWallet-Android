package io.grabity.planetwallet.VO.MainItems;


import java.io.Serializable;

//Todo api 통신클래스 추후 수정
public class ERC20Token implements Serializable {


    String contract_address;
    String symbol;
    String name;
    String decimal;
    String img_path;
    String volume;

    boolean isCheck = false;

    public String getContract_address( ) {
        return contract_address;
    }

    public void setContract_address( String contract_address ) {
        this.contract_address = contract_address;
    }

    public String getSymbol( ) {
        return symbol;
    }

    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }

    public String getName( ) {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDecimal( ) {
        return decimal;
    }

    public void setDecimal( String decimal ) {
        this.decimal = decimal;
    }

    public String getImg_path( ) {
        return img_path;
    }

    public void setImg_path( String img_path ) {
        this.img_path = img_path;
    }

    public String getVolume( ) {
        return volume;
    }

    public void setVolume( String volume ) {
        this.volume = volume;
    }

    public boolean isCheck( ) {
        return isCheck;
    }

    public void setCheck( boolean check ) {
        isCheck = check;
    }
}
