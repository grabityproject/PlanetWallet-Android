package io.grabity.planetwallet.MiniFramework.utils;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by. JcobPark on 2018. 08. 29
 */

public class SetTokenImageDownloader extends BaseImageDownloader {

    public SetTokenImageDownloader( Context context ) {
        super( context );
    }

    public SetTokenImageDownloader( Context context, int connectTimeout, int readTimeout ) {
        super( context, connectTimeout, readTimeout );
    }

    @Override
    protected HttpURLConnection createConnection( String url, Object extra ) throws IOException {
        HttpURLConnection conn = super.createConnection( url, extra );
        Map< String, String > headers = ( Map< String, String > ) extra;
        if ( headers != null ) {
            for ( Map.Entry< String, String > header : headers.entrySet( ) ) {
                conn.setRequestProperty( header.getKey( ), header.getValue( ) );
            }
        }
        return conn;
    }
}