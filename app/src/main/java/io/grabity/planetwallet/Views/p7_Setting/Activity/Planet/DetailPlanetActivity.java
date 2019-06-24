package io.grabity.planetwallet.Views.p7_Setting.Activity.Planet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyValueStore;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ToggleButton;
import io.grabity.planetwallet.Widgets.ToolBar;

public class DetailPlanetActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ToggleButton.OnToggleListener {

    private ViewMapper viewMapper;
    private Planet planet;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_planet );

        viewMapper = new ViewMapper( );
        viewInit( );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnMnemonic.setOnClickListener( this );
        viewMapper.btnPrivateKey.setOnClickListener( this );
        viewMapper.btnName.setOnClickListener( this );
        viewMapper.toggleButton.setOnToggleListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            finish( );
        } else {

            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            planet = PlanetStore.getInstance( ).getPlanet( planet.getKeyId( ) );

            viewMapper.btnMnemonic.setVisibility( planet.getPathIndex( ) != -1 ? View.VISIBLE : View.GONE );

            viewMapper.textType.setText( String.format( "%s Universe", CoinType.of( planet.getCoinType( ) ).name( ) ) );
            viewMapper.textAddressType.setText( String.format( "%s Address", CoinType.of( planet.getCoinType( ) ).name( ) ) );
            viewMapper.toolBar.setTitle( planet.getName( ) );
            viewMapper.planetView.setData( planet.getAddress( ) );
            viewMapper.textAddress.setText( planet.getAddress( ) );
            viewMapper.textName.setText( planet.getName( ) );
            viewMapper.toggleButton.setOn( Utils.equals( planet.getHide( ), "Y" ) );

        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnMnemonic ) {
            setTransition( Transition.NO_ANIMATION );
            sendAction(
                    C.requestCode.PLANET_MNEMONIC_EXPORT,
                    PinCodeCertificationActivity.class );

        } else if ( v == viewMapper.btnPrivateKey ) {
            setTransition( Transition.NO_ANIMATION );
            sendAction(
                    C.requestCode.PLANET_PRIVATEKEY_EXPORT,
                    PinCodeCertificationActivity.class );

        } else if ( v == viewMapper.btnName ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.PLANET_RENAME, RenamePlanetActivity.class,
                    Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );

        }

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.PLANET_MNEMONIC_EXPORT && resultCode == RESULT_OK ) {

            assert data != null;
            char[] pinCode = data.getCharArrayExtra( C.bundleKey.PINCODE );
            if ( pinCode != null ) {
                StringBuilder pinCodeString = new StringBuilder( );
                for ( char c : pinCode ) {
                    pinCodeString.append( c );
                }
                if ( Utils.equals( Utils.sha256( pinCodeString.toString( ) ), KeyValueStore.getInstance( ).getValue( C.pref.PASSWORD, pinCodeString.toString( ).toCharArray( ) ) ) ) {
                    setTransition( Transition.SLIDE_UP );
                    sendAction( MnemonicExportActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
                }
            }


        } else if ( requestCode == C.requestCode.PLANET_PRIVATEKEY_EXPORT && resultCode == RESULT_OK ) {

            assert data != null;
            char[] pinCode = data.getCharArrayExtra( C.bundleKey.PINCODE );
            if ( pinCode != null ) {
                StringBuilder pinCodeString = new StringBuilder( );
                for ( char c : pinCode ) {
                    pinCodeString.append( c );
                }
                if ( Utils.equals( Utils.sha256( pinCodeString.toString( ) ), KeyValueStore.getInstance( ).getValue( C.pref.PASSWORD, pinCodeString.toString( ).toCharArray( ) ) ) ) {
                    setTransition( Transition.SLIDE_UP );
                    sendAction( PrivateKeyExportActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
                }
            }

        }

    }

    @Override
    public void onToggle( ToggleButton toggleButton, boolean isOn ) {
        Planet planet = new Planet( );
        planet.setKeyId( this.planet.getKeyId( ) );
        planet.setHide( isOn ? "Y" : "N" );
        PlanetStore.getInstance( ).update( planet );
    }

    public class ViewMapper {

        ToolBar toolBar;
        ToggleButton toggleButton;

        TextView textType;
        TextView textAddress;
        TextView textAddressType;
        TextView textName;

        View btnName;

        ViewGroup btnMnemonic;
        ViewGroup btnPrivateKey;

        PlanetView planetView;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            toggleButton = findViewById( R.id.toggleBtn );
            textType = findViewById( R.id.text_detail_planet_type );
            textAddress = findViewById( R.id.text_detail_planet_address );
            textAddressType = findViewById( R.id.text_detail_planet_address_type );
            textName = findViewById( R.id.text_detail_planet_name );
            textType = findViewById( R.id.text_detail_planet_type );


            btnName = findViewById( R.id.btn_detail_planet_name_change );
            btnMnemonic = findViewById( R.id.group_detail_planet_mnemonic_backup );
            btnPrivateKey = findViewById( R.id.group_detail_planet_privatekey_backup );

            planetView = findViewById( R.id.planet_detail_planet_planetview );

        }
    }
}
