package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Widgets.ToolBar;


public class PlanetGenerateActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_planet_generate );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.btnSelect.setOnClickListener( this );
        viewMapper.btnRefresh.setOnClickListener( this );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
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
            Toast.makeText( this, "새로운 이미지 교체", Toast.LENGTH_SHORT ).show( );
        } else if ( v == viewMapper.btnSelect ) {
            Utils.setPreferenceData( this, C.pref.WALLET_GENERATE, C.wallet.CREATE );
            sendAction( MainActivity.class );
            setResult( RESULT_OK );
            finish( );
        }
    }

    public class ViewMapper {

        View btnRefresh;
        View btnSelect;
        ToolBar toolBar;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            btnRefresh = findViewById( R.id.btn_planet_generate_refresh );
            btnSelect = findViewById( R.id.btn_planet_generate_select );

        }

    }
}