package io.grabity.planetwallet.MiniFramework.networktask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import io.grabity.planetwallet.MiniFramework.utils.PLog;

public abstract class AbstractNetworkTask {

    public String StringToParameter( String jsonString ) {
        String result = "?";

        if ( jsonString != null ) {

            try {
                JSONObject map = new JSONObject( jsonString );

                Iterator< String > keySet = map.keys( );

                for ( Iterator iterator = keySet; iterator.hasNext( ); ) {

                    String key = ( String ) iterator.next( );
                    String value = ( String ) map.get( key );
                    result += URLEncoder.encode( key, "UTF-8" ) + "=" + URLEncoder.encode( String.valueOf( value ), "UTF-8" );
                    if ( iterator.hasNext( ) )
                        result += "&";
                }

            } catch ( JSONException e ) {
            } catch ( UnsupportedEncodingException e ) {
            }

        }
        return result;
    }

    public ArrayList< NameValuePair > VOToMap( Object obj ) {
        if ( obj != null ) {
            ArrayList< NameValuePair > result = new ArrayList< NameValuePair >( );
            try {
                Field[] fields = obj.getClass( ).getDeclaredFields( );
                for ( int i = 0; i <= fields.length - 1; i++ ) {
                    fields[ i ].setAccessible( true );
                    if ( !fields[ i ].getName( ).equals( "serialVersionUID" ) ) {
                        if ( fields[ i ].get( obj ) != null ) {
                            if ( ArrayList.class.equals( fields[ i ].getType( ) ) ) {

                                for ( int j = 0; j < ( ( ArrayList ) fields[ i ].get( obj ) ).size( ); j++ ) {
                                    if ( ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( ArrayList.class ) ) {
                                        for ( int k = 0; k < ( ( ArrayList ) ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ) ).size( ); k++ ) {
                                            result.add( new BasicNameValuePair( fields[ i ].getName( ) + "[" + j + "][]", String.valueOf( ( ( ArrayList ) ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ) ).get( k ) ) ) );
                                        }
                                    } else {

                                        if ( ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( String.class ) ||
                                                ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( Integer.class ) ||
                                                ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( Long.class ) ||
                                                ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( Double.class ) ||
                                                ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( Float.class ) ||
                                                ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( int.class ) ||
                                                ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( long.class ) ||
                                                ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( double.class ) ||
                                                ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ).getClass( ).equals( float.class ) ) {
                                            result.add( new BasicNameValuePair( fields[ i ].getName( ) + "[]", String.valueOf( ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ) ) ) );
                                            PLog.e( fields[ i ].getName( ) + "[]", String.valueOf( ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ) ) );
                                        } else {
                                            ArrayList< NameValuePair > inner = VOToMap( ( ( ArrayList ) fields[ i ].get( obj ) ).get( j ) );
                                            for ( int k = 0; k < inner.size( ); k++ ) {
                                                result.add( new BasicNameValuePair( fields[ i ].getName( ) + "[" + j + "][" + inner.get( k ).getName( ) + "]", inner.get( k ).getValue( ) ) );

                                            }
                                        }
                                    }
                                }
                            } else {
                                result.add( new BasicNameValuePair( fields[ i ].getName( ), String.valueOf( fields[ i ].get( obj ) ) ) );

                            }

                        }
                    }
                }
                return result;
            } catch ( IllegalArgumentException e ) {
                return null;
            } catch ( IllegalAccessException e ) {
                return null;
            }
        } else return null;
    }
}
