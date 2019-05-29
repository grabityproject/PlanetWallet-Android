package io.grabity.planetwallet.Common.components;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import io.grabity.planetwallet.Widgets.PlanetWalletViews.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetWalletViews.PWLinearLayout;
import io.grabity.planetwallet.Widgets.PlanetWalletViews.PWRelativeLayout;
import io.grabity.planetwallet.Widgets.Themeable;

/**
 * Created by. JcobPark on 2018. 08. 29
 */
public abstract class PlanetWalletActivity extends FragmentActivity implements View.OnClickListener, NetworkInterface {


    public enum Transition {
        NO_ANIMATION,
        SLIDE_SIDE,
        SLIDE_UP
    }

    protected Transition transition = Transition.SLIDE_SIDE;
    private Stack< PopupView > popupViewStack;

    private boolean theme = false;
    private View contentView;

    private boolean fadeIn = false;

    @Override
    protected void onResume( ) {
        super.onResume( );
        if ( theme != getPlanetWalletApplication( ).getCurrentTheme( ) ) {
            theme = getPlanetWalletApplication( ).getCurrentTheme( );
            onUpdateTheme( theme );
        }
        PLog.setTAG( this.getClass( ).getSimpleName( ) );
    }

    protected void onUpdateTheme( boolean theme ) {
        applyTheme( theme );
    }

    @Override
    public void setContentView( int layoutResID ) {
        contentView = LayoutInflater.from( this ).inflate( layoutResID, null );
        overrideFonts( contentView );
        applyTheme( getPlanetWalletApplication( ).getCurrentTheme( ) );
        super.setContentView( contentView );
    }

    public void setContentView( int layoutResID, boolean fadeIn ) {
        this.fadeIn = fadeIn;
        this.setContentView( layoutResID );
        if ( fadeIn ) {
            childViewAllFadeIn( ( ( ViewGroup ) this.contentView ).getChildAt( 0 ) );
        }
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

    protected void childViewAllFadeIn( final View v ) {
        ArrayList< Animator > animations = new ArrayList<>( );
        AnimatorSet animatorSet = new AnimatorSet( );
        animatorSet.setDuration( 400 );
        if ( v instanceof ViewGroup ) {
            ViewGroup vg = ( ViewGroup ) v;
            for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                View child = vg.getChildAt( i );
                child.setAlpha( 0.0f );
                animations.add( ObjectAnimator.ofFloat( child, "alpha", 1.0f ) );
            }
        }
        animatorSet.playTogether( animations );
        animatorSet.start( );
    }


    protected void childViewAllFadeOut( final View v ) {
        ArrayList< Animator > animations = new ArrayList<>( );
        AnimatorSet animatorSet = new AnimatorSet( );
        animatorSet.setDuration( 400 );
        if ( v instanceof ViewGroup ) {
            ViewGroup vg = ( ViewGroup ) v;
            for ( int i = 0; i < vg.getChildCount( ); i++ ) {
                View child = vg.getChildAt( i );
                animations.add( ObjectAnimator.ofFloat( child, "alpha", 0.0f ) );
            }
        }
        animatorSet.playTogether( animations );
        animatorSet.start( );
    }

    public void setTheme( boolean theme ) {
        getPlanetWalletApplication( ).setTheme( theme );
        if ( this.theme != getPlanetWalletApplication( ).getCurrentTheme( ) ) {
            onUpdateTheme( theme );
            this.theme = getPlanetWalletApplication( ).getCurrentTheme( );
        }
    }

    private void applyTheme( boolean theme ) {
        this.theme = theme;
        findViewAndSetTheme( contentView, theme );
        setStatusColor( );
    }

    protected void findViewAndSetTheme( final View v, boolean theme ) {
        try {
            if ( v instanceof ViewGroup ) {
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

    @Override
    protected void onStop( ) {
        super.onStop( );
    }

    @Override
    public void onClick( View v ) {
    }

    @Override
    public void onBackPressed( ) {
        if ( popupViewStack != null && !popupViewStack.isEmpty( ) ) {
            onRemovePopup( popupViewStack.peek( ) );
            popupViewStack.pop( ).onBackPressed( );
        } else {
            if ( fadeIn ) {
                childViewAllFadeOut( ( ( ViewGroup ) this.contentView ).getChildAt( 0 ) );
                new Handler( ).postDelayed( new Runnable( ) {
                    @Override
                    public void run( ) {
                        finish( );
                        setExitTransition( );
                    }
                }, 400 );
            } else {
                super.onBackPressed( );
                setExitTransition( );
            }
        }
    }

//    @Override
//    public void finish( ) {
//        setExitTransition( );
//        super.finish( );
//    }

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
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

    public void setTransition( Transition transition ) {
        this.transition = transition;
    }

    public PlanetWalletApplication getPlanetWalletApplication( ) {
        return ( PlanetWalletApplication ) getApplication( );
    }

    public void sendAction( Class< ? > targetClass ) {
        Intent intent = new Intent( this, targetClass );
        startActivity( putTransitionToBundle( intent, transition ) );
        setEnterTransition( );
    }

    public void sendAction( Class< ? > targetClass, Bundle bundle ) {
        Intent intent = new Intent( this, targetClass );
        if ( bundle != null ) intent.putExtras( bundle );
        startActivity( putTransitionToBundle( intent, transition ) );
        setEnterTransition( );
    }

    public void sendAction( int requestCode, Class< ? > targetClass ) {
        Intent intent = new Intent( this, targetClass );
        startActivityForResult( putTransitionToBundle( intent, transition ), requestCode );
        setEnterTransition( );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, Bundle bundle ) {
        Intent intent = new Intent( this, targetClass );
        intent.putExtras( bundle );
        startActivityForResult( putTransitionToBundle( intent, transition ), requestCode );
        setEnterTransition( );
    }

    public void sendAction( Class< ? > targetClass, int... flags ) {
        Intent intent = new Intent( this, targetClass );
        for ( int f : flags ) intent.addFlags( f );
        startActivity( putTransitionToBundle( intent, transition ) );
        setEnterTransition( );
    }

    public void sendAction( Class< ? > targetClass, Bundle bundle, int... flags ) {
        Intent intent = new Intent( this, targetClass );
        if ( bundle != null ) intent.putExtras( bundle );
        startActivity( putTransitionToBundle( intent, transition ) );
        setEnterTransition( );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, int... flags ) {
        Intent intent = new Intent( this, targetClass );
        for ( int f : flags ) intent.addFlags( f );
        startActivityForResult( putTransitionToBundle( intent, transition ), requestCode );
        setEnterTransition( );
    }

    public void sendAction( int requestCode, Class< ? > targetClass, Bundle bundle, int... flags ) {
        Intent intent = new Intent( this, targetClass );
        intent.putExtras( bundle );
        for ( int f : flags ) intent.addFlags( f );
        startActivityForResult( putTransitionToBundle( intent, transition ), requestCode );
        setEnterTransition( );
    }

    private Intent putTransitionToBundle( Intent intent, Transition transition ) {
        intent.putExtra( "ACTIVITY_TRANSITION", transition.ordinal( ) );
        return intent;
    }

    private void setEnterTransition( ) {
        if ( transition == Transition.NO_ANIMATION ) overridePendingTransition( 0, 0 );
        if ( transition == Transition.SLIDE_SIDE )
            overridePendingTransition( R.anim.right_in, R.anim.left_out );
        if ( transition == Transition.SLIDE_UP )
            overridePendingTransition( R.anim.bottom_in, R.anim.top_out );
    }

    private void setExitTransition( ) {
        Transition transition = Transition.values( )[ getInt( "ACTIVITY_TRANSITION" ) ];
        if ( transition == Transition.NO_ANIMATION ) overridePendingTransition( 0, 0 );
        if ( transition == Transition.SLIDE_SIDE )
            overridePendingTransition( R.anim.left_in, R.anim.right_out );
        if ( transition == Transition.SLIDE_UP )
            overridePendingTransition( R.anim.top_in, R.anim.bottom_out );
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
        if ( this.contentView instanceof PWRelativeLayout ) {

            Integer color = ( ( PWRelativeLayout ) this.contentView ).getBackgroundColor( );
            if ( color != null ) {
                setStatusColor( color );
                if ( color > -8421505 ) {
                    setDarkStatusBarTextColor( );
                } else {
                    setLightStatusBarTextColor( );
                }
            }

        } else if ( this.contentView instanceof PWLinearLayout ) {

            Integer color = ( ( PWLinearLayout ) this.contentView ).getBackgroundColor( );
            if ( color != null ) {
                setStatusColor( color );
                if ( color > -8421505 ) {
                    setDarkStatusBarTextColor( );
                } else {
                    setLightStatusBarTextColor( );
                }
            }

        } else {
            if ( theme ) {
                setStatusColor( Color.WHITE );
                setDarkStatusBarTextColor( );
            } else {
                setStatusColor( Color.WHITE );
                setLightStatusBarTextColor( );
            }
        }
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

    public boolean getCurrentTheme( ) {
        return theme;
    }
}
