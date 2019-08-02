package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Transfer implements Serializable {

    //transfer planet class

    private String toAddress;
    private String toName;
    private String toBalance;
    private String Choice;
    private String fee;
    private String serializeTx;
    private String txHash;



    public Transfer( ) {

    }

    public Transfer( String serializeTx ) {
        this.serializeTx = serializeTx;
    }

    public String getTxHash( ) {
        return txHash;
    }

    public void setTxHash( String txHash ) {
        this.txHash = txHash;
    }


    public String getSerializeTx( ) {
        return serializeTx;
    }

    public void setSerializeTx( String serializeTx ) {
        this.serializeTx = serializeTx;
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
