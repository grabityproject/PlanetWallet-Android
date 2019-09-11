package io.grabity.planetwallet.VO;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.wallet.transaction.UTXO;

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
    String status;
    String to_planet;
    String from_planet;
    String created_at;
    String updated_at;
    String decimals;
    String actualFee;
    String explorer;
    String url;

    ArrayList< UTXO > utxos;

    String serializeTx;

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

    public ArrayList< UTXO > getUtxos( ) {
        return utxos;
    }

    public void setUtxos( ArrayList< UTXO > utxos ) {
        this.utxos = utxos;
    }

    public String getActualFee( ) {
        return actualFee;
    }

    public void setActualFee( String actualFee ) {
        this.actualFee = actualFee;
    }

    public String getExplorer( ) {
        return explorer;
    }

    public void setExplorer( String explorer ) {
        this.explorer = explorer;
    }

    public String getUrl( ) {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    @NonNull
    @Override
    public String toString( ) {
        StringBuilder builder = new StringBuilder( );

        builder.append( "to : " );
        builder.append( getTo( ) );
        builder.append( ", " );

        builder.append( "from : " );
        builder.append( getFrom( ) );
        builder.append( ", " );

        builder.append( "amount : " );
        builder.append( getAmount( ) );
        builder.append( ", " );

        builder.append( "fee : " );
        builder.append( getFee( ) );
        builder.append( ", " );

        builder.append( "gasPrice : " );
        builder.append( getGasPrice( ) );
        builder.append( ", " );

        builder.append( "getGasLimit : " );
        builder.append( getGasLimit( ) );
        builder.append( ", " );

        builder.append( "getTo_planet : " );
        builder.append( getTo_planet( ) );
        builder.append( ", " );

        builder.append( "getFrom_planet : " );
        builder.append( getFrom_planet( ) );
        builder.append( ", " );

        return builder.toString( );
    }
}
