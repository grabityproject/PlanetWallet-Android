package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.Common.components.ViewPagerAdapter;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p3_Wallet.Fragment.JSONImportFragment;
import io.grabity.planetwallet.Views.p3_Wallet.Fragment.MnemonicImportFragment;
import io.grabity.planetwallet.Views.p3_Wallet.Fragment.PrivateKeyImportFragment;
import io.grabity.planetwallet.Widgets.LockableViewPager;
import io.grabity.planetwallet.Widgets.TabBar;
import io.grabity.planetwallet.Widgets.ToolBar;


public class WalletImportActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ViewPager.OnPageChangeListener {

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
                new TabBar.ButtonItem( ).setText( localized( R.string.wallet_import_tabbar_mnemonic_title ) ).setTextSize( 14 ),
//                new TabBar.ButtonItem( ).setText( "Json" ).setTextSize( 14 ),
                new TabBar.ButtonItem( ).setText( localized( R.string.wallet_import_tabbar_privatekey_title ) ).setTextSize( 14 )
        );

        viewMapper.tabBar.setViewPager( viewMapper.viewPager );
        viewMapper.viewPager.addOnPageChangeListener( this );
//        viewMapper.viewPager.setOffscreenPageLimit( 3 );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        viewMapper.tabBar.setTheme( getPlanetWalletApplication( ).getCurrentTheme( ) );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        fragments = new ArrayList<>( );
        fragments.add( mnemonicImportFragment = MnemonicImportFragment.newInstance( ) );
//        fragments.add( jsonImportFragment = JSONImportFragment.newInstance( ) );
        fragments.add( privateKeyImportFragment = PrivateKeyImportFragment.newInstance( ) );

        adapter = new ViewPagerAdapter<>( getSupportFragmentManager( ), fragments );
        viewMapper.viewPager.setAdapter( adapter );


    }

    @Override
    public void onPageScrolled( int i, float v, int i1 ) {

    }

    @Override
    public void onPageSelected( int position ) {
        Utils.hideKeyboard( this, getCurrentFocus( ) );
    }

    @Override
    public void onPageScrollStateChanged( int i ) {

    }

    public void setPlanet( Planet planet ) {

        if ( planet != null ) {
            setTransition( PlanetWalletActivity.Transition.SLIDE_UP );
            sendAction( C.requestCode.PLANET_ADD, PlanetNameActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
        }

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.PLANET_ADD && resultCode == RESULT_OK ) {
            setResult( RESULT_OK );
            super.onBackPressed( );
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
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