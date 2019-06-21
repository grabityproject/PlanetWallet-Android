package io.grabity.planetwallet.VO;

public class KeyPair {

    String keyId;
    String value;
    String master;

    public KeyPair( ) {
    }

    public String getKeyId( ) {
        return keyId;
    }

    public void setKeyId( String keyId ) {
        this.keyId = keyId;
    }

    public String getValue( ) {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getMaster( ) {
        return master;
    }

    public void setMaster( String master ) {
        this.master = master;
    }
}
