package io.grabity.planetwallet.Common.components;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.MissingFormatArgumentException;
import java.util.Stack;

import io.grabity.planetwallet.Common.components.AbsPopupView.PopupView;
import io.grabity.planetwallet.MiniFramework.managers.FontManager;
import io.grabity.planetwallet.MiniFramework.networktask.NetworkInterface;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.R;

/**
 * Created by. JcobPark on 2018. 08. 29
 */
public abstract class PlanetWalletActivity extends FragmentActivity implements View.OnClickListener, NetworkInterface {

    protected boolean transition = false;
    private Stack< PopupView > popupViewStack;

    @Override
    protected void onResume( ) {
        super.onResume( );
        PLog.setTAG( this.getClass( ).getSimpleName( ) );
    }

    @Override
    public void setContentView( int layoutResID ) {
        View view = LayoutInflater.from( this ).inflate( layoutResID, null );
        overrideFonts( view, FontManager.getInstance( ).getFont( ), FontManager.getInstance( ).getBoldFont( ) );
        super.setContentView( view );
        setStatusColor( );
    }

    protected void overrideFonts( final View v, Typeface font, Typeface bold ) {
        try {
            if ( v instanceof ViewGroup ) {
                ViewGroup vg = ( ViewGroup ) v;
                for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                    View child = vg.getChildAt( i );
                    overrideFonts( child, font, bold );
                }
            } else if ( v instanceof TextView ) {
                if ( ( ( TextView ) v ).getTypeface( ).getStyle( ) == Typeface.BOLD ) {
                    ( ( TextView ) v ).setTypeface( bold );
                } else {
                    ( ( TextView ) v ).setTypeface( font );
                }
            }
        } catch ( Exception e ) {

        }
    }

    @Override
    protected void onPause( ) {
        super.onPause( );
    }

    @Override
    public void onClick( View v ) {
    }

    @Override
    protected void onStop( ) {
        super.onStop( );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
        if ( popupViewStack != null && !popupViewStack.isEmpty( ) ) {
            onRemovePopup( popupViewStack.peek( ) );
            popupViewStack.pop( ).onBackPressed( );
        } else {
            super.onBackPressed( );
        }
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        PLog.e( "Activity onRequestPermissionsResult" );
        for ( int i = 0; i < grantResults.length; i++ ) {
            int grantResult = grantResults[ i ];
            if ( grantResult == PackageManager.PERMISSION_DENIED ) {
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                    if ( !shouldShowRequestPermissionRationale( permissions[ i ] ) ) {
                        neverNotAllowed( requestCode, permissions[ i ] );
                    }
                }
                permissionNotAllowed( requestCode, permissions[ i ] );
                return;
            }
        }
        permissionsAllowed( requestCode );
    }

    protected void requestPermissions( int code, String... permissions ) {
        boolean isCheck = false;
        for ( String permission : permissions ) {
            isCheck = ( ActivityCompat.checkSelfPermission( this, permission ) != PackageManager.PERMISSION_GRANTED ) || isCheck;
        }
        if ( isCheck ) {
            ActivityCompat.requestPermissions( this, permissions, code );
        } else {
            permissionsAllowed( code );
        }
    }

    protected void neverNotAllowed( int code, String permission ) {
    }

    protected void permissionsAllowed( int code ) {
    }

    protected void permissionNotAllowed( int code, String permission ) {
    }

    protected void viewInit( ) {
    }

    protected void setData( ) {
    }

    public void setTransition( boolean transition ) {
        this.transition = transition;
    }


    public PlanetWalletApplication getPlanetWalletApplication( ) {
        return ( PlanetWalletApplication ) getApplication( );
    }

    public void sendAction( Class< ? > targetClass ) {
        Intent intent = new Intent( this, targetClass );
        startActivity( intent );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    public void sendAction( Class< ? > targetClass, Bundle bundle ) {
        Intent intent = new Intent( this, targetClass );
        if ( bundle != null ) intent.putExtras( bundle );
        startActivity( intent );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    public void sendAction( int requestCode, Class< ? > targetClass ) {
        Intent intent = new Intent( this, targetClass );
        startActivityForResult( intent, requestCode );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, Bundle bundle ) {
        Intent intent = new Intent( this, targetClass );
        intent.putExtras( bundle );
        startActivityForResult( intent, requestCode );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    public void sendAction( Class< ? > targetClass, int... flags ) {
        Intent intent = new Intent( this, targetClass );
        for ( int f : flags ) intent.addFlags( f );
        startActivity( intent );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    public void sendAction( Class< ? > targetClass, Bundle bundle, int... flags ) {
        Intent intent = new Intent( this, targetClass );
        if ( bundle != null ) intent.putExtras( bundle );
        startActivity( intent );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, int... flags ) {
        Intent intent = new Intent( this, targetClass );
        for ( int f : flags ) intent.addFlags( f );
        startActivityForResult( intent, requestCode );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, Bundle bundle, int... flags ) {
        Intent intent = new Intent( this, targetClass );
        intent.putExtras( bundle );
        for ( int f : flags ) intent.addFlags( f );
        startActivityForResult( intent, requestCode );
        if ( !transition ) overridePendingTransition( 0, 0 );
    }

    public String localized( int id, Object... formats ) {
        String returnValue;
        try {
            if ( formats.length > 0 ) {
                if ( getResources( ).getString( id ).contains( "%s" ) ) {
                    returnValue = String.format( getResources( ).getString( id ), formats );
                } else
                    returnValue = String.format( getResources( ).getString( id ) );
            } else
                returnValue = String.format( getResources( ).getString( id ) );

        } catch ( MissingFormatArgumentException e ) {
            returnValue = getResources( ).getString( id ).replace( "%s", "" );
        }
        return returnValue;
    }


    public Serializable getSerialize( String key ) {
        if ( getIntent( ) != null )
            return getIntent( ).getSerializableExtra( key );
        else
            return null;
    }

    public int getInt( String key ) {
        return getInt( key, 0 );
    }

    public int getInt( String key, int defVal ) {
        int retVal = defVal;
        try {
            retVal = getIntent( ).getExtras( ).getInt( key );
        } catch ( NullPointerException e ) {
            return retVal;
        }
        return retVal;
    }

    public String getString( String key ) {
        return getString( key, "" );
    }

    public String getString( String key, String defVal ) {
        String retVal = defVal;
        try {
            retVal = getIntent( ).getExtras( ).getString( key );
        } catch ( NullPointerException e ) {

        }
        return retVal;
    }


    public float getFloat( String key ) {
        return getFloat( key, 0.0f );
    }

    public float getFloat( String key, float defVal ) {
        float retVal = defVal;
        try {
            retVal = getIntent( ).getExtras( ).getFloat( key );
        } catch ( NullPointerException e ) {

        }
        return retVal;
    }


    public ArrayList getStringArrayList( String key ) {
        return getStringArrayList( key, new ArrayList<>( ) );
    }

    public ArrayList getStringArrayList( String key, ArrayList defVal ) {
        ArrayList retVal = defVal;
        try {
            return getIntent( ).getExtras( ).getStringArrayList( key );
        } catch ( NullPointerException e ) {
            return retVal;
        }
    }


    public void setStatusColor( int color ) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            Window window = getWindow( );
            window.setStatusBarColor( color );
        }
    }

    public void setStatusColor( ) {
        setStatusColor( ContextCompat.getColor( this, R.color.colorPrimary ) );
    }

    public void clearStatusBar( ) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            Window window = getWindow( );
            window.setStatusBarColor( ContextCompat.getColor( this, R.color.colorPrimaryDark ) );
        }
    }

    public void setLightStatusBarTextColor( ) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int newUiVisibility = ( int ) getWindow( ).getDecorView( ).getSystemUiVisibility( );
            newUiVisibility &= ~( int ) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow( ).getDecorView( ).setSystemUiVisibility( newUiVisibility );
        }
    }

    public void setDarkStatusBarTextColor( ) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int newUiVisibility = ( int ) getWindow( ).getDecorView( ).getSystemUiVisibility( );
            newUiVisibility |= ( int ) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getWindow( ).getDecorView( ).setSystemUiVisibility( newUiVisibility );
        }
    }

    public void addPopup( PopupView popupView ) {
        if ( popupViewStack == null ) {
            popupViewStack = new Stack<>( );
        }
        popupViewStack.push( popupView );
    }

    public void onRemovePopup( PopupView popupView ) {
    }

    public Stack< PopupView > getPopupViewStack( ) {
        return popupViewStack;
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode,
                           int statusCode, String result ) {

    }
}
