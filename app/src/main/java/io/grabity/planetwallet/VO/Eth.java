package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Eth implements Serializable {

        /**
         * 임시로 필요한 부분만 작성
         */
    int icon;
    String name;

    boolean isCheck;


    public Eth( ){

    }

    public Eth( int icon , String name , boolean addCheck ){
        this.icon = icon;
        this.name = name;
        this.isCheck = addCheck;
    }

    public int getIcon ( ) {
        return icon;
    }

    public void setIcon ( int icon ) {
        this.icon = icon;
    }

    public String getName ( ) {
        return name;
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public boolean isCheck ( ) {
        return isCheck;
    }

    public void setCheck ( boolean check ) {
        this.isCheck = check;
    }
}
