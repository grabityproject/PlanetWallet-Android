package io.grabity.planetwallet.VO;

public class Version {

    String platform;
    String version;
    String url;

    public String getPlatform( ) {
        return platform;
    }

    public void setPlatform( String platform ) {
        this.platform = platform;
    }

    public String getVersion( ) {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }

    public String getUrl( ) {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }
}
