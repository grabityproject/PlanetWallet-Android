package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.app.Service;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.KeyboardManager;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.PlanetManagementActivity;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.RoundButton.RoundButton;
import io.grabity.planetwallet.Widgets.RoundEditText;
import io.grabity.planetwallet.Widgets.ShadowView;
import io.grabity.planetwallet.Widgets.ToolBar;


public class PlanetGenerateActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ViewTreeObserver.OnGlobalLayoutListener, KeyboardManager.KeyboardVisibilityEventListener, TextView.OnEditorActionListener, TextWatcher {

    private ViewMapper viewMapper;
    boolean first = true;

    private String plantName;
    private int cursor;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setStatusTransparent( true );
        setContentView( R.layout.activity_planet_generate );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

        KeyboardManager.setEventListener( this, this );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        findViewById( android.R.id.content ).getViewTreeObserver( ).addOnGlobalLayoutListener( this );

        viewMapper.btnSelect.setOnClickListener( this );
        viewMapper.btnRefresh.setOnClickListener( this );

        ( ( ViewGroup.MarginLayoutParams ) viewMapper.toolBar.getLayoutParams( ) ).height = ( int ) ( Utils.dpToPx( this, 68 ) + getResources( ).getDimensionPixelSize( getResources( ).getIdentifier( "status_bar_height", "dimen", "android" ) ) );
        viewMapper.toolBar.requestLayout( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnRefresh.setBorderColor( Color.parseColor( !getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );
        viewMapper.btnSelect.setBorderColor( Color.parseColor( !getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );

        viewMapper.shadowBackground.setShadowColor(
                Color.parseColor( getCurrentTheme( ) ? "#FFFFFF" : "#000000" ),
                Color.parseColor( getCurrentTheme( ) ? "#C8FFFFFF" : "#A8000000" )
        );

        viewMapper.etPlanetName.setTypeface( Typeface.createFromAsset( getAssets( ), "fonts/WorkSans-Bold.otf" ) );
        viewMapper.etPlanetName.setSelection( viewMapper.etPlanetName.getText( ).length( ) );

        viewMapper.etPlanetName.setOnEditorActionListener( this );
        viewMapper.etPlanetName.addTextChangedListener( this );

    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    @Override
    public void onGlobalLayout( ) {
        findViewById( android.R.id.content ).getViewTreeObserver( ).removeOnGlobalLayoutListener( this );

        float backgroundSize = ( ( Utils.getScreenWidth( this ) * 480.0f / 375.0f ) );
        float backgroundTopMargin = getResources( ).getDimensionPixelSize( getResources( ).getIdentifier( "status_bar_height", "dimen", "android" ) );

        viewMapper.planetBackground.getLayoutParams( ).width = ( int ) backgroundSize;
        viewMapper.planetBackground.getLayoutParams( ).height = ( int ) backgroundSize;
        viewMapper.shadowBackground.getLayoutParams( ).width = ( int ) backgroundSize;
        viewMapper.shadowBackground.getLayoutParams( ).height = ( int ) backgroundSize;

        viewMapper.planetBackground.setScaleX( 1.3f );
        viewMapper.planetBackground.setScaleY( 1.3f );

        ( ( ViewGroup.MarginLayoutParams ) viewMapper.planetBackground.getLayoutParams( ) ).topMargin = ( int ) backgroundTopMargin;

        viewMapper.planetBackground.requestLayout( );
        viewMapper.shadowBackground.requestLayout( );

    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnRefresh ) {
            viewMapper.planetView.setData( randomString( ) );
            viewMapper.planetBackground.setData( viewMapper.planetView.getData( ) );
        } else if ( v == viewMapper.btnSelect ) {
            if ( Utils.equals( getInt( C.bundleKey.PLANETADD, WalletAddActivity.PLANETADD ), WalletAddActivity.PLANETADD ) ) {
                setResult( RESULT_OK );
                super.onBackPressed( );
            } else {

                Utils.setPreferenceData( this, C.pref.WALLET_GENERATE, C.wallet.CREATE );
                sendAction( MainActivity.class );
                finish( );

            }

        }
    }

    /**
     * planetView Change Test
     */
    String randomString( ) {
        Random random = new Random( );
        StringBuffer buffer = new StringBuffer( );
        for ( int i = 0; i < 25; i++ ) {
            buffer.append( ( char ) ( random.nextInt( 26 ) ) + 97 );
        }
        return buffer.toString( );
    }


    @Override
    public void onVisibilityChanged( boolean isOpen, float oldHeight, float keyboardHeight ) {
        if ( !isOpen ) {
            viewMapper.planetBackground.setFocusable( true );
            viewMapper.planetBackground.setFocusableInTouchMode( true );
            viewMapper.planetBackground.requestFocus( );

            if ( viewMapper.etPlanetName.getText( ).length( ) == 0 ) {
                //Todo 모두 다 지우고 키보드를 닫는경우
                viewMapper.etPlanetName.setText( "WalletName" );
            }
        } else {
            if ( viewMapper.etPlanetName.getText( ).toString( ).contains( " " ) )
                if ( first ) {
                    first = false;
                    viewMapper.etPlanetName.setText( viewMapper.etPlanetName.getText( ).toString( ).trim( ) );
                    viewMapper.etPlanetName.setSelection( viewMapper.etPlanetName.getText( ).length( ) );
//                    viewMapper.etPlanetName.setSelection( viewMapper.etPlanetName.getText( ).toString( ).length( ) - getStrCount( " ", viewMapper.etPlanetName.getText( ).toString( ) ) );
                }
        }
    }

    int getStrCount( String regex, String input ) {
        Pattern p = Pattern.compile( regex );
        Matcher m = p.matcher( input );
        int count = 0;
        while ( m.find( ) )
            count++;
        return count;
    }

    @Override
    public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
        if ( actionId == EditorInfo.IME_ACTION_DONE ) {
            if ( getCurrentFocus( ) != null )
                Utils.hideKeyboard( this, getCurrentFocus( ) );
            return false;
        }
        return true;
    }


    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
        if ( viewMapper.etPlanetName.getText( ).length( ) != 0 ) {
            plantName = viewMapper.etPlanetName.getText( ).toString( );
            cursor = viewMapper.etPlanetName.getSelectionStart( );
        } else {
            plantName = "";
        }

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {

    }

    @Override
    public void afterTextChanged( Editable s ) {
        if ( viewMapper.etPlanetName.getText( ).length( ) != 0 ) {
            if ( !Utils.isPlanetName( viewMapper.etPlanetName.getText( ).toString( ) ) ) {
                viewMapper.etPlanetName.setText( plantName );
                viewMapper.etPlanetName.setSelection( cursor <= 0 ? cursor : cursor - 1 );
            }
        }

    }


    public class ViewMapper {

        ViewGroup groupBtn;

        CircleImageView btnRefresh;
        CircleImageView btnSelect;
        ToolBar toolBar;
        PlanetView planetView;

        PlanetView planetBackground;
        ShadowView shadowBackground;

        EditText etPlanetName;

        public ViewMapper( ) {

            groupBtn = findViewById( R.id.group_planet_generate_refresh_select_btn );

            planetView = findViewById( R.id.planet_generate_icon );
            toolBar = findViewById( R.id.toolBar );
            btnRefresh = findViewById( R.id.btn_planet_generate_refresh );
            btnSelect = findViewById( R.id.btn_planet_generate_select );

            planetBackground = findViewById( R.id.planet_planet_generate_background );
            shadowBackground = findViewById( R.id.shadow_planet_generate_background );

            etPlanetName = findViewById( R.id.et_planet_generate_name );


        }

    }
}