package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.Random;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.PlanetManagementActivity;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.PlanetView;
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

        viewMapper.btnRefresh.setBorderColor( Color.parseColor( !getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );
        viewMapper.btnSelect.setBorderColor( Color.parseColor( !getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );
    }

    @Override
    protected void setData( ) {
        super.setData( );
    }



    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            this.onBackPressed( );
        }
    }

    @Override
    public void onBackPressed( ) {
        setTransition( Transition.NO_ANIMATION );
        super.onBackPressed( );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnRefresh ) {
            viewMapper.planetView.setData( randomString( ) );
        } else if ( v == viewMapper.btnSelect ) {
            Utils.setPreferenceData( this, C.pref.WALLET_GENERATE, C.wallet.CREATE );

            if ( Utils.equals( getInt( C.bundleKey.PLANETADD, PlanetManagementActivity.PLANETADD ), PlanetManagementActivity.PLANETADD ) ) {
                setResult( RESULT_OK );
                super.onBackPressed( );
            } else {
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

    public class ViewMapper {

        CircleImageView btnRefresh;
        CircleImageView btnSelect;
        ToolBar toolBar;
        PlanetView planetView;

        public ViewMapper( ) {
            planetView = findViewById( R.id.planet_generate_icon );
            toolBar = findViewById( R.id.toolBar );
            btnRefresh = findViewById( R.id.btn_planet_generate_refresh );
            btnSelect = findViewById( R.id.btn_planet_generate_select );

        }

    }
}