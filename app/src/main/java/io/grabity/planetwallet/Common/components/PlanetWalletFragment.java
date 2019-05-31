package io.grabity.planetwallet.Common.components;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.MissingFormatArgumentException;
import java.util.Objects;

import io.grabity.planetwallet.MiniFramework.managers.FontManager;
import io.grabity.planetwallet.MiniFramework.networktask.NetworkInterface;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.Themeable;


/**
 * Created by. JcobPark on 2018. 08. 29
 */
public abstract class PlanetWalletFragment extends Fragment implements NetworkInterface {

    private View contentView;
    protected boolean transition = true;

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        PLog.setTAG( this.getClass( ).getSimpleName( ) );
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        if ( this.contentView != null ) return this.contentView;
        else return super.onCreateView( inflater, container, savedInstanceState );
    }

    @Override
    public void onResume( ) {
        super.onResume( );
        if ( theme != getPlanetWalletActivity( ).getPlanetWalletApplication( ).getCurrentTheme( ) ) {
            theme = getPlanetWalletActivity( ).getPlanetWalletApplication( ).getCurrentTheme( );
            onUpdateTheme( theme );
        } else {
            applyTheme( getPlanetWalletActivity( ).getPlanetWalletApplication( ).getCurrentTheme( ) );
        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {

    }

    protected PlanetWalletActivity getPlanetWalletActivity( ) {
        return ( PlanetWalletActivity ) getActivity( );
    }

    protected void viewInit( ) {
    }

    public void setData( ) {
    }


    public void setContentView( int resId ) {
        this.contentView = View.inflate( getPlanetWalletActivity( ), resId, null );
        overrideFonts( this.contentView );
    }

    public < T extends View > T findViewById( int resId ) {
        if ( this.contentView != null ) return this.contentView.findViewById( resId );
        else return null;
    }

    protected void overrideFonts( final View v ) {
        try {
            if ( v instanceof ViewGroup ) {
                ViewGroup vg = ( ViewGroup ) v;
                for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                    View child = vg.getChildAt( i );
                    overrideFonts( child );
                }
            } else if ( v instanceof FontTextView ) {
                ( ( TextView ) v ).setTypeface( FontManager.getInstance( ).getFont( ( ( FontTextView ) v ).getFontStyle( ) ) );
            } else if ( v instanceof TextView ) {
                ( ( TextView ) v ).setTypeface( FontManager.getInstance( ).getFont( ( ( TextView ) v ).getTypeface( ).getStyle( ) ) );
            }
        } catch ( Exception e ) {

        }
    }

    public void sendAction( Class< ? > targetClass ) {
        Intent intent = new Intent( getActivity( ), targetClass );
        startActivity( intent );
        if ( !transition )
            Objects.requireNonNull( getActivity( ) ).overridePendingTransition( 0, 0 );
    }

    public void sendAction( Class< ? > targetClass, Bundle bundle ) {
        Intent intent = new Intent( getActivity( ), targetClass );
        if ( bundle != null ) intent.putExtras( bundle );
        startActivity( intent );
        if ( !transition )
            Objects.requireNonNull( getActivity( ) ).overridePendingTransition( 0, 0 );
    }

    public void sendAction( int requestCode, Class< ? > targetClass ) {
        Intent intent = new Intent( getActivity( ), targetClass );
        startActivityForResult( intent, requestCode );
        if ( !transition )
            Objects.requireNonNull( getActivity( ) ).overridePendingTransition( 0, 0 );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, Bundle bundle ) {
        Intent intent = new Intent( getActivity( ), targetClass );
        intent.putExtras( bundle );
        startActivityForResult( intent, requestCode );
        if ( !transition )
            Objects.requireNonNull( getActivity( ) ).overridePendingTransition( 0, 0 );
    }

    public void sendAction( Class< ? > targetClass, int... flags ) {
        Intent intent = new Intent( getActivity( ), targetClass );
        for ( int f : flags ) intent.addFlags( f );
        startActivity( intent );
        if ( !transition )
            Objects.requireNonNull( getActivity( ) ).overridePendingTransition( 0, 0 );
    }

    public void sendAction( Class< ? > targetClass, Bundle bundle, int... flags ) {
        Intent intent = new Intent( getActivity( ), targetClass );
        if ( bundle != null ) intent.putExtras( bundle );
        startActivity( intent );
        if ( !transition )
            Objects.requireNonNull( getActivity( ) ).overridePendingTransition( 0, 0 );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, int... flags ) {
        Intent intent = new Intent( getActivity( ), targetClass );
        for ( int f : flags ) intent.addFlags( f );
        startActivityForResult( intent, requestCode );
        if ( !transition )
            Objects.requireNonNull( getActivity( ) ).overridePendingTransition( 0, 0 );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, Bundle bundle, int... flags ) {
        Intent intent = new Intent( getActivity( ), targetClass );
        intent.putExtras( bundle );
        for ( int f : flags ) intent.addFlags( f );
        startActivityForResult( intent, requestCode );
        if ( !transition )
            Objects.requireNonNull( getActivity( ) ).overridePendingTransition( 0, 0 );
    }

    public String localized( int id, Object... formats ) {
        String returnValue;
        try {
            if ( formats.length > 0 ) {
                if ( getResources( ).getString( id ).contains( "%s" ) ) {
                    returnValue = String.format( getResources( ).getString( id ), formats );
                } else
                    returnValue = getResources( ).getString( id );
            } else
                returnValue = getResources( ).getString( id );

        } catch ( MissingFormatArgumentException e ) {
            returnValue = getResources( ).getString( id ).replace( "%s", "" );
        }
        return returnValue;
    }

    public void clearStatusBar( ) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            Window window = getPlanetWalletActivity( ).getWindow( );
            window.setStatusBarColor( ContextCompat.getColor( getPlanetWalletActivity( ), R.color.colorPrimaryDark ) );
        }
    }

    public void setLightStatusBarTextColor( ) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int newUiVisibility = ( int ) getPlanetWalletActivity( ).getWindow( ).getDecorView( ).getSystemUiVisibility( );
            newUiVisibility &= ~( int ) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getPlanetWalletActivity( ).getWindow( ).getDecorView( ).setSystemUiVisibility( newUiVisibility );
        }
    }

    public void setDarkStatusBarTextColor( ) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int newUiVisibility = ( int ) getPlanetWalletActivity( ).getWindow( ).getDecorView( ).getSystemUiVisibility( );
            newUiVisibility |= ( int ) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            getPlanetWalletActivity( ).getWindow( ).getDecorView( ).setSystemUiVisibility( newUiVisibility );
        }
    }


    // Theme Variable & Methods
    private boolean theme = false;

    public boolean getCurrentTheme( ) {
        return theme;
    }

    protected void onUpdateTheme( boolean theme ) {
        applyTheme( theme );
    }

    public void setTheme( boolean theme ) {
        getPlanetWalletActivity( ).getPlanetWalletApplication( ).setTheme( theme );
        if ( this.theme != getPlanetWalletActivity( ).getPlanetWalletApplication( ).getCurrentTheme( ) ) {
            onUpdateTheme( theme );
            this.theme = getPlanetWalletActivity( ).getPlanetWalletApplication( ).getCurrentTheme( );
        }
    }

    private void applyTheme( boolean theme ) {
        this.theme = theme;
        findViewAndSetTheme( contentView, theme );
    }

    protected void findViewAndSetTheme( final View v, boolean theme ) {
        try {
            if ( v instanceof ViewGroup && !( v instanceof RecyclerView ) ) {
                ViewGroup vg = ( ViewGroup ) v;
                if ( v instanceof Themeable ) {
                    ( ( Themeable ) v ).setTheme( theme );
                }
                for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                    View child = vg.getChildAt( i );
                    findViewAndSetTheme( child, theme );
                }
            } else if ( v instanceof Themeable ) {
                ( ( Themeable ) v ).setTheme( theme );
            }

        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
}
