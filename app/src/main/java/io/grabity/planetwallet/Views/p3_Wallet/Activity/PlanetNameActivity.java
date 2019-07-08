package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.KeyboardManager;
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
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
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.CustomToast;
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
        findViewById( android.R.id.content ).getViewTreeObserver( ).addOnGlobalLayoutListener( this );

        viewMapper.btnSubmit.setOnClickListener( this );
        ( ( ViewGroup.MarginLayoutParams ) viewMapper.toolBar.getLayoutParams( ) ).height = ( int ) ( Utils.dpToPx( this, 68 ) + getResources( ).getDimensionPixelSize( getResources( ).getIdentifier( "status_bar_height", "dimen", "android" ) ) );
        viewMapper.toolBar.requestLayout( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

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
        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            onBackPressed( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );

            planet.setName( viewMapper.etPlanetName.getText( ).toString( ) );
            viewMapper.planetView.setData( planet.getAddress( ) );
            viewMapper.planetBackground.setData( planet.getAddress( ) );

        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnSubmit ) {

            if ( viewMapper.etPlanetName.getText( ).length( ) == 0 ) {
                CustomToast.makeText( this, localized( R.string.planet_name_name_not_blank_title ) ).show( );
                return;
            }

            planet.setName( viewMapper.etPlanetName.getText( ).toString( ) );

            Planet request = new Planet( );
            request.setPlanet( planet.getName( ) );
            request.setSignature(
                    Signer.getInstance( ).sign( planet.getName( ),
                            planet.getPrivateKey( KeyPairStore.getInstance( ), getPlanetWalletApplication( ).getPINCODE( ) ) ) );
            request.setAddress( planet.getAddress( ) );

            new Post( this ).action( Route.URL( "planet", CoinType.of( planet.getCoinType( ) ).name( ) ), 0, 0, request );

        }
    }


    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( statusCode == 200 ) {
            if ( requestCode == 0 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Planet.class );
                if ( returnVO.isSuccess( ) ) {

                    PlanetStore.getInstance( ).save( planet );

                    if ( getRequestCode( ) == C.requestCode.PLANET_ADD ) {
                        setResult( RESULT_OK );
                        super.onBackPressed( );

                    } else {
                        sendAction( MainActivity.class );
                        finish( );

                    }

                } else {

                    PLog.e( "returnVO.getResult( ) : " + returnVO.getResult( ).getClass( ) );
                    PLog.e( result );

                }
            }
        } else {
            ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, ErrorResult.class );
            ErrorResult errorResult = ( ErrorResult ) returnVO.getResult( );
            CustomToast.makeText( this, errorResult.getErrorMsg( ) ).show( );
//            if ( errorResult == null ) {
//                CustomToast.makeText( this, "server error" ).show( );
//            } else{
//                CustomToast.makeText( this, errorResult.getErrorMsg( ) ).show( );
//            }

        }
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

//            if ( viewMapper.etPlanetName.getText( ).length( ) == 0 ) {
//                //Todo 모두 다 지우고 키보드를 닫는경우
//                CustomToast.makeText( this, "이름은 공백일수 없습니다." ).show( );
//            }
        } else {
            if ( viewMapper.etPlanetName.getText( ).toString( ).contains( " " ) )
                if ( first ) {
                    first = false;
                    viewMapper.etPlanetName.setText( viewMapper.etPlanetName.getText( ).toString( ).trim( ) );
                    viewMapper.etPlanetName.setSelection( viewMapper.etPlanetName.getText( ).length( ) );
                }
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }

    public class ViewMapper {

        View btnSubmit;
        ToolBar toolBar;
        PlanetView planetView;

        PlanetView planetBackground;
        ShadowView shadowBackground;

        EditText etPlanetName;

        public ViewMapper( ) {
            btnSubmit = findViewById( R.id.submit );
            toolBar = findViewById( R.id.toolBar );
            planetView = findViewById( R.id.planet_name_icon );

            planetBackground = findViewById( R.id.planet_planet_name_background );
            shadowBackground = findViewById( R.id.shadow_planet_name_background );
            etPlanetName = findViewById( R.id.et_planet_name_name );
        }
    }
}
