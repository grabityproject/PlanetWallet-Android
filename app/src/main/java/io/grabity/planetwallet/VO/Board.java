package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Board implements Serializable {


    /**
     * 임시
     */
    String title;
    String date;

    public Board( String title, String date ) {
        this.title = title;
        this.date = date;
    }

    public String getTitle( ) {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDate( ) {
        return date;
    }

    public void setDate( String date ) {
        this.date = date;
    }
}
