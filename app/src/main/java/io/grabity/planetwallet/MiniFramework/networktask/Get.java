package io.grabity.planetwallet.MiniFramework.networktask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class Get extends AbstractNetworkTask {

    private NetworkInterface in;
    private int requestCode;
    private int resultCode;
    private HttpClient httpClient;
    private String token = null;

    private String deviceKey = null;

    public Get( NetworkInterface in ) {
        this.httpClient = new DefaultHttpClient( );
        this.in = in;
    }

    public Get setToken( String token ) {
        this.token = token;
        return this;
    }

    public Get setDeviceKey( String deviceKey ) {
        this.deviceKey = deviceKey;
        return this;
    }

    public void action( String url, int requestCode, int resultCode, Object data ) {
        this.execute( url, requestCode, resultCode, data );
    }


    public AsyncTask< Void, Void, String[] > execute( String url, int requestCode, int resultCode, Object data ) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        return new GetTask( url, httpClient, in, data ).execute( );
    }

    protected class GetTask extends AsyncTask< Void, Void, String[] > {

        private long netWorkTime = 0L;
        private String url;
        private NetworkInterface in;
        private Object data;
        private HttpClient httpClient;

        public GetTask( String url, HttpClient httpClient, NetworkInterface in, Object data ) {
            this.url = url;
            this.in = in;
            this.data = data;
            this.httpClient = httpClient;
        }

        @Override
        protected void onPreExecute( ) {
            super.onPreExecute( );
            this.netWorkTime = System.currentTimeMillis( );
        }

        @Override
        protected String[] doInBackground( Void... arg0 ) {
            try {
                if ( data != null ) {

                    if ( data.getClass( ).equals( String.class ) ) {
                        this.url += StringToParameter( ( String ) data );
                    } else {
                        this.url += "?" + URLEncodedUtils.format( VOToMap( this.data ), "UTF-8" );
                    }

                }
                HttpGet httpGet = new HttpGet( this.url );
                httpGet.setHeader( "locale", Locale.getDefault( ).getLanguage( ) );

                if ( token != null ) {
                    httpGet.setHeader( "Authorization", "Bearer " + token );
                }

                if ( deviceKey != null ) {
                    httpGet.setHeader( "device-key", deviceKey );
                }

                HttpResponse response = httpClient.execute( httpGet );
                return new String[]{ String.valueOf( response.getStatusLine( ).getStatusCode( ) ), EntityUtils.toString( response.getEntity( ) ), "true" };

            } catch ( ClientProtocolException e ) {
                return new String[]{ "-1", "ClientProtocolException", "false" };
            } catch ( IOException e ) {
                return new String[]{ "-2", "IO Exception", "false" };
            } catch ( IllegalStateException e ) {
                return new String[]{ "-3", "IllegalStateException", "false" };
            }

        }

        @Override
        protected void onProgressUpdate( Void... values ) {
            super.onProgressUpdate( values );
        }

        @Override
        protected void onPostExecute( String[] result ) {
            super.onPostExecute( result );
            this.netWorkTime = System.currentTimeMillis( ) - this.netWorkTime;
            Log.i( "NetworkTask[GET]",
                    "<--------- HTTP Get Method---------> \n" +
                            "<--------- StatusCode : " + result[ 0 ] + "--------->\n" +
                            "<--------- Time : " + this.netWorkTime + " ms--------->" );
            if( in != null )
                in.onReceive( !Boolean.parseBoolean( result[ 2 ] ), requestCode, resultCode, Integer.parseInt( result[ 0 ] ), result[ 1 ] );
        }
    }
}
