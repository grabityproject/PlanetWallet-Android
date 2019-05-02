package io.grabity.planetwallet.MiniFramework.networktask;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

public class Post extends AbstractNetworkTask {

    private NetworkInterface in;
    private int requestCode;
    private int resultCode;
    private HttpClient httpClient;
    private String token = null;

    public Post( NetworkInterface in ) {
        // TODO Auto-generated constructor stub
        this.httpClient = new DefaultHttpClient( );
        this.in = in;
    }

    public Post setToken( String token ) {
        this.token = token;
        return this;
    }

    public void action( String url, int requestCode, int resultCode, Object data ) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        new PostTask( url, false, httpClient, in, data ).execute( );
    }

    public void actionRaw( String url, int requestCode, int resultCode, Object data ) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        new PostTask( url, true, httpClient, in, data ).execute( );
    }

    protected class PostTask extends AsyncTask< Void, Void, String[] > {

        private long netWorkTime = 0L;
        private String url;
        private NetworkInterface in;
        private Object data;
        private HttpClient httpClient;
        private boolean isRaw;

        public PostTask( String url, HttpClient httpClient, NetworkInterface in, Object data ) {
            // TODO Auto-generated constructor stub
            this.url = url;
            this.in = in;
            this.data = data;
            this.httpClient = httpClient;
            this.isRaw = false;
        }

        public PostTask( String url, boolean isRaw, HttpClient httpClient, NetworkInterface in, Object data ) {
            // TODO Auto-generated constructor stub
            this.url = url;
            this.in = in;
            this.data = data;
            this.httpClient = httpClient;
            this.isRaw = isRaw;
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
                HttpPost httpPost;

                httpPost = new HttpPost( this.url );

                if ( token != null ) {
                    httpPost.setHeader( "Authorization", "Bearer " + token );
                }
                if ( data != null ) {

                    if ( isRaw ) {
                        if ( data.getClass( ).equals( String.class ) ) {
                            httpPost.setEntity( new StringEntity( ( String ) data ) );
                            httpPost.addHeader( "Content-type", "application/json" );
                        } else {
                            Gson gson = new Gson( );
                            httpPost.setEntity( new StringEntity( gson.toJson( data ) ) );
                            httpPost.addHeader( "Content-type", "application/json" );
                        }

                    } else if ( data.getClass( ).equals( HashMap.class ) ) {

                        HashMap map = ( HashMap ) data;
                        Set keySet = map.keySet( );
                        List< NameValuePair > nameValuePair = new ArrayList<>( );

                        for ( Iterator iterator = keySet.iterator( ); iterator.hasNext( ); ) {
                            String key = ( String ) iterator.next( );
                            String value = ( String ) map.get( key );
                            NameValuePair nameValue = new BasicNameValuePair( key, value );
                            nameValuePair.add( nameValue );
                        }

                        httpPost.setEntity( new UrlEncodedFormEntity( nameValuePair, "UTF-8" ) );

                    } else if ( data.getClass( ).equals( String.class ) ) {

                        try {
                            JSONObject map = new JSONObject( ( String ) data );

                            Iterator< String > keySet = map.keys( );
                            List< NameValuePair > nameValuePair = new ArrayList<>( );

                            for ( Iterator iterator = keySet; iterator.hasNext( ); ) {
                                String key = ( String ) iterator.next( );
                                String value = ( String ) map.get( key );
                                NameValuePair nameValue = new BasicNameValuePair( key, value );
                                nameValuePair.add( nameValue );
                            }
                            httpPost.setEntity( new UrlEncodedFormEntity( nameValuePair, "UTF-8" ) );

                        } catch ( JSONException e ) {

                        }

                    } else {

                        List< NameValuePair > nameValuePair = VOToMap( this.data );
                        HttpEntity entity = new UrlEncodedFormEntity( nameValuePair, "UTF-8" );
                        httpPost.setEntity( entity );
                    }
                }

                HttpResponse response = httpClient.execute( httpPost );

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
            }
        }

        @Override
        protected void onPostExecute( String[] result ) {
            // TODO Auto-generated method stub
            super.onPostExecute( result );
            this.netWorkTime = System.currentTimeMillis( ) - this.netWorkTime;
            Log.i( "NetworkTask[POST]",
                    "\n < ---------HTTP Post Method---------> \n" +
                            "<---------StatusCode :" + result[ 0 ] + "--------->\n" +
                            "< ---------Time :" + this.netWorkTime + "ms--------->" );

            if ( Boolean.parseBoolean( result[ 2 ] ) ) {
                in.onReceive( true, requestCode, resultCode, Integer.parseInt( result[ 0 ] ), result[ 1 ] );
            } else {
                in.onReceive( false, requestCode, resultCode, Integer.parseInt( result[ 0 ] ), result[ 1 ] );
            }
        }
    }
}
