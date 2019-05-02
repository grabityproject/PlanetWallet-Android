package io.grabity.planetwallet.MiniFramework.utils;

import android.util.Log;

public class PLog {

    private static String TAG = "[PSE framework]";

    public PLog( Object message ) {
        if ( message == null )
            Log.e( TAG, "Null Message" );
        else {
            String stringMessage = String.valueOf( message );
            Log.e( TAG, stringMessage );
        }
    }

    public static void setTAG( String TAG ) {
        PLog.TAG = "[PSE framework] / " + TAG;
    }

    public static void v( Object message ) {
        if ( message == null )
            Log.v( TAG, "Null Message" );
        else {
            String stringMessage = String.valueOf( message );
            Log.v( TAG, stringMessage );
        }
    }

    public static void e( Object message ) {
        if ( message == null )
            Log.v( TAG, "Null Message" );
        else {
            String stringMessage = String.valueOf( message );
            Log.e( TAG, stringMessage );
        }
    }


    public static void e( Object... messages ) {
        if ( messages == null )
            Log.v( TAG, "Null Message" );
        else {
            for ( Object message : messages ) {
                String stringMessage = "Null Message";
                if ( message != null ) stringMessage = String.valueOf( message );
                Log.e( TAG, stringMessage );
            }
        }
    }

    public static void e( Class< ? > T, Object message ) {
        if ( message == null )
            Log.v( TAG, "Null Message" );
        else {
            String stringMessage = String.valueOf( T.getSimpleName( ) + " : " + message );
            Log.e( TAG, stringMessage );
        }
    }

    public static void w( Object message ) {
        if ( message == null )
            Log.v( TAG, "Null Message" );
        else {
            String stringMessage = String.valueOf( message );
            Log.w( TAG, stringMessage );
        }
    }

}
