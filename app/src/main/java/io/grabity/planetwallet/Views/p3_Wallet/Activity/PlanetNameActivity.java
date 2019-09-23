package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.KeyboardManager;
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.signer.Signer;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.ErrorResult;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Widgets.ActionEditText;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.PlanetCursor;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ShadowView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class PlanetNameActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ViewTreeObserver.OnGlobalLayoutListener, KeyboardManager.KeyboardVisibilityEventListener, TextView.OnEditorActionListener, TextWatcher {

    private ViewMapper viewMapper;
    boolean first = true;

    private Planet planet;

    private String plantName;
    private int cursor;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setStatusTransparent( true );
        setContentView( R.layout.activity_planet_name );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

        KeyboardManager.setEventListener( this, this );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.btnSubmit.setOnClickListener( this );

        viewMapper.toolBar.setTopMarginFullScreen( );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.shadowBackground.setShadowColor(
                Color.parseColor( getCurrentTheme( ) ? "#FFFFFF" : "#000000" ),
                Color.parseColor( getCurrentTheme( ) ? "#C8FFFFFF" : "#A8000000" )
        );

        viewMapper.etPlanetName.setTypeface( Typeface.createFromAsset( getAssets( ), "fonts/WorkSans-Bold.otf" ) );
        viewMapper.etPlanetName.setSelection( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).length( ) );

        viewMapper.etPlanetName.setOnEditorActionListener( this );
        viewMapper.etPlanetName.addTextChangedListener( this );

        viewMapper.planetBackground.setFocusable( true );
        viewMapper.planetBackground.setFocusableInTouchMode( true );
        viewMapper.planetBackground.requestFocus( );
        viewMapper.planetBackground.getViewTreeObserver( ).addOnGlobalLayoutListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            onBackPressed( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );

            if ( planet.getName( ) != null ) {

                if ( getRequestCode( ) == C.requestCode.PLANET_ADD || getRequestCode( ) == C.requestCode.MAIN_PLANET_ADD ) {
                    setResult( RESULT_OK );
                    super.onBackPressed( );

                } else {
                    sendAction( MainActivity.class );
                    finish( );
                }

            } else {
                planet.setName( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).toString( ) );
                viewMapper.planetView.setData( planet.getAddress( ) );
                viewMapper.planetBackground.setData( planet.getAddress( ) );


                viewMapper.etPlanetName.setText( Utils.randomPlanetName( this, planet.getAddress( ) ) );
                viewMapper.cursor.setX( ( ( Utils.getScreenWidth( this ) + Utils.getTextWidth( viewMapper.etPlanetName ) ) / 2.0f ) + Utils.dpToPx( this, 4 ) );
            }

        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnSubmit ) {

            if ( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).length( ) == 0 ) {
                CustomToast.makeText( this, localized( R.string.planet_name_name_not_blank_title ) ).show( );
                return;
            }

            planet.setName( viewMapper.etPlanetName.getText( ).toString( ).trim( ) );

            Planet request = new Planet( );
            request.setPlanet( planet.getName( ) );
            request.setSignature(
                    Signer.getInstance( ).sign( planet.getName( ),
                            planet.getPrivateKey( KeyPairStore.getInstance( ), getPlanetWalletApplication( ).getPINCODE( ) ) ) );
            request.setAddress( planet.getAddress( ) );

            new Post( this ).action( Route.URL( "planet", CoinType.of( planet.getCoinType( ) ).name( ) ), 0, 0, request, Utils.createStringHashMap( "device-key", getPlanetWalletApplication( ).getDeviceKey( ) ) );

        }
    }


    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );


        if ( !error ) {
            if ( statusCode == 200 && requestCode == 0 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Planet.class );
                if ( returnVO.isSuccess( ) ) {

                    PlanetStore.getInstance( ).save( planet );

                    //ETH wallet GBT add
                    if ( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ) {
                        Utils.gbtSave( planet.getKeyId( ) );
                    }

                    if ( getRequestCode( ) == C.requestCode.PLANET_ADD || getRequestCode( ) == C.requestCode.MAIN_PLANET_ADD ) {
                        setResult( RESULT_OK );
                        super.onBackPressed( );

                    } else {
                        sendAction( MainActivity.class );
                        finish( );
                    }
                }
            } else {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, ErrorResult.class );
                ErrorResult errorResult = ( ErrorResult ) returnVO.getResult( );
                if ( errorResult == null ) return;
                CustomToast.makeText( this, errorResult.getErrorMsg( ) ).show( );
            }
        }
    }


    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {
        if ( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).length( ) != 0 ) {
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
        if ( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).length( ) != 0 ) {
            if ( !Utils.isPlanetName( viewMapper.etPlanetName.getText( ).toString( ) ) ) {
                viewMapper.etPlanetName.setText( plantName );
                try {
                    viewMapper.etPlanetName.setSelection( cursor <= 0 ? cursor : cursor - 1 );
                } catch ( Exception e ) {
                    viewMapper.etPlanetName.setSelection( viewMapper.etPlanetName.getText( ).toString( ).length( ) );
                }

            }
        }
    }

    @Override
    public void onGlobalLayout( ) {
        viewMapper.planetBackground.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
        Utils.setViewSize( viewMapper.planetBackground, Utils.getScreenWidth( this ) * 480.0f / 375.0f );
        Utils.setViewSize( viewMapper.shadowBackground, Utils.getScreenWidth( this ) * 480.0f / 375.0f );
        Utils.addTopMarginStatusBarHeight( this, viewMapper.planetBackground );
        Utils.setScale( viewMapper.planetBackground, 1.3f );
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
    public void onVisibilityChanged( boolean isOpen, float oldHeight, float keyboardHeight ) {
        if ( !isOpen ) {
            viewMapper.planetBackground.setFocusable( true );
            viewMapper.planetBackground.setFocusableInTouchMode( true );
            viewMapper.planetBackground.requestFocus( );
        } else {
            if ( first ) {
                first = false;
                viewMapper.cursor.setVisibility( View.GONE );
            }
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            super.onBackPressed( );
        }
    }

    public class ViewMapper {

        PlanetCursor cursor;

        View btnSubmit;
        ToolBar toolBar;
        PlanetView planetView;

        PlanetView planetBackground;
        ShadowView shadowBackground;

        ActionEditText etPlanetName;

        public ViewMapper( ) {
            cursor = findViewById( R.id.cursor );
            btnSubmit = findViewById( R.id.submit );
            toolBar = findViewById( R.id.toolBar );
            planetView = findViewById( R.id.planet_name_icon );

            planetBackground = findViewById( R.id.planet_planet_name_background );
            shadowBackground = findViewById( R.id.shadow_planet_name_background );
            etPlanetName = findViewById( R.id.et_planet_name_name );
        }
    }
}
