package io.grabity.planetwallet.MiniFramework.utils;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class Route {

    public static String URL( Object... segments ) {

        String URL = "https://test.planetwallet.io";
        if ( segments != null ) {
            for ( Object segment : segments ) {
                if ( segment != null )
                    URL = URL + "/" + segment.toString( );
            }
        }
        return URL;
    }

}
