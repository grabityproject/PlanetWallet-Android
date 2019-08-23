package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class Tx implements Serializable {

    String _id;
    String keyId;
    String type;
    String tx_id;
    String contract;
    String to;
    String from;
    String nonce;
    String amount;
    String gasPrice;
    String gasLimit;
    String fee;
    String coin;
    String symbol;
    String rawTransaction;
    String utxos;
    String status;
    String to_planet;
    String from_planet;
    String created_at;
    String updated_at;
    String decimals;


    public String getKeyId( ) {
        return keyId;
    }

    public void setKeyId( String keyId ) {
        this.keyId = keyId;
    }

    public String getType( ) {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
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


    public String getSymbol( ) {
        return symbol;
    }

    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }

    public String get_id( ) {
        return _id;
    }

    public void set_id( String _id ) {
        this._id = _id;
    }

    public String getTx_id( ) {
        return tx_id;
    }

    public void setTx_id( String tx_id ) {
        this.tx_id = tx_id;
    }

    public String getContract( ) {
        return contract;
    }

    public void setContract( String contract ) {
        this.contract = contract;
    }

    public String getTo( ) {
        return to;
    }

    public void setTo( String to ) {
        this.to = to;
    }

    public String getFrom( ) {
        return from;
    }

    public void setFrom( String from ) {
        this.from = from;
    }

    public String getNonce( ) {
        return nonce;
    }

    public void setNonce( String nonce ) {
        this.nonce = nonce;
    }

    public String getGasPrice( ) {
        return gasPrice;
    }

    public void setGasPrice( String gasPrice ) {
        this.gasPrice = gasPrice;
    }

    public String getGasLimit( ) {
        return gasLimit;
    }

    public void setGasLimit( String gasLimit ) {
        this.gasLimit = gasLimit;
    }

    public String getCoin( ) {
        return coin;
    }

    public void setCoin( String coin ) {
        this.coin = coin;
    }

    public String getRawTransaction( ) {
        return rawTransaction;
    }

    public void setRawTransaction( String rawTransaction ) {
        this.rawTransaction = rawTransaction;
    }

    public String getStatus( ) {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public String getTo_planet( ) {
        return to_planet;
    }

    public void setTo_planet( String to_planet ) {
        this.to_planet = to_planet;
    }

    public String getFrom_planet( ) {
        return from_planet;
    }

    public void setFrom_planet( String from_planet ) {
        this.from_planet = from_planet;
    }

    public String getCreated_at( ) {
        return created_at;
    }

    public void setCreated_at( String created_at ) {
        this.created_at = created_at;
    }

    public String getUpdated_at( ) {
        return updated_at;
    }

    public void setUpdated_at( String updated_at ) {
        this.updated_at = updated_at;
    }

    public String getDecimals( ) {
        return decimals;
    }

    public void setDecimals( String decimals ) {
        this.decimals = decimals;
    }

    public String getUtxos( ) {
        return utxos;
    }

    public void setUtxos( String utxos ) {
        this.utxos = utxos;
    }
}
