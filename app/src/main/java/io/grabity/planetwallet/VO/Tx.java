package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Tx implements Serializable {

    //todo 임시
    String toAddress;
    String fromAddress;
    String amount;
    String fee;
    String date;
    String symbol;
    String txId;

    public String getToAddress( ) {
        return toAddress;
    }

    public void setToAddress( String toAddress ) {
        this.toAddress = toAddress;
    }

    public String getFromAddress( ) {
        return fromAddress;
    }

    public void setFromAddress( String fromAddress ) {
        this.fromAddress = fromAddress;
    }

    public String getAmount( ) {
        return amount;
    }

    public void setAmount( String amount ) {
        this.amount = amount;
    }

    public String getFee( ) {
        return fee;
    }

    public void setFee( String fee ) {
        this.fee = fee;
    }

    public String getDate( ) {
        return date;
    }

    public void setDate( String date ) {
        this.date = date;
    }

    public String getSymbol( ) {
        return symbol;
    }

    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }

    public String getTxId( ) {
        return txId;
    }

    public void setTxId( String txId ) {
        this.txId = txId;
    }
}
