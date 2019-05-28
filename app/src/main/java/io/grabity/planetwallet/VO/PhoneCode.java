package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class PhoneCode implements Serializable {

    String country;
    String countryCode;


    public PhoneCode( String country, String countryCode ) {
        this.country = country;
        this.countryCode = countryCode;
    }

    public String getCountry( ) {
        return country;
    }

    public void setCountry( String country ) {
        this.country = country;
    }

    public String getCountryCode( ) {
        return countryCode;
    }

    public void setCountryCode( String countryCode ) {
        this.countryCode = countryCode;
    }
}
