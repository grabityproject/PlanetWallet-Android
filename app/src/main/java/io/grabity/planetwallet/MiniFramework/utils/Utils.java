package io.grabity.planetwallet.MiniFramework.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    /**
     * @author PSE.inc
     */

    public static boolean isValidEmail( String email ) {
        boolean err = false;
        String regex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        Pattern p = Pattern.compile( regex );
        Matcher m = p.matcher( email );
        if ( m.matches( ) ) {
            err = true;
        }
        return err;
    }

    public static < T > T jsonToVO( String jsonString, Class< T > type ) {
        Gson gson = new Gson( );
        return gson.fromJson( jsonString, type );
    }

    public static < T > T jsonToVO( String jsonString, Class< T > type, Class subType ) {
        Object returnObject = null;
        try {
            returnObject = type.newInstance( );
            Field[] fields = type.getDeclaredFields( );
            Method[] methods = type.getDeclaredMethods( );
            returnObject = new Gson( ).fromJson( jsonString, type );

            for ( Method method : methods ) {
                method.setAccessible( true );
                if ( method.getName( ).substring( 0, 3 ).equals( "get" ) ) {
                    Object object = method.invoke( returnObject );

                    if ( object != null ) {
                        if ( ArrayList.class.equals( object.getClass( ) ) ) {

                            ArrayList re = new ArrayList( );
                            for ( Object obj : ( ArrayList ) object ) {

                                re.add( linkedMapToVO( ( LinkedTreeMap ) obj, subType ) );
                            }
                            for ( Field field : fields ) {
                                String f = method.getName( ).replace( "get", "" ).toLowerCase( );

                                if ( f.equals( field.getName( ) ) ) {
                                    Method setMethod = type.getDeclaredMethod( method.getName( ).replace( "get", "set" ), field.getType( ) );
                                    setMethod.setAccessible( true );
                                    setMethod.invoke( returnObject, re );

                                }
                            }
                        } else if ( LinkedTreeMap.class.equals( object.getClass( ) ) ) {

                            for ( Field field : fields ) {
                                String f = method.getName( ).replace( "get", "" ).toLowerCase( );

                                if ( f.equals( field.getName( ) ) ) {

                                    Method setMethod = returnObject.getClass( ).getDeclaredMethod( method.getName( ).replace( "get", "set" ), Object.class );
                                    setMethod.setAccessible( true );
                                    setMethod.invoke( returnObject, linkedMapToVO( ( LinkedTreeMap ) object, subType ) );

                                }
                            }
                        }
                    }
                }
            }
            T target = ( T ) returnObject;
            return target;
        } catch ( InstantiationException e ) {
            T target = ( T ) returnObject;
            PLog.e( "InstantiationException" );
            return target;
        } catch ( IllegalAccessException e ) {
            T target = ( T ) returnObject;
            PLog.e( "IllegalAccessException" );
            return target;
        } catch ( InvocationTargetException e ) {
            PLog.e( "InvocationTargetException" );
            T target = ( T ) returnObject;
            return target;
        } catch ( NoSuchMethodException e ) {
            PLog.e( "NoSuchMethodException" );
            T target = ( T ) returnObject;
            return target;
        } catch ( JsonSyntaxException e ) {
            T target = ( T ) returnObject;
            return target;
        }
    }

    public static < T > T linkedMapToVO( LinkedTreeMap map, Class< T > type ) {

        Object returnObject = null;
        Field[] fields = type.getDeclaredFields( );
        Method[] methods = type.getDeclaredMethods( );

        try {
            returnObject = type.newInstance( );

            for ( Field field : fields ) {
                field.setAccessible( true );
                if ( map.containsKey( field.getName( ) ) ) {

                    for ( Method method : methods ) {
                        method.setAccessible( true );

                        if ( method.getName( ).equals( "set" + field.getName( ).substring( 0, 1 ).toUpperCase( ) + field.getName( ).substring( 1, field.getName( ).length( ) ) ) ) {

                            if ( Double.class.equals( field.getType( ) ) ) {

                                method.invoke( returnObject, Double.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );

                            } else if ( Float.class.equals( field.getType( ) ) ) {

                                method.invoke( returnObject, Float.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );

                            } else if ( Long.class.equals( field.getType( ) ) ) {

                                method.invoke( returnObject, Long.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );

                            } else if ( Integer.class.equals( field.getType( ) ) ) {

                                try {
                                    method.invoke( returnObject, Integer.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );
                                } catch ( NumberFormatException e ) {
                                    method.invoke( returnObject, 0 );
                                }
                            } else if ( Boolean.class.equals( field.getType( ) ) ) {

                                method.invoke( returnObject, Boolean.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );

                            } else if ( String.class.equals( field.getType( ) ) ) {

                                method.invoke( returnObject, String.valueOf( map.get( field.getName( ) ) ) );

                            } else if ( List.class.equals( field.getType( ) ) ) {

                                PLog.e( "ClassNotFoundException List" );


                            } else if ( ArrayList.class.equals( field.getType( ) ) ) {

                                try {
                                    ParameterizedType myListType = ( ( ParameterizedType ) field.getGenericType( ) );
                                    Class< ? > itemType = ( Class< ? > ) myListType.getActualTypeArguments( )[ 0 ];

                                    ArrayList list = ( ( ArrayList ) map.get( field.getName( ) ) );
                                    ArrayList resultList = new ArrayList<>( );

                                    for ( int i = 0; i < list.size( ); i++ ) {

                                        if ( LinkedTreeMap.class.equals( list.get( i ).getClass( ) ) ) {

                                            resultList.add( linkedMapToVO( ( LinkedTreeMap ) list.get( i ), itemType ) );
                                        } else if ( String.class.equals( list.get( i ).getClass( ) ) ) {
                                            resultList.add( String.valueOf( list.get( i ) ) );
                                        }
                                    }
                                    method.invoke( returnObject, resultList );

                                } catch ( ClassCastException e ) {

                                    PLog.e( "ClassCastException List" );

                                }
                            } else {

                                try {

                                    method.invoke( returnObject, linkedMapToVO( ( LinkedTreeMap ) map.get( field.getName( ) ), Class.forName( field.getType( ).getName( ) ) ) );

                                } catch ( ClassNotFoundException e ) {

                                    PLog.e( "ClassNotFoundException" );

                                } catch ( ClassCastException e ) {
                                    PLog.e( "ClassCastException" + "[ " + field.getName( ) + " ]" );
                                }
                            }
                        }
                    }
                }
            }
            T target = ( T ) returnObject;
            return target;

        } catch ( InstantiationException e ) {
            T target = ( T ) returnObject;
            return target;
        } catch ( IllegalAccessException e ) {
            e.printStackTrace( );
            T target = ( T ) returnObject;
            return target;
        } catch ( InvocationTargetException e ) {
            e.printStackTrace( );
            T target = ( T ) returnObject;
            return target;
        }
    }

    public static Object getPreferenceData( Context context, String prefName, String key, Object defValue ) {
        if ( defValue != null && context != null ) {
            //Log.v("", "type : " + defValue.getClass());
            if ( defValue.getClass( ) == String.class ) {
                return context.getSharedPreferences( prefName, context.MODE_PRIVATE ).getString( key, ( String ) defValue );
            } else if ( defValue.getClass( ) == Integer.class ) {
                return context.getSharedPreferences( prefName, context.MODE_PRIVATE ).getInt( key, ( Integer ) defValue );
            } else if ( defValue.getClass( ) == Long.class ) {
                return context.getSharedPreferences( prefName, context.MODE_PRIVATE ).getLong( key, ( Long ) defValue );
            } else if ( defValue.getClass( ) == Boolean.class ) {
                return context.getSharedPreferences( prefName, context.MODE_PRIVATE ).getBoolean( key, ( Boolean ) defValue );
            } else if ( defValue.getClass( ) == Float.class ) {
                return context.getSharedPreferences( prefName, context.MODE_PRIVATE ).getFloat( key, ( Float ) defValue );
            }
            return defValue;
        } else return null;
    }

    public static void setPreferenceData( Context context, String prefName, String key, Object defValue ) {
        if ( defValue != null ) {
            //Log.v("", "Set type : " + defValue.getClass());
            if ( defValue.getClass( ) == String.class ) {
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).putString( key, ( String ) defValue ).commit( );
            } else if ( defValue.getClass( ) == Integer.class ) {
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).putInt( key, ( Integer ) defValue ).commit( );
            } else if ( defValue.getClass( ) == Long.class ) {
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).putLong( key, ( Long ) defValue ).commit( );
            } else if ( defValue.getClass( ) == Boolean.class ) {
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).putBoolean( key, ( Boolean ) defValue ).commit( );
            } else if ( defValue.getClass( ) == Float.class ) {
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( prefName, context.MODE_PRIVATE ).edit( ).putFloat( key, ( Float ) defValue ).commit( );
            }
        }
    }

    public static Object getPreferenceData( Context context, String key, Object defValue ) {
        if ( defValue != null && context != null ) {
            //Log.v("", "type : " + defValue.getClass());
            if ( defValue.getClass( ) == String.class ) {
                return context.getSharedPreferences( "default", context.MODE_PRIVATE ).getString( key, ( String ) defValue );
            } else if ( defValue.getClass( ) == Integer.class ) {
                return context.getSharedPreferences( "default", context.MODE_PRIVATE ).getInt( key, ( Integer ) defValue );
            } else if ( defValue.getClass( ) == Long.class ) {
                return context.getSharedPreferences( "default", context.MODE_PRIVATE ).getLong( key, ( Long ) defValue );
            } else if ( defValue.getClass( ) == Boolean.class ) {
                return context.getSharedPreferences( "default", context.MODE_PRIVATE ).getBoolean( key, ( Boolean ) defValue );
            } else if ( defValue.getClass( ) == Float.class ) {
                return context.getSharedPreferences( "default", context.MODE_PRIVATE ).getFloat( key, ( Float ) defValue );
            }
            return defValue;
        } else return null;
    }

    public static String getPreferenceData( Context context, String key ) {
        if ( context != null ) {
            //Log.v("", "type : " + defValue.getClass());
            return context.getSharedPreferences( "default", context.MODE_PRIVATE ).getString( key, ( String ) "" );
        } else return null;
    }

    public static void setPreferenceData( Context context, String key, Object value ) {
        if ( value != null ) {
            //Log.v("", "Set type : " + defValue.getClass());
            if ( value.getClass( ) == String.class ) {
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).putString( key, ( String ) value ).commit( );
            } else if ( value.getClass( ) == Integer.class ) {
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).putInt( key, ( Integer ) value ).commit( );
            } else if ( value.getClass( ) == Long.class ) {
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).putLong( key, ( Long ) value ).commit( );
            } else if ( value.getClass( ) == Boolean.class ) {
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).putBoolean( key, ( Boolean ) value ).commit( );
            } else if ( value.getClass( ) == Float.class ) {
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).remove( key ).commit( );
                context.getSharedPreferences( "default", context.MODE_PRIVATE ).edit( ).putFloat( key, ( Float ) value ).commit( );
            }
        }
    }

    public static String getWeekForInt( int num ) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols( Locale.ENGLISH );
        String[] months = dfs.getShortWeekdays( );
        if ( num >= 0 && num <= 7 ) {
            month = months[ num ];
        }
        return month;
    }

    public static String getMonthForInt( int num ) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols( Locale.ENGLISH );
        String[] months = dfs.getShortMonths( );
        if ( num >= 0 && num <= 12 ) {
            month = months[ num ];
        }
        return month;
    }

    public static void vibrate( Context context ) {
        vibrate( context, 1000 );
    }

    public static void vibrate( Context context, int duration ) {
        Vibrator vibe = ( Vibrator ) context.getSystemService( Context.VIBRATOR_SERVICE );
        vibe.vibrate( duration );
    }

    public static void shakeView( View target, Context context, int duration ) {
        ObjectAnimator animator = ObjectAnimator.ofFloat( target, "translationX", 0, 25, -25, 25, -25, 15, -15, 15, -15, 6, -6, 6, -6, 0 );
        animator.setDuration( duration );
        animator.setInterpolator( new LinearInterpolator( ) );
        animator.start( );
        vibrate( context, duration );
    }

    public static void shakeView( View target, Context context ) {
        shakeView( target, context, 600 );
    }

    public static File saveBitmapToFileCache( Bitmap bitmap, String strFilePath, String filename ) {

        File file = new File( strFilePath );

        // If no folders
        if ( !file.exists( ) ) {
            file.mkdirs( );
        }
        File fileCacheItem = new File( strFilePath + "/" + filename );
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile( );
            out = new FileOutputStream( fileCacheItem );

            bitmap.compress( Bitmap.CompressFormat.JPEG, 100, out );
        } catch ( Exception e ) {
            e.printStackTrace( );
        } finally {
            try {
                out.close( );
            } catch ( IOException e ) {
                e.printStackTrace( );
            }
        }
        return file;
    }

    public static void imageResize( String strFilePath, String filename, int width, int height ) {

        File file = new File( strFilePath );

        // If no folders
        if ( !file.exists( ) ) {
            file.mkdirs( );
            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
        File fileCacheItem = new File( strFilePath + "/" + filename );
        OutputStream out = null;

        Bitmap srcBmp = BitmapFactory.decodeFile( strFilePath + "/" + filename );
        Bitmap destBmp = Bitmap.createScaledBitmap( srcBmp, ( int ) width, ( int ) height, true );

        try {
            fileCacheItem.createNewFile( );
            out = new FileOutputStream( fileCacheItem );

            destBmp.compress( Bitmap.CompressFormat.JPEG, 100, out );
        } catch ( Exception e ) {
            e.printStackTrace( );
        } finally {
            try {
                out.close( );
            } catch ( IOException e ) {
                e.printStackTrace( );
            }
        }
    }

    public static void imageResize( String strFilePath, String filename, float scale ) {

        File file = new File( strFilePath );

        // If no folders
        if ( !file.exists( ) ) {
            file.mkdirs( );
            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }
        File fileCacheItem = new File( strFilePath + "/" + filename );
        OutputStream out = null;


        Bitmap srcBmp = BitmapFactory.decodeFile( strFilePath + "/" + filename );

        int width = srcBmp.getWidth( );
        int height = srcBmp.getHeight( );

        Bitmap destBmp = Bitmap.createScaledBitmap( srcBmp, ( int ) ( width * scale ), ( int ) ( height * scale ), true );

        try {
            fileCacheItem.createNewFile( );
            out = new FileOutputStream( fileCacheItem );

            destBmp.compress( Bitmap.CompressFormat.JPEG, 100, out );
        } catch ( Exception e ) {
            e.printStackTrace( );
        } finally {
            try {
                out.close( );
            } catch ( IOException e ) {
                e.printStackTrace( );
            }
        }
    }

    public static String base64Encode( String content ) {
        PLog.e( content );
        return Base64.encodeToString( content.getBytes( ), 0 );
    }

    public static String base64Decode( String content ) {
        PLog.e( content );
        return new String( Base64.decode( content, 0 ), 0, Base64.decode( content, 0 ).length );
    }

    public static void hideKeyboard( Context context, View view ) {
        if ( view != null ) {
            InputMethodManager imm;
            imm = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow( view.getWindowToken( ), 0 );
        }
    }

    public static String readFromAssetsFile( Context context, String filename ) {
        StringBuilder returnString = new StringBuilder( );
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getAssets( ).open( filename );
            isr = new InputStreamReader( fIn );
            input = new BufferedReader( isr );
            String line = "";
            while ( ( line = input.readLine( ) ) != null ) {
                returnString.append( line );
            }
        } catch ( Exception e ) {
            return "IOException";
        } finally {
            try {
                if ( isr != null )
                    isr.close( );
                if ( fIn != null )
                    fIn.close( );
                if ( input != null )
                    input.close( );
            } catch ( Exception e2 ) {
                return "IOException";
            }
        }
        return returnString.toString( );
    }

    public static String decimalToHex( int d ) {
        String digits = "0123456789ABCDEF";
        if ( d == 0 ) return "0";
        String hex = "";
        while ( d > 0 ) {
            int digit = d % 16;                // rightmost digit
            hex = digits.charAt( digit ) + hex;  // string concatenation
            d = d / 16;
        }
        if ( hex.length( ) % 2 != 0 )
            hex = "0" + hex;
        return hex;
    }

    public static int hexToDecimal( String s ) {
        String digits = "0123456789ABCDEF";
        s = s.toUpperCase( );
        int val = 0;
        for ( int i = 0; i < s.length( ); i++ ) {
            char c = s.charAt( i );
            int d = digits.indexOf( c );
            val = 16 * val + d;
        }
        return val;
    }

    public static byte[] hexStringToByteArray( String hex ) {
        byte[] result = null;

        if ( hex != null ) {

            result = new byte[ hex.length( ) / 2 ];
            for ( int i = 0; i < result.length; i++ ) {
                result[ i ] = ( byte ) Integer.parseInt( hex.substring( 2 * i, 2 * i + 2 ), 16 );
            }
        }
        return result;
    }

    public static String byteArrayToHexString( byte[] bytes ) {
        String HEXES = "0123456789ABCDEF";
        if ( bytes == null ) {
            return null;
        }
        final StringBuilder hex = new StringBuilder( 2 * bytes.length );
        for ( final byte b : bytes ) {
            hex.append( HEXES.charAt( ( b & 0xF0 ) >> 4 ) )
                    .append( HEXES.charAt( ( b & 0x0F ) ) );
        }
        return hex.toString( );
    }

    public static String unicodeDecode( final String in ) {
        String working = in;
        int index;
        index = working.indexOf( "\\u" );
        while ( index > -1 ) {
            int length = working.length( );
            if ( index > ( length - 6 ) ) break;
            int numStart = index + 2;
            int numFinish = numStart + 4;
            String substring = working.substring( numStart, numFinish );
            int number = Integer.parseInt( substring, 16 );
            String stringStart = working.substring( 0, index );
            String stringEnd = working.substring( numFinish );
            working = stringStart + ( ( char ) number ) + stringEnd;
            index = working.indexOf( "\\u" );
        }
        return working;
    }

    public static float dpToPx( Context context, int dp ) {
        Resources resources = context.getResources( );
        DisplayMetrics metrics = resources.getDisplayMetrics( );
        float px = dp * ( metrics.densityDpi / 160f );
        return px;
    }

    public static int getScreenWidth( Context context ) {
        Resources resources = context.getResources( );
        DisplayMetrics metrics = resources.getDisplayMetrics( );
        return ( int ) metrics.widthPixels;
    }

    public static int getScrennHeight( Context context ) {
        Resources resources = context.getResources( );
        DisplayMetrics metrics = resources.getDisplayMetrics( );
        return ( int ) metrics.heightPixels;
    }

    public static long diffOfSecond( String begin, String end ) {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        long diff = 0;
        Date beginDate = null;
        try {
            beginDate = formatter.parse( begin );
            Date endDate = formatter.parse( end );

            diff = endDate.getTime( ) - beginDate.getTime( );

        } catch ( ParseException e ) {
        }
        return diff;
    }

    public static long diffOfSecond( String begin ) {
        return diffOfSecond( begin, dateFormat( "yyyy-MM-dd HH:mm:ss" ) );
    }

    public static String dateFormat( Date date, String format ) {
        SimpleDateFormat formatter = new SimpleDateFormat( format, Locale.US );
        return formatter.format( date );
    }

    public static String dateFormat( String format ) {
        SimpleDateFormat formatter = new SimpleDateFormat( format, Locale.US );
        return formatter.format( new Date( ) );
    }

    public static String changeDateFormat( String dateString, String before, String after ) {
        if ( dateString == null || before == null || after == null ) return "";
        SimpleDateFormat beforeFormat = new SimpleDateFormat( before, Locale.US );
        SimpleDateFormat afterFormat = new SimpleDateFormat( after, Locale.US );
        Date date = new Date( );
        try {
            date = beforeFormat.parse( dateString );
        } catch ( ParseException e ) {
            date = new Date( );
        } finally {
            return afterFormat.format( date );
        }
    }

    public static Date dateFromFormat( String date, String format ) {
        SimpleDateFormat sdf = new SimpleDateFormat( format, Locale.US );
        try {
            return sdf.parse( date );
        } catch ( ParseException e ) {
            return new Date( );
        }
    }

    public static boolean equals( Object obj1, Object obj2 ) {
        if ( obj1 == null && obj2 == null ) return true;
        if ( obj1 == null ) return false;
        if ( obj2 == null ) return false;
        if ( obj1.equals( obj2 ) ) return true;
        else return false;
    }

    public static void keyboardUPorDown( Context context, View view, boolean up ) {
        if ( up == true ) {
            InputMethodManager imm = ( InputMethodManager ) context.getSystemService( context.INPUT_METHOD_SERVICE );
            imm.showSoftInput( view, 0 );
        } else if ( up == false ) {
            InputMethodManager imm = ( InputMethodManager ) context.getSystemService( context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow( view.getWindowToken( ), 0 );
        }
    }

    public static void keyboardToggle( Context context ) {
        InputMethodManager imm = ( InputMethodManager ) context.getSystemService( context.INPUT_METHOD_SERVICE );
        imm.toggleSoftInput( 0, 0 );
    }

    public static String getAppVersion( Context context ) {
        PackageInfo i = null;
        try {
            i = context.getPackageManager( ).getPackageInfo( context.getPackageName( ), 0 );
            return i.versionName;
        } catch ( PackageManager.NameNotFoundException e ) {
            return "";
        }
    }

    public static void copyToClipboard( Context context, String copyText, String toastMsg ) {
        int sdk = Build.VERSION.SDK_INT;
        if ( sdk < Build.VERSION_CODES.HONEYCOMB ) {
            android.text.ClipboardManager clipboard = ( android.text.ClipboardManager )
                    context.getSystemService( Context.CLIPBOARD_SERVICE );
            clipboard.setText( copyText );
        } else {
            android.content.ClipboardManager clipboard = ( android.content.ClipboardManager )
                    context.getSystemService( Context.CLIPBOARD_SERVICE );
            android.content.ClipData clip = android.content.ClipData.newPlainText( "Copy to clipboard", copyText );
            clipboard.setPrimaryClip( clip );
        }
        if ( toastMsg != null ) {
            Toast toast = Toast.makeText( context.getApplicationContext( ), toastMsg, Toast.LENGTH_SHORT );
            toast.show( );
        }
    }

    public static int getResource( Context context, String resourceType, String resName ) {
        try {
            Context resContext = context.createPackageContext( context.getPackageName( ), 0 );
            Resources res = resContext.getResources( );
            return res.getIdentifier( resName, resourceType, context.getPackageName( ) );
        } catch ( PackageManager.NameNotFoundException e ) {
            return -1;
        }
    }

    public static int getSoftButtonsBarHeight( Activity activity ) {
        DisplayMetrics metrics = new DisplayMetrics( );
        activity.getWindowManager( ).getDefaultDisplay( ).getMetrics( metrics );
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager( ).getDefaultDisplay( ).getRealMetrics( metrics );
        int realHeight = metrics.heightPixels;
        if ( realHeight > usableHeight )
            return realHeight - usableHeight;
        else
            return 0;
    }

    public static boolean checkBundleKey( Intent intent, String... keys ) {
        if ( intent == null ) return false;
        if ( intent.getExtras( ) == null ) return false;
        else {

            boolean checkKey = true;
            for ( String key : keys ) {
                if ( checkKey )
                    checkKey = intent.getExtras( ).containsKey( key );
            }
            return checkKey;
        }
    }

    public static int getTextViewHeight( TextView textView ) {
        WindowManager wm =
                ( WindowManager ) textView.getContext( ).getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay( );

        int deviceWidth;

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 ) {
            Point size = new Point( );
            display.getSize( size );
            deviceWidth = size.x;
        } else {
            deviceWidth = display.getWidth( );
        }
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec( deviceWidth, View.MeasureSpec.AT_MOST );
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec( 0, View.MeasureSpec.UNSPECIFIED );
        textView.measure( widthMeasureSpec, heightMeasureSpec );
        return textView.getMeasuredHeight( );
    }

    public static int getTextViewWidth( TextView textView ) {
        WindowManager wm =
                ( WindowManager ) textView.getContext( ).getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay( );

        int deviceWidth;

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2 ) {
            Point size = new Point( );
            display.getSize( size );
            deviceWidth = size.x;
        } else {
            deviceWidth = display.getWidth( );
        }
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec( deviceWidth, View.MeasureSpec.AT_MOST );
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec( 0, View.MeasureSpec.UNSPECIFIED );
        textView.measure( widthMeasureSpec, heightMeasureSpec );
        return textView.getMeasuredWidth( );
    }

    public static String getFileExtension( String filePath ) {
        String[] path = filePath.split( "\\." );
        if ( path.length > 0 ) {
            try {
                return path[ path.length - 1 ];
            } catch ( ArrayIndexOutOfBoundsException e ) {
                return "";
            } catch ( IndexOutOfBoundsException e ) {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String removeFileExtension( String filePath ) {
        int point = filePath.lastIndexOf( '.' );
        if ( point > 0 ) {
            return filePath.substring( 0, point );
        } else {
            return null;
        }
    }

    public static String getApplicationName( Context context ) {
        ApplicationInfo applicationInfo = context.getApplicationInfo( );
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString( ) : context.getString( stringId );
    }

    public static String currencyFormat( long changeComma ) {
        return String.format( Locale.US, "%,d", changeComma );
    }

    public static int parseInt( String string ) {
        try {
            return Integer.parseInt( string );
        } catch ( NumberFormatException | NullPointerException e ) {
            return 0;
        }
    }

    public static int parseInt( String string, int def ) {
        try {
            return Integer.parseInt( string );
        } catch ( NumberFormatException | NullPointerException e ) {
            return def;
        }
    }

    public static float parseFloat( String string ) {
        try {
            return Float.parseFloat( string );
        } catch ( NumberFormatException | NullPointerException e ) {
            return 0;
        }
    }

    public static double parseDouble( String string ) {
        try {
            return Double.parseDouble( string );
        } catch ( NumberFormatException | NullPointerException e ) {
            return 0;
        }
    }

    public static long parseLong( String string ) {
        try {
            return Long.parseLong( string );
        } catch ( NumberFormatException | NullPointerException e ) {
            return 0;
        }
    }

    public static Bundle createSerializableBundle( String key, Serializable object ) {
        Bundle bundle = new Bundle( );
        bundle.putSerializable( key, object );
        return bundle;
    }

    public static Bundle createStringBundle( String key, String value ) {
        Bundle bundle = new Bundle( );
        bundle.putString( key, value );
        return bundle;
    }

    public static Bundle createIntBundle( String key, int value ) {
        Bundle bundle = new Bundle( );
        bundle.putInt( key, value );
        return bundle;
    }

    public static String md5( final String s ) {
        try {
            MessageDigest digest = MessageDigest.getInstance( "MD5" );
            digest.update( s.getBytes( ) );
            byte messageDigest[] = digest.digest( );
            return byteArrayToHexString( messageDigest );
        } catch ( NoSuchAlgorithmException e ) {
            return null;
        }
    }

    public static String sha256( final String s ) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            digest.update( s.getBytes( ) );
            byte messageDigest[] = digest.digest( );
            return byteArrayToHexString( messageDigest );
        } catch ( NoSuchAlgorithmException e ) {
            return null;
        }
    }
}
