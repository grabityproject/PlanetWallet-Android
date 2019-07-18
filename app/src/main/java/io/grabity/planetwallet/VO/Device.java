package io.grabity.planetwallet.VO;

public class Device {

    String device_token;
    String device_key;

    public Device( ) {
    }

    public Device( String device_token ) {
        this.device_token = device_token;
    }

    public String getDevice_token( ) {
        return device_token;
    }

    public void setDevice_token( String device_token ) {
        this.device_token = device_token;
    }

    public String getDevice_key( ) {
        return device_key;
    }

    public void setDevice_key( String device_key ) {
        this.device_key = device_key;
    }
}
