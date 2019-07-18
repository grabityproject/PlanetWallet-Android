package io.grabity.planetwallet.VO;

import java.math.BigDecimal;

public class ETHGasPrice {

    public static BigDecimal DEFALUT_GAS_LIMIT = BigDecimal.valueOf( 21000 );
    public static String DEFALUT_GAS_GWEI = "20.0";

    private String safeLow;
    private String standard;
    private String fast;
    private String fastest;

    public String getSafeLow( ) {
        return safeLow;
    }

    public void setSafeLow( String safeLow ) {
        this.safeLow = safeLow;
    }

    public String getStandard( ) {
        return standard;
    }

    public void setStandard( String standard ) {
        this.standard = standard;
    }

    public String getFast( ) {
        return fast;
    }

    public void setFast( String fast ) {
        this.fast = fast;
    }

    public String getFastest( ) {
        return fastest;
    }

    public void setFastest( String fastest ) {
        this.fastest = fastest;
    }
}
