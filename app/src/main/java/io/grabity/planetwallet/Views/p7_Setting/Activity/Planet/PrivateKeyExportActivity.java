package io.grabity.planetwallet.Views.p7_Setting.Activity.Planet;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;
import io.grabity.planetwallet.Widgets.ToolBar;

public class PrivateKeyExportActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_privatekey_export );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            onBackPressed( );
        } else {
            Planet planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            String privateKey = null;
            if ( planet.getCoinType( ).equals( CoinType.ETH.getCoinType( ) ) ) {
                privateKey = planet.getPrivateKey( KeyPairStore.getInstance( ), getPlanetWalletApplication( ).getPINCODE( ) );
            } else {
                privateKey = planet.getPrivateKeyBase58Encode( KeyPairStore.getInstance( ), getPlanetWalletApplication( ).getPINCODE( ) );
            }


            PLog.e( "privateKey : " + privateKey );
            viewMapper.etPrivateKey.setHint( privateKey );
            viewMapper.groupPrivateKey.setBackground_color_normal( Color.parseColor( getCurrentTheme( ) ? "#1E1E28" : "#F5F5F5" ) );
            viewMapper.groupPrivateKey.setBorder_color_normal( Color.parseColor( getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );
        }

    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }


    public class ViewMapper {
        ToolBar toolBar;
        EditText etPrivateKey;
        RoundRelativeLayout groupPrivateKey;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            etPrivateKey = findViewById( R.id.et_privatekey_export_privatekey );
            groupPrivateKey = findViewById( R.id.group_privatekey_export_privatekey );
        }
    }
}
