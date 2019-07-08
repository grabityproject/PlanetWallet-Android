package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Transfer implements Serializable {

    //transfer planet class

    private String toAddress;
    private String toName;
    private String toBalance;
    private String Choice;
    private String fee;


    public Transfer( ) {

    }

    public String getToAddress( ) {
        return toAddress;
    }

    public void setToAddress( String toAddress ) {
        this.toAddress = toAddress;
    }

    public String getToName( ) {
        return toName;
    }

    public void setToName( String toName ) {
        this.toName = toName;
    }

    public String getToBalance( ) {
        return toBalance;
    }

    public void setToBalance( String toBalance ) {
        this.toBalance = toBalance;
    }

    public String getChoice( ) {
        return Choice;
    }

    public void setChoice( String choice ) {
        this.Choice = choice;
    }

    public String getFee( ) {
        return fee;
    }

    public void setFee( String fee ) {
        this.fee = fee;
    }
}
