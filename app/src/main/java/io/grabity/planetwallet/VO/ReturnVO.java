package io.grabity.planetwallet.VO;

import java.io.Serializable;

public class ReturnVO implements Serializable {
    private static final long serialVersionUID = 1002L;

    boolean success;
    Object result;

    public boolean isSuccess( ) {
        return success;
    }

    public void setSuccess( boolean success ) {
        this.success = success;
    }

    public Object getResult( ) {
        return result;
    }

    public void setResult( Object result ) {
        this.result = result;
    }
}
