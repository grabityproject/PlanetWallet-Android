package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.Common.components.PlanetWalletFragment;
import io.grabity.planetwallet.Common.components.ViewPagerAdapter;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.Views.p3_Wallet.Fragment.MnemonicImportFragment;
import io.grabity.planetwallet.Views.p3_Wallet.Fragment.PrivateKeyImportFragment;
import io.grabity.planetwallet.Widgets.LockableViewPager;
import io.grabity.planetwallet.Widgets.TabBar;
import io.grabity.planetwallet.Widgets.ToolBar;


public class WalletImportActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ViewPager.OnPageChangeListener {

    private ViewMapper viewMapper;
    private ViewPagerAdapter< PlanetWalletFragment > adapter;
    private ArrayList< PlanetWalletFragment > fragments;
    private Planet planet;

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

        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.tabBar.setItems(
                new TabBar.ButtonItem( ).setText( localized( R.string.wallet_import_tabbar_mnemonic_title ) ).setTextSize( 14 ),
                new TabBar.ButtonItem( ).setText( localized( R.string.wallet_import_tabbar_privatekey_title ) ).setTextSize( 14 )
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
        fragments = new ArrayList<>( );
        fragments.add( MnemonicImportFragment.newInstance( ) );
        fragments.add( PrivateKeyImportFragment.newInstance( ) );

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
            this.planet = planet;
            new Get( this ).action( Route.URL( "planet", "search", CoinType.of( planet.getCoinType( ) ).name( ) ), 0, 0, "{\"q\"=\"" + planet.getAddress( ) + "\"}" );
        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( !error ) {
            if ( requestCode == 0 && statusCode == 200 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Planet.class );
                if ( returnVO.isSuccess( ) ) {
                    ArrayList< Planet > planets = ( ArrayList< Planet > ) returnVO.getResult( );
                    if ( planets.size( ) > 0 ) {
                        this.planet.setName( planets.get( 0 ).getName( ) );
                    }
                }

                setTransition( PlanetWalletActivity.Transition.SLIDE_UP );
                sendAction( getRequestCode( ) == C.requestCode.MAIN_PLANET_ADD ? C.requestCode.MAIN_PLANET_ADD : C.requestCode.PLANET_ADD, PlanetNameActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
            }
        }

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.PLANET_ADD || requestCode == C.requestCode.MAIN_PLANET_ADD && resultCode == RESULT_OK ) {
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