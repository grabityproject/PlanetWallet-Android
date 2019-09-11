package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pentasecurity.cryptowallet.exceptions.DecryptionErrorException;

import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.FontManager;
import io.grabity.planetwallet.MiniFramework.managers.KeyboardManager;
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.managers.BitCoinManager;
import io.grabity.planetwallet.MiniFramework.wallet.managers.EthereumManager;
import io.grabity.planetwallet.MiniFramework.wallet.signer.Signer;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.ErrorResult;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Widgets.ActionEditText;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.PlanetCursor;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ShadowView;
import io.grabity.planetwallet.Widgets.ToolBar;


public class PlanetGenerateActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ViewTreeObserver.OnGlobalLayoutListener, KeyboardManager.KeyboardVisibilityEventListener, TextView.OnEditorActionListener, TextWatcher {

    private ViewMapper viewMapper;
    boolean first = true;

    private String plantName;
    private int cursor;

    private Planet planet;

    private boolean btcMaster = false;


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


        viewMapper.btnSelect.setOnClickListener( this );
        viewMapper.btnRefresh.setOnClickListener( this );

        viewMapper.toolBar.setTopMarginFullScreen( );


        viewMapper.btnRefresh.setBorderColor( Color.parseColor( !getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );
        viewMapper.btnSelect.setBorderColor( Color.parseColor( !getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );

        viewMapper.shadowBackground.setShadowColor(
                Color.parseColor( getCurrentTheme( ) ? "#FFFFFF" : "#000000" ),
                Color.parseColor( getCurrentTheme( ) ? "#C8FFFFFF" : "#A8000000" )
        );

        viewMapper.etPlanetName.setTypeface( FontManager.getInstance( ).getFont( FontManager.BOLD ) );
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
        try {
            if ( getRequestCode( ) == C.requestCode.PLANET_ADD || getRequestCode( ) == C.requestCode.MAIN_PLANET_ADD ) {

                viewMapper.toolBar.addLeftButton( ToolBar.ButtonItem( !getCurrentTheme( ) ? R.drawable.image_toolbar_close_gray : R.drawable.image_toolbar_close_blue ).setTag( C.tag.TOOLBAR_CLOSE ) );
                viewMapper.toolBar.setOnToolBarClickListener( this );

                if ( getInt( C.bundleKey.COINTYPE, -1 ) == CoinType.BTC.getCoinType( ) ) {


                    if ( KeyPairStore.getInstance( ).getMasterKeyPair( CoinType.BTC.getCoinType( ), C.PINCODE ) == null ) {

                        btcMaster = true;
                        generateBtcPlanet( );

                    } else {
                        addBtcPlanet( );
                    }

                } else if ( getInt( C.bundleKey.COINTYPE, -1 ) == CoinType.ETH.getCoinType( ) ) {

                    addEthPlanet( );

                } else {
                    finish( );
                }

            } else {
                generateEthPlanet( );
            }

            setPlanetName( );

        } catch ( DecryptionErrorException e ) {
            e.printStackTrace( );
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
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            super.onBackPressed( );
        }
    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnRefresh ) {

            try {
                if ( getRequestCode( ) == C.requestCode.PLANET_ADD || getRequestCode( ) == C.requestCode.MAIN_PLANET_ADD ) {
                    if ( getInt( C.bundleKey.COINTYPE, -1 ) == CoinType.BTC.getCoinType( ) ) {

                        if ( btcMaster ) {
                            generateBtcPlanet( );
                        } else {
                            addBtcPlanet( );
                        }

                    } else if ( getInt( C.bundleKey.COINTYPE, -1 ) == CoinType.ETH.getCoinType( ) ) {
                        addEthPlanet( );
                    }
                } else {

                    generateEthPlanet( );

                }
            } catch ( DecryptionErrorException e ) {
                e.printStackTrace( );
            }

        } else if ( v == viewMapper.btnSelect ) {

            if ( planet != null ) {

                if ( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).length( ) == 0 ) {
                    CustomToast.makeText( this, localized( R.string.planet_generate_name_not_blank_title ) ).show( );
                    return;
                }

                planet.setName( viewMapper.etPlanetName.getText( ).toString( ).trim( ) );


                Planet request = new Planet( );
                request.setPlanet( planet.getName( ) );
                request.setSignature(
                        Signer.getInstance( ).sign( planet.getName( ),
                                planet.getPrivateKey( KeyPairStore.getInstance( ), C.PINCODE ) ) );
                request.setAddress( planet.getAddress( ) );

                new Post( this ).action( Route.URL( "planet", CoinType.of( planet.getCoinType( ) ).name( ) ), 0, 0, request, Utils.createStringHashMap( "device-key", getPlanetWalletApplication( ).getDeviceKey( ) ) );

            }

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

                    //ETH Wallet GBT add
                    if ( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ) {
//                        defaultSave( );
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

//    void defaultSave( ) {
//        MainItem gbt = new MainItem( );
//        gbt.setKeyId( planet.getKeyId( ) );
//        gbt.setCoinType( CoinType.ERC20.getCoinType( ) );
//        gbt.setHide( "N" );
//        gbt.setName( "Grabity Coin" );
//        gbt.setSymbol( "GBT" );
//        gbt.setDecimals( "18" );
//        gbt.setImg_path( "/erc20/img/gbt.png" );
//        gbt.setContract( "0xcbD49182346421D3B410B04AeB1789346DA6Ce43" );
//
//        MainItemStore.getInstance( ).tokenSave( gbt );
//    }

    @Override
    public void onSetPinCode( ) {
        super.onSetPinCode( );
        setData( );
    }

    void setPlanetName( ) {

        viewMapper.etPlanetName.setText( Utils.randomPlanetName( this, planet.getAddress( ) ) );
        viewMapper.cursor.setX( ( ( Utils.getScreenWidth( this ) + Utils.getTextWidth( viewMapper.etPlanetName ) ) / 2.0f ) + Utils.dpToPx( this, 4 ) );
    }

    void addBtcPlanet( ) {
        if ( planet != null ) {
            KeyPairStore.getInstance( ).deleteKeyPair( planet.getKeyId( ) );
            planet = BitCoinManager.getInstance( ).addPlanet( planet.getPathIndex( ) + 1, C.PINCODE );

            if ( PlanetStore.getInstance( ).getPlanet( planet.getKeyId( ) ) != null ) {
                addBtcPlanet( );
                return;
            }
        } else {
            planet = BitCoinManager.getInstance( ).addPlanet( C.PINCODE );
            if ( PlanetStore.getInstance( ).getPlanet( planet.getKeyId( ) ) != null ) {
                addBtcPlanet( );
                return;
            }

        }
        planet.setName( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).toString( ) );
        viewMapper.planetView.setData( planet.getAddress( ) );
        viewMapper.planetBackground.setData( viewMapper.planetView.getData( ) );

    }


    void generateBtcPlanet( ) {
        char[] pinCode = C.PINCODE;
        if ( pinCode == null ) {
            return;
        }
        BitCoinManager.getInstance( ).generateMaster( pinCode );

        planet = BitCoinManager.getInstance( ).addPlanet( pinCode );
        planet.setName( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).toString( ) );

        viewMapper.planetView.setData( planet.getAddress( ) );
        viewMapper.planetBackground.setData( viewMapper.planetView.getData( ) );

    }


    void addEthPlanet( ) {
        if ( planet != null ) {
            KeyPairStore.getInstance( ).deleteKeyPair( planet.getKeyId( ) );
            planet = EthereumManager.getInstance( ).addPlanet( planet.getPathIndex( ) + 1, C.PINCODE );

            if ( PlanetStore.getInstance( ).getPlanet( planet.getKeyId( ) ) != null ) {
                addEthPlanet( );
                return;
            }
        } else {
            planet = EthereumManager.getInstance( ).addPlanet( C.PINCODE );

            if ( PlanetStore.getInstance( ).getPlanet( planet.getKeyId( ) ) != null ) {
                addEthPlanet( );
                return;
            }
        }
        planet.setName( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).toString( ) );
        viewMapper.planetView.setData( planet.getAddress( ) );
        viewMapper.planetBackground.setData( viewMapper.planetView.getData( ) );


    }

    void generateEthPlanet( ) {
        char[] pinCode = getIntent( ).getCharArrayExtra( C.bundleKey.PINCODE );
        EthereumManager.getInstance( ).generateMaster( pinCode );

        planet = EthereumManager.getInstance( ).addPlanet( pinCode );
        planet.setName( Objects.requireNonNull( viewMapper.etPlanetName.getText( ) ).toString( ) );

        viewMapper.planetView.setData( planet.getAddress( ) );
        viewMapper.planetBackground.setData( viewMapper.planetView.getData( ) );

    }

    //test
//    void randomPlanetName( ) {
//        PLog.e( "address Sha256 : " + Utils.sha256( planet.getAddress( ) ) );
//        String addressToSha256 = Utils.sha256( planet.getAddress( ) );
//        PLog.e( "name.length : " + name.length );
//
//        if ( addressToSha256 == null ) return;
//        int index = Utils.hexToDecimal( addressToSha256.substring( 0, 4 ) );
//        if ( index > name.length ) {
//            index = Utils.hexToDecimal( addressToSha256.substring( 0, 3 ) );
//        }
//
//        int last = Utils.hexToDecimal( addressToSha256.substring( addressToSha256.length( ) - 3 ) );
//
//        String planetname = name[ index ] + last;
//        PLog.e( "random name : " + planetname );
//
//
//    }

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


    public class ViewMapper {

        ViewGroup groupBtn;
        PlanetCursor cursor;

        CircleImageView btnRefresh;
        CircleImageView btnSelect;
        ToolBar toolBar;
        PlanetView planetView;

        PlanetView planetBackground;
        ShadowView shadowBackground;

        ActionEditText etPlanetName;

        public ViewMapper( ) {

            groupBtn = findViewById( R.id.group_planet_generate_refresh_select_btn );
            cursor = findViewById( R.id.cursor );

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