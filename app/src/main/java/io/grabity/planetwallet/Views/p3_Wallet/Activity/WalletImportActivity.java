package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.Common.components.ViewPagerAdapter;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p3_Wallet.Fragment.JSONImportFragment;
import io.grabity.planetwallet.Views.p3_Wallet.Fragment.MnemonicImportFragment;
import io.grabity.planetwallet.Views.p3_Wallet.Fragment.PrivateKeyImportFragment;
import io.grabity.planetwallet.Widgets.LockableViewPager;
import io.grabity.planetwallet.Widgets.TabBar;
import io.grabity.planetwallet.Widgets.ToolBar;


public class WalletImportActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener , ViewPager.OnPageChangeListener {

    private ViewMapper viewMapper;
    private ViewPagerAdapter< PlanetWalletFragment > adapter;
    private ArrayList< PlanetWalletFragment > fragments;

    private JSONImportFragment jsonImportFragment;
    private MnemonicImportFragment mnemonicImportFragment;
    private PrivateKeyImportFragment privateKeyImportFragment;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_wallet_import );
        viewMapper = new ViewMapper( );

        viewInit( );
        setData( );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.tabBar.setItems(
                new TabBar.ButtonItem(  ).setText( "Mnemonic" ).setTextSize( 14 ).setTextColor( Color.parseColor( "#5C5964" ) ),
                new TabBar.ButtonItem(  ).setText( "Json" ).setTextSize( 14 ).setTextColor( Color.parseColor( "#5C5964" ) ),
                new TabBar.ButtonItem(  ).setText( "Private Key" ).setTextSize( 14 ).setTextColor( Color.parseColor( "#5C5964" ) )
        );

        ( ( TextView ) viewMapper.tabBar.getItems( ).get( 0 ).getView( ) ).setTextColor( Color.parseColor( "#FFFFFF" ) );
        viewMapper.tabBar.setIndicatorColor( Color.parseColor( "#FFFFFF" ) );

        viewMapper.tabBar.setViewPager( viewMapper.viewPager );
        viewMapper.viewPager.addOnPageChangeListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        fragments = new ArrayList<>(  );
        fragments.add( mnemonicImportFragment = MnemonicImportFragment.newInstance() );
        fragments.add( jsonImportFragment = JSONImportFragment.newInstance() );
        fragments.add( privateKeyImportFragment = PrivateKeyImportFragment.newInstance() );

        adapter = new ViewPagerAdapter<>( getSupportFragmentManager() , fragments );
        viewMapper.viewPager.setAdapter( adapter );
    }

    @Override
    public void onPageScrolled ( int i, float v, int i1 ) {

    }

    @Override
    public void onPageSelected ( int position ) {
        for ( int i = 0; i < viewMapper.tabBar.getItems( ).size( ); i++ ) {
            ( ( TextView ) viewMapper.tabBar.getItems( ).get( i ).getView( ) ).setTextColor( Color.parseColor( i == position ? "#FFFFFF" : "#5C5964" ) );
        }
    }

    @Override
    public void onPageScrollStateChanged ( int i ) {

    }

    @Override
    public void onToolBarClick ( Object tag, View view ) {
        if( Utils.equals( tag , C.tag.TOOLBAR_BACK ) ){
            super.onBackPressed( );
        }
    }



    public class ViewMapper {

        ToolBar toolBar;
        TabBar tabBar;
        LockableViewPager viewPager;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            tabBar = findViewById( R.id.tabBar );
            viewPager = findViewById( R.id.viewPager );
        }

    }
}