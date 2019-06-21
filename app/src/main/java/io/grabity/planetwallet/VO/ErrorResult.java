package io.grabity.planetwallet.VO;

public class ErrorResult {

    Integer errorCode;
    String errorMsg;

    public ErrorResult( ) {
    }

    public Integer getErrorCode( ) {
        return errorCode;
    }

    public void setErrorCode( Integer errorCode ) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg( ) {
        return errorMsg;
    }

    public void setErrorMsg( String errorMsg ) {
        this.errorMsg = errorMsg;
    }
}
