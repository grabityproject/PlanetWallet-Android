package io.grabity.planetwallet.VO;

import java.math.BigDecimal;

public class ETHGasProvider {

    public final static BigDecimal ETH_DEFAULT_GAS_LIMIT = BigDecimal.valueOf( 21000 );
    public final static String ETH_DEFAULT_GAS_GWEI = "20";
    public final static String ETH_DEFAULT_FEE = "0.00042";

    public final static BigDecimal ERC_DEFAULT_GAS_LIMIT = BigDecimal.valueOf( 100000 );
    public final static String ERC_DEFAULT_GAS_GWEI = "10";
    public final static String ERC_DEFAULT_FEE = "0.001";


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
