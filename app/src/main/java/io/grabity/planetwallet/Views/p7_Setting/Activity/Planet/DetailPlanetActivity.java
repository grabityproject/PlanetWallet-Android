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
        if ( getSerialize( C.bundleKey.PLANET ) != null ) {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            viewMapper.textType.setText( String.format( "%s Universe", planet.getCoinType( ).name( ) ) );
            viewMapper.textAddressType.setText( String.format( "%s Address", planet.getCoinType( ).name( ) ) );
            viewMapper.planetView.setData( planet.getAddress( ) );
            viewMapper.textAddress.setText( planet.getAddress( ) );
            viewMapper.textName.setText( planet.getName( ) );
        } else {
            finish( );
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
            sendAction( C.requestCode.PLANET_MNEMONIC_EXPORT, PinCodeCertificationActivity.class, Utils.createIntBundle( C.bundleKey.MNEMONIC, PinCodeCertificationActivity.MNEMONIC ) );
        } else if ( v == viewMapper.btnPrivateKey ) {
            setTransition( Transition.NO_ANIMATION );
            sendAction( C.requestCode.PLANET_PRIVATEKEY_EXPORT, PinCodeCertificationActivity.class, Utils.createIntBundle( C.bundleKey.PRIVATEKEY, PinCodeCertificationActivity.PRIVATEKEY ) );
        } else if ( v == viewMapper.btnName ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.PLANET_RENAME, RenamePlanetActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.PLANET_RENAME && resultCode == RESULT_OK ) {
            if ( data == null ) return;
            planet = ( Planet ) data.getSerializableExtra( C.bundleKey.PLANET );
            viewMapper.textName.setText( planet.getName( ) );
        } else if ( requestCode == C.requestCode.PLANET_MNEMONIC_EXPORT && resultCode == RESULT_OK ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( MnemonicExportActivity.class );
        } else if ( requestCode == C.requestCode.PLANET_PRIVATEKEY_EXPORT && resultCode == RESULT_OK ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( PrivateKeyExportActivity.class );
        }

    }

    @Override
    public void onToggle( ToggleButton toggleButton, boolean isOn ) {
        PLog.e( "토글버튼클릭 : " + isOn );
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
