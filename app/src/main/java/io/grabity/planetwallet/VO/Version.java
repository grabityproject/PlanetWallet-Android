package io.grabity.planetwallet.VO;

public class Version {

    String platform;
    String version;
    String url;
    String force_update;

    public Version( ) {
    }

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

    public String getForce_update( ) {
        if ( force_update == null ) return "N";
        return force_update;
    }

    public void setForce_update( String force_update ) {
        this.force_update = force_update;
    }

}
