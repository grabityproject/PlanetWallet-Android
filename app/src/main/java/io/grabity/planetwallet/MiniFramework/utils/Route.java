package io.grabity.planetwallet.MiniFramework.utils;

import io.grabity.planetwallet.Common.commonset.C;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class Route {

    public static String URL( Object... segments ) {

        String URL = C.DEBUG ? "https://test.planetwallet.io" : "https://api.planetwallet.io";
        if ( segments != null ) {
            for ( Object segment : segments ) {
                if ( segment != null )
                    URL = URL + "/" + segment.toString( );
            }
        }
        return URL;
    }

}
