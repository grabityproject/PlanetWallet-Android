package io.grabity.planetwallet.MiniFramework.networktask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class Put extends AbstractNetworkTask {

    private NetworkInterface in;
    private int requestCode;
    private int resultCode;
    private Object data;
    private HttpClient httpClient;
    private String token = null;

    public Put( HttpClient httpClient, NetworkInterface in ) {
        this.httpClient = httpClient;
        this.in = in;
    }

    public Put( NetworkInterface in ) {
        this.httpClient = new DefaultHttpClient( );
        this.in = in;
    }

    public Put setToken( String token ) {
        this.token = token;
        return this;
    }

    public void action( String url, int requestCode, int resultCode, Object data ) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
        new GetTask( url, httpClient, in, data ).execute( );
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
                HttpPut httpPut;

                if ( data != null ) {

                    if ( data.getClass( ).equals( String.class ) ) {
                        this.url += StringToParameter( ( String ) data );
                    } else {
                        this.url += "?" + URLEncodedUtils.format( VOToMap( this.data ), "UTF-8" );
                    }

                }

                httpPut = new HttpPut( this.url );
                httpPut.setHeader( "locale", Locale.getDefault().getLanguage() );

                if ( token != null ) {
                    httpPut.setHeader( "Authorization", "Bearer " + token );
                }

                HttpResponse response = httpClient.execute( httpPut );

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
        protected void onPostExecute( String[] result ) {
            super.onPostExecute( result );
            this.netWorkTime = System.currentTimeMillis( ) - this.netWorkTime;
            Log.i( "NetworkTask[PUT]",
                    "<--------- HTTP Put Method---------> \n" +
                            "<--------- StatusCode : " + result[ 0 ] + "--------->\n" +
                            "<--------- Time : " + this.netWorkTime + " ms--------->" );
            in.onReceive( !Boolean.parseBoolean( result[ 2 ] ), requestCode, resultCode, Integer.parseInt( result[ 0 ] ), result[ 1 ] );
        }
    }
}
