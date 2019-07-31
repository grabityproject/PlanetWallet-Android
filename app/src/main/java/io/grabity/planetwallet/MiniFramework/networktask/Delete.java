package io.grabity.planetwallet.MiniFramework.networktask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URI;
import java.util.Locale;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;


public class Delete extends AbstractNetworkTask {

    private NetworkInterface in;
    private int requestCode;
    private int resultCode;
    private HttpClient httpClient;
    private String token = null;

    public Delete( NetworkInterface in ) {
        // TODO Auto-generated constructor stub
        this.httpClient = new DefaultHttpClient( );
        this.in = in;
    }

    public Delete setToken( String token ) {
        this.token = token;
        return this;
    }

    public void action( String url, int requestCode, int resultCode, Object data ) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        new DeleteTask( url, httpClient, in, data ).execute( );
    }

    protected class DeleteTask extends AsyncTask< Void, Void, String[] > {

        private long netWorkTime = 0L;
        private String url;
        private NetworkInterface in;
        private Object data;
        private HttpClient httpClient;

        public DeleteTask( String url, HttpClient httpClient, NetworkInterface in, Object data ) {
            // TODO Auto-generated constructor stub
            this.url = url;
            this.in = in;
            this.data = data;
            this.httpClient = httpClient;
        }

        @Override
        protected void onPreExecute( ) {
            // TODO Auto-generated method stub
            super.onPreExecute( );
            this.netWorkTime = System.currentTimeMillis( );
        }

        @Override
        protected String[] doInBackground( Void... arg0 ) {
            // TODO Auto-generated method stub


            try {
                HttpEntityEnclosingRequestBase httpPatch = new HttpEntityEnclosingRequestBase( ) {
                    @Override
                    public String getMethod( ) {
                        return "DELETE";
                    }
                };

                if ( data != null ) {

                    if ( data.getClass( ).equals( String.class ) ) {
                        this.url += StringToParameter( ( String ) data );
                    } else {
                        this.url += "?" + URLEncodedUtils.format( VOToMap( this.data ), "UTF-8" );
                    }

                }

                httpPatch.setURI( URI.create( url ) );
                httpPatch.setHeader( "locale", Locale.getDefault( ).getLanguage( ) );

                if ( token != null ) {
                    httpPatch.setHeader( "Authorization", "Bearer " + token );
                }

                HttpResponse response = httpClient.execute( httpPatch );

                return new String[]{ String.valueOf( response.getStatusLine( ).getStatusCode( ) ), EntityUtils.toString( response.getEntity( ) ), "true" };

            } catch ( ClientProtocolException e ) {
                // TODO Auto-generated catch block
                return new String[]{ "-1", "ClientProtocolException", "false" };
            } catch ( IOException e ) {
                // TODO Auto-generated catch block
                return new String[]{ "-2", "IO Exception", "false" };
            } catch ( IllegalStateException e ) {
                // TODO: handle exception
                return new String[]{ "-3", "IllegalStateException", "false" };
            } catch ( IllegalArgumentException e ) {
                return new String[]{ "-4", "IllegalArgumentException", "false" };
            }

        }

        @Override
        protected void onPostExecute( String[] result ) {
            // TODO Auto-generated method stub
            super.onPostExecute( result );
            this.netWorkTime = System.currentTimeMillis( ) - this.netWorkTime;
            Log.i( "NetworkTask[DELETE]",
                    "<--------- HTTP Delete Method---------> \n" +
                            "<--------- StatusCode : " + result[ 0 ] + "--------->\n" +
                            "<--------- Time : " + this.netWorkTime + " ms--------->" );
            if ( in != null )
                in.onReceive( !Boolean.parseBoolean( result[ 2 ] ), requestCode, resultCode, Integer.parseInt( result[ 0 ] ), result[ 1 ] );
        }
    }
}
