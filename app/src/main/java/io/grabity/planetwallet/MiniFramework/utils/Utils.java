package io.grabity.planetwallet.MiniFramework.utils;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.managers.BitCoinManager;
import io.grabity.planetwallet.MiniFramework.wallet.managers.EthereumManager;
import io.grabity.planetwallet.MiniFramework.wallet.store.MainItemStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.Widgets.FontTextView;

import static android.content.Context.CLIPBOARD_SERVICE;

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

    public static boolean isPlanetName( String name ) {
        boolean err = false;
        if ( name.matches( "[^ㄱ-ㅎㅏ-ㅣ가-힣]+$" ) ) {
            String regex = "^[a-zA-Z0-9\\w]+$";
            Pattern p = Pattern.compile( regex );
            Matcher m = p.matcher( name );
            if ( m.matches( ) ) {
                err = true;
            }
            return err;
        }
        return false;
    }

    public static < T > T jsonToVO( String jsonString, Class< T > type ) {
        Gson gson = new Gson( );
        try {
            return gson.fromJson( jsonString, type );
        } catch ( JsonSyntaxException e ) {
            try {
                return type.newInstance( );
            } catch ( IllegalAccessException e1 ) {
                return null;
            } catch ( InstantiationException e1 ) {
                return null;
            }
        }

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

                                if ( map.get( field.getName( ) ) != null )
                                    method.invoke( returnObject, Double.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );

                            } else if ( Float.class.equals( field.getType( ) ) ) {

                                if ( map.get( field.getName( ) ) != null )
                                    method.invoke( returnObject, Float.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );

                            } else if ( Long.class.equals( field.getType( ) ) ) {

                                if ( map.get( field.getName( ) ) != null )
                                    method.invoke( returnObject, Long.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );


                            } else if ( Integer.class.equals( field.getType( ) ) ) {

                                try {
                                    if ( map.get( field.getName( ) ) != null )
                                        method.invoke( returnObject, Integer.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );
                                } catch ( NumberFormatException e ) {
                                    method.invoke( returnObject, 0 );
                                }
                            } else if ( Boolean.class.equals( field.getType( ) ) ) {

                                if ( map.get( field.getName( ) ) != null )
                                    method.invoke( returnObject, Boolean.valueOf( String.valueOf( map.get( field.getName( ) ) ) ) );

                            } else if ( String.class.equals( field.getType( ) ) ) {

                                if ( map.get( field.getName( ) ) != null )
                                    method.invoke( returnObject, String.valueOf( map.get( field.getName( ) ) ) );

                            } else if ( List.class.equals( field.getType( ) ) ) {

                                PLog.e( "ClassNotFoundException List" );


                            } else if ( ArrayList.class.equals( field.getType( ) ) ) {

                                try {
                                    ParameterizedType myListType = ( ( ParameterizedType ) field.getGenericType( ) );
                                    Class< ? > itemType = ( Class< ? > ) myListType.getActualTypeArguments( )[ 0 ];

                                    ArrayList list = ( ( ArrayList ) map.get( field.getName( ) ) );


                                    if ( list != null ) {
                                        ArrayList resultList = new ArrayList<>( );
                                        for ( int i = 0; i < list.size( ); i++ ) {

                                            if ( LinkedTreeMap.class.equals( list.get( i ).getClass( ) ) ) {

                                                resultList.add( linkedMapToVO( ( LinkedTreeMap ) list.get( i ), itemType ) );
                                            } else if ( String.class.equals( list.get( i ).getClass( ) ) ) {
                                                resultList.add( String.valueOf( list.get( i ) ) );
                                            }
                                        }
                                        method.invoke( returnObject, resultList );
                                    }

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
                    context.getSystemService( CLIPBOARD_SERVICE );
            clipboard.setText( copyText );
        } else {
            android.content.ClipboardManager clipboard = ( android.content.ClipboardManager )
                    context.getSystemService( CLIPBOARD_SERVICE );
            android.content.ClipData clip = android.content.ClipData.newPlainText( "Copy to clipboard", copyText );
            clipboard.setPrimaryClip( clip );
        }
        if ( toastMsg != null ) {
            Toast toast = Toast.makeText( context.getApplicationContext( ), toastMsg, Toast.LENGTH_SHORT );
            toast.show( );
        }
    }

    public static void copyToClipboard( Context context, String copyText ) {
        ClipboardManager clipboardManager = ( ClipboardManager ) context.getSystemService( CLIPBOARD_SERVICE );
        ClipData clipData = ClipData.newPlainText( "Copy to clipboard", copyText );
        clipboardManager.setPrimaryClip( clipData );
    }

    public static boolean checkClipboard( Context context, Integer coinType ) {
        ClipboardManager clipboardManager = ( ClipboardManager ) context.getSystemService( CLIPBOARD_SERVICE );
        if ( clipboardManager.hasPrimaryClip( ) && Objects.requireNonNull( clipboardManager.getPrimaryClipDescription( ) ).hasMimeType( ClipDescription.MIMETYPE_TEXT_PLAIN ) ) {
            if ( Utils.equals( coinType, CoinType.BTC.getCoinType( ) ) ) {
                if ( BitCoinManager.getInstance( ).validateAddress( clipboardManager.getPrimaryClip( ).getItemAt( 0 ).getText( ).toString( ) ) ) {
                    return true;
                }
                return false;
            } else if ( Utils.equals( coinType, CoinType.ETH.getCoinType( ) ) ) {
                if ( EthereumManager.getInstance( ).isValidAddress( clipboardManager.getPrimaryClip( ).getItemAt( 0 ).getText( ).toString( ) ) ) {
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public static String getClipboard( Context context ) {
        ClipboardManager clipboardManager = ( ClipboardManager ) context.getSystemService( CLIPBOARD_SERVICE );
        if ( Objects.requireNonNull( clipboardManager.getPrimaryClip( ) ).getItemAt( 0 ) == null )
            return "";
        return clipboardManager.getPrimaryClip( ).getItemAt( 0 ).getText( ).toString( );
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

    public static float getTextWidth( TextView textView ) {
        if ( textView != null && textView.getText( ) != null ) {
            return textView.getPaint( ).measureText( textView.getText( ).toString( ) );
        }
        return -1;
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

    public static Bundle mergeBundles( Bundle... bundles ) {
        Bundle bundle = new Bundle( );
        for ( Bundle b : bundles ) {
            Set< String > keys = b.keySet( );
            for ( String k : keys ) {
                try {
                    bundle.putSerializable( k, ( Serializable ) b.get( k ) );
                } catch ( Exception e ) {
                    e.printStackTrace( );
                }
            }
        }
        return bundle;
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

    public static HashMap< String, String > createStringHashMap( String key, String value ) {
        HashMap< String, String > map = new HashMap<>( );
        map.put( key, value );
        return map;
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

    public static byte[] sha256Binary( byte[] input ) {
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            digest.update( input, 0, input.length );
            return digest.digest( );
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace( );
            return null;
        }
    }


    public static < T extends View > ArrayList< T > getAllViewsFromParentView( ViewGroup parentView, Class< T > findType ) {
        ArrayList< ViewGroup > viewGroups = new ArrayList<>( );
        viewGroups.add( parentView );
        ArrayList< T > items = new ArrayList<>( );

        int v = viewGroups.size( );
        int t = 0;

        while ( true ) {
            for ( int i = 0; i < viewGroups.get( t ).getChildCount( ); i++ ) {

                if ( viewGroups.get( t ).getChildAt( i ).getClass( ).equals( findType ) ) {

                    items.add( ( T ) viewGroups.get( t ).getChildAt( i ) );

                } else if ( viewGroups.get( t ).getChildAt( i ) instanceof ViewGroup ) {

                    viewGroups.add( ( ViewGroup ) viewGroups.get( t ).getChildAt( i ) );

                }

            }
            if ( v == viewGroups.size( ) && t + 1 == v ) {
                break;
            }
            v = viewGroups.size( );
            t++;
        }

        return items;
    }

    public static String addressReduction( String address ) {
        if ( address == null ) return "";
        if ( address.length( ) < 8 ) return "";
        String endString = address.substring( address.length( ) - 4 );
        return address.substring( 0, 2 ).equals( "0x" ) ? address.substring( 0, 6 ) + "..." + endString : address.substring( 0, 4 ) + "..." + endString;
    }

    public static String balanceReduction( String balance ) {
        if ( balance == null ) return "";
        if ( balance.length( ) <= 8 ) return balance;
        return new BigDecimal( balance.substring( 0, 8 ) ).stripTrailingZeros( ).toPlainString( );
//        return balance.substring( 0, 8 );
    }

    public static void addressForm( View v, String address ) {
        if ( address == null ) return;
        if ( address.length( ) < 8 ) return;
        if ( v.getClass( ) == TextView.class || v.getClass( ) == FontTextView.class ) {
            SpannableString s = new SpannableString( address );

            if ( address.substring( 0, 2 ).equals( "0x" ) ) {
                s.setSpan( new ForegroundColorSpan( Color.parseColor( "#FF0050" ) ), 2, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            } else {
                s.setSpan( new ForegroundColorSpan( Color.parseColor( "#FF0050" ) ), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
            }
            s.setSpan( new ForegroundColorSpan( Color.parseColor( "#FF0050" ) ), address.length( ) - 4, address.length( ), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            if ( v.getClass( ) == TextView.class ) {
                ( ( TextView ) v ).setText( s );
            } else if ( v.getClass( ) == FontTextView.class ) {
                ( ( FontTextView ) v ).setText( s );
            }
        }
    }

    public static ViewGroup getAndroidContentViewGroup( Activity activity ) {
        try {
            if ( activity != null ) {
                return ( ( ViewGroup ) activity.findViewById( android.R.id.content ) );
            }
        } catch ( Exception e ) {
            return null;
        }
        return null;
    }

    public static int getDeviceStatusBarHeight( Context context ) {
        if ( context == null ) return 0;
        return context.getResources( ).getDimensionPixelSize( context.getResources( ).getIdentifier( "status_bar_height", "dimen", "android" ) );
    }

    public static void postDelayed( Runnable runnable, long delay ) {
        new Handler( ).postDelayed( runnable, delay );
    }

    public static String join( ArrayList< String > list ) {
        StringBuilder builder = new StringBuilder( );
        for ( String s : list ) {
            builder.append( s );
        }
        return builder.toString( );
    }

    public static String join( String[] array ) {
        StringBuilder builder = new StringBuilder( );
        for ( String s : array ) {
            builder.append( s );
        }
        return builder.toString( );
    }

    public static String join( char[] array ) {
        StringBuilder builder = new StringBuilder( );
        for ( char c : array ) {
            builder.append( c );
        }
        return builder.toString( );
    }

    public static void addTopMargin( View target, float margin ) {
        try {
            ( ( ViewGroup.MarginLayoutParams ) target.getLayoutParams( ) ).topMargin = ( int ) margin;
            target.requestLayout( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }

    public static void addBottomMargin( View target, int margin ) {
        try {
            ( ( ViewGroup.MarginLayoutParams ) target.getLayoutParams( ) ).bottomMargin = ( int ) dpToPx( target.getContext( ), margin );
            target.requestLayout( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }

    public static void addTopMarginStatusBarHeight( Context context, View target ) {
        try {
            ( ( ViewGroup.MarginLayoutParams ) target.getLayoutParams( ) ).topMargin = ( int ) Utils.getDeviceStatusBarHeight( context );
            target.requestLayout( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }

    public static void setViewSize( View target, float width, float height ) {
        if ( target != null ) {
            target.getLayoutParams( ).width = ( int ) width;
            target.getLayoutParams( ).height = ( int ) height;
            target.requestLayout( );
        }
    }


    public static void setViewSize( View target, float size ) {
        if ( target != null ) {
            target.getLayoutParams( ).width = ( int ) size;
            target.getLayoutParams( ).height = ( int ) size;
            target.requestLayout( );
        }
    }

    public static void setScale( View target, float scale ) {
        if ( target != null ) {
            target.setScaleX( scale );
            target.setScaleY( scale );
        }
    }

    public static void setPadding( View target, int left, int top, int right, int bottom ) {
        if ( target != null ) {
            target.setPadding( ( int ) dpToPx( target.getContext( ), left ), ( int ) dpToPx( target.getContext( ), top ), ( int ) dpToPx( target.getContext( ), right ), ( int ) dpToPx( target.getContext( ), bottom ) );
            target.requestLayout( );
        }
    }

    public static String moveLeftPoint( String s, int leftPoint ) {
        if ( s == null || leftPoint < 0 ) return "";
        return new BigDecimal( s ).movePointLeft( leftPoint ).toPlainString( );
    }

    public static String moveRightPoint( String s, int rightPoint ) {
        if ( s == null || rightPoint < 0 ) return "";
        return new BigDecimal( s ).movePointRight( rightPoint ).toPlainString( );
    }

    public static String ofZeroClear( String s ) {
        if ( s == null ) return "";
        return new BigDecimal( s ).stripTrailingZeros( ).toPlainString( );
    }

    public static String feeCalculation( String strPrice, String strLimit ) {
        if ( strPrice == null || strLimit == null ) return "";
        BigDecimal price = new BigDecimal( strPrice );
        BigDecimal limit = new BigDecimal( strLimit );
        return price.multiply( limit ).stripTrailingZeros( ).toPlainString( );
    }


    public static String toMaxUnit( int precision, String balance ) {
        if ( balance == null || precision < 0 ) return "";
        if ( balance.equals( "0" ) ) return "0";
        return moveLeftPoint( balance, precision );
    }

    public static String toMaxUnit( CoinType coinType, String balance ) {
        return toMaxUnit( coinType.getPrecision( ), balance );
    }

    public static String toMaxUnit( MainItem item, String balance ) {
        if ( CoinType.of( item.getCoinType( ) ) == CoinType.ERC20 ) {
            return toMaxUnit( ( int ) Math.abs( Double.valueOf( item.getDecimals( ) ) ), balance );
        } else {
            return toMaxUnit( CoinType.of( item.getCoinType( ) ), balance );
        }

    }

    public static String toMinUnit( MainItem item, String value ) {
        int from = 0;
        int to = 0;
        if ( CoinType.of( item.getCoinType( ) ) == CoinType.ERC20 ) {
            from = ( int ) Math.abs( Double.valueOf( item.getDecimals( ) ) );
        } else {
            from = CoinType.of( item.getCoinType( ) ).getPrecision( );
        }

        if ( value == null || from < 0 || to < 0 ) return "";
        if ( from == to ) return value;
        if ( from < to ) return moveLeftPoint( value, to - from );
        return moveRightPoint( value, from - to );
    }

    public static String convertUnit( String value, int from, int to ) {
        if ( value == null || from < 0 || to < 0 ) return "";
        if ( from == to ) return value;
        if ( from < to ) return moveLeftPoint( value, to - from );
        return moveRightPoint( value, from - to );
    }

    public static String firstUpperCase( String s ) {
        if ( s == null || s.length( ) == 0 ) return "";
        if ( s.length( ) == 1 ) return s;
        String ss = s.substring( 0, 1 );
        String sss = s.substring( 1 );

        return ss.toUpperCase( ) + sss;
    }

    public static String removeLastZero( String str ) {
        char[] array = str.toCharArray( );
        int length = 0;
        for ( int i = array.length - 1; i > 0; i-- ) {
            if ( array[ i ] == '0' ) {
                length++;
            } else {
                break;
            }
        }
        str = str.substring( 0, str.length( ) - length );
        if ( str.charAt( str.length( ) - 1 ) == '.' ) {
            str = str.substring( 0, str.length( ) - 1 );
        }
        return str;
    }

    public static String prefKey( String... elements ) {
        StringBuilder builder = new StringBuilder( );
        if ( elements != null ) {
            for ( String element : elements ) {
                builder.append( element );
                builder.append( "_" );
            }
        }
        if ( builder.length( ) > 0 && builder.substring( builder.length( ) - 1 ).equals( "_" ) ) {
            builder.deleteCharAt( builder.length( ) - 1 );
        }
        return builder.toString( );
    }

    public static String emptyToNull( @Nullable String string ) {
        return TextUtils.isEmpty( string ) ? null : string;
    }

    public static String randomPlanetName( Context context, String address ) {
        if ( address == null || context == null ) return null;
        String[] list = context.getResources( ).getStringArray( R.array.planetRandomName );
        String nameSha256 = sha256( address );

        if ( nameSha256 == null || nameSha256.length( ) != 64 ) return null;

        int index = 0;
        if ( hexToDecimal( nameSha256.substring( 0, 4 ) ) > list.length ) {
            index = hexToDecimal( nameSha256.substring( 0, 3 ) );
        } else {
            index = hexToDecimal( nameSha256.substring( 0, 4 ) );
        }

        return new StringBuilder( ).append( list[ index ] ).append( hexToDecimal( nameSha256.substring( nameSha256.length( ) - 3 ) ) ).toString( );
    }

    public static void gbtSave( String keyId ) {
        MainItem gbt = new MainItem( );

        gbt.setKeyId( keyId );
        gbt.setCoinType( CoinType.ERC20.getCoinType( ) );
        gbt.setHide( "N" );
        gbt.setName( C.gbtInfo.GBT_NAME );
        gbt.setSymbol( C.gbtInfo.GBT_SYMBOL );
        gbt.setDecimals( C.gbtInfo.GBT_DECIMALS );
        gbt.setImg_path( C.gbtInfo.GBT_IMG_PATH );
        gbt.setContract( C.gbtInfo.GBT_CONTRACT );

        MainItemStore.getInstance( ).tokenSave( gbt );
    }

}
