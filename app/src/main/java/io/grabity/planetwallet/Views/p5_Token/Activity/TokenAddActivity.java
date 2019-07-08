package io.grabity.planetwallet.Views.p5_Token.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.Common.components.ViewPagerAdapter;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p5_Token.Fragment.CustomTokenFragment;
import io.grabity.planetwallet.Views.p5_Token.Fragment.TokenListFragment;
import io.grabity.planetwallet.Widgets.LockableViewPager;
import io.grabity.planetwallet.Widgets.TabBar;
import io.grabity.planetwallet.Widgets.ToolBar;


public class TokenAddActivity extends PlanetWalletActivity implements ViewPager.OnPageChangeListener, ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private ViewPagerAdapter< PlanetWalletFragment > adapter;
    private ArrayList< PlanetWalletFragment > fragments;

    private TokenListFragment tokenListFragment;
    private CustomTokenFragment customTokenFragment;

    private Planet planet;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_token_add );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.tabBar.setItems(
                new TabBar.ButtonItem( ).setText( localized( R.string.token_add_tabbar_add_token_title ) ).setTextSize( 14 ),
                new TabBar.ButtonItem( ).setText( localized( R.string.token_add_tabbar_custom_token_title ) ).setTextSize( 14 )
        );

        viewMapper.tabBar.setViewPager( viewMapper.viewPager );
        viewMapper.viewPager.addOnPageChangeListener( this );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        viewMapper.tabBar.setTheme( getPlanetWalletApplication( ).getCurrentTheme( ) );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            onBackPressed( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );

            fragments = new ArrayList<>( );
            fragments.add( tokenListFragment = TokenListFragment.newInstance( ) );
            fragments.add( customTokenFragment = CustomTokenFragment.newInstance( ) );

            adapter = new ViewPagerAdapter<>( getSupportFragmentManager( ), fragments );
            viewMapper.viewPager.setAdapter( adapter );
        }

    }

    public Planet getPlanet( ) {
        return planet;
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

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            setResult( RESULT_OK );
            super.onBackPressed( );
        }
    }

    @Override
    public void onBackPressed( ) {
        setResult( RESULT_OK );
        super.onBackPressed( );

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