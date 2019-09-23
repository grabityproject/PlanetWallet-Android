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

public class MnemonicExportActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mnemonic_export );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            onBackPressed( );
        } else {

            Planet planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            String mnemonic = planet.getMnemonic( KeyPairStore.getInstance( ), getPlanetWalletApplication( ).getPINCODE( ) );
            viewMapper.etMnemonic.setHint( mnemonic );
            viewMapper.groupMnemonic.setBackground_color_normal( Color.parseColor( getCurrentTheme( ) ? "#1E1E28" : "#F5F5F5" ) );
            viewMapper.groupMnemonic.setBorder_color_normal( Color.parseColor( getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );


            if ( Utils.equals( CoinType.BTC.getCoinType( ), planet.getCoinType( ) ) ) {
                Utils.setPreferenceData( this, C.pref.BACK_UP_MNEMONIC_BTC, String.valueOf( true ) );
            } else if ( Utils.equals( CoinType.ETH.getCoinType( ), planet.getCoinType( ) ) ) {
                Utils.setPreferenceData( this, C.pref.BACK_UP_MNEMONIC_ETH, String.valueOf( true ) );
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
        ToolBar toolBar;
        RoundRelativeLayout groupMnemonic;
        EditText etMnemonic;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            etMnemonic = findViewById( R.id.et_mnemonic_export_mnemonic );
            groupMnemonic = findViewById( R.id.group_mnemonic_export_mnemonic );
        }
    }
}
