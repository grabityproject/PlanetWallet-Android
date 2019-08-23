package io.grabity.planetwallet.MiniFramework.wallet.transaction;

public class UTXO {

    Double block_height = 0d;
    String tx_hash;
    String value;
    String script;
    Double tx_output_n;

    String signedScript;

    public UTXO( ) {
    }

    public Double getBlock_height( ) {
        return block_height;
    }

    public void setBlock_height( Double block_height ) {
        this.block_height = block_height;
    }

    public String getTx_hash( ) {
        return tx_hash;
    }

    public void setTx_hash( String tx_hash ) {
        this.tx_hash = tx_hash;
    }

    public String getValue( ) {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getScript( ) {
        return script;
    }

    public void setScript( String script ) {
        this.script = script;
    }

    public Double getTx_output_n( ) {
        return tx_output_n;
    }

    public void setTx_output_n( Double tx_output_n ) {
        this.tx_output_n = tx_output_n;
    }

    public String getSignedScript( ) {
        return signedScript;
    }

    public void setSignedScript( String signedScript ) {
        this.signedScript = signedScript;
    }

}
