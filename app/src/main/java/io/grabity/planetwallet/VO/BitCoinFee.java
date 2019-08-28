package io.grabity.planetwallet.VO;

public class BitCoinFee {

    private String fastestFee;
    private String halfHourFee;
    private String hourFee;

    public String getFastestFee( ) {
        return fastestFee;
    }

    public void setFastestFee( String fastestFee ) {
        this.fastestFee = fastestFee;
    }

    public String getHalfHourFee( ) {
        return halfHourFee;
    }

    public void setHalfHourFee( String halfHourFee ) {
        this.halfHourFee = halfHourFee;
    }

    public String getHourFee( ) {
        return hourFee;
    }

    public void setHourFee( String hourFee ) {
        this.hourFee = hourFee;
    }
}
