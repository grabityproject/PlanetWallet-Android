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

        viewMapper.btnPlanetName.setOnClickListener( this );
        viewMapper.btnMnemonic.setOnClickListener( this );
        viewMapper.btnPrivateKey.setOnClickListener( this );
        viewMapper.toggleButton.setOnToggleListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) != null ) {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            viewMapper.textType.setText( String.format( "%s Address", planet.getCoinType( ).name( ) ) );
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
        if ( v == viewMapper.btnPlanetName ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.PLANET_RENAME, RenamePlanetActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
        } else if ( v == viewMapper.btnMnemonic ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( MnemonicExportActivity.class );
        } else if ( v == viewMapper.btnPrivateKey ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( PrivateKeyExportActivity.class );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.PLANET_RENAME && resultCode == RESULT_OK ) {
            if ( data == null ) return;
            planet = ( Planet ) data.getSerializableExtra( C.bundleKey.PLANET );
            viewMapper.textName.setText( planet.getName( ) );
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
        TextView textName;

        ViewGroup btnPlanetName;
        ViewGroup btnMnemonic;
        ViewGroup btnPrivateKey;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            toggleButton = findViewById( R.id.toggleBtn );
            textType = findViewById( R.id.text_detail_planet_type );
            textAddress = findViewById( R.id.text_detail_planet_address );
            textName = findViewById( R.id.text_detail_planet_name );
            btnPlanetName = findViewById( R.id.group_detail_planet_name );
            btnMnemonic = findViewById( R.id.group_detail_planet_mnemonic_backup );
            btnPrivateKey = findViewById( R.id.group_detail_planet_privatekey_backup );

        }
    }
}
