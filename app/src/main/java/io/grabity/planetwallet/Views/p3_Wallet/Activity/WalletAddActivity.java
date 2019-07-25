package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.AbsPopupView.PopupView;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p3_Wallet.Adapter.PopupWalletAddAdapter;
import io.grabity.planetwallet.Widgets.ListPopupView.ListPopup;
import io.grabity.planetwallet.Widgets.ToolBar;


public class WalletAddActivity extends PlanetWalletActivity implements ListPopup.OnListPopupItemClickListener, ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private PopupWalletAddAdapter adapter;
    private ArrayList< Planet > items;

    private ListPopup popupCreate;
    private ListPopup popupImport;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_wallet_add );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnCreate.setOnClickListener( this );
        viewMapper.btnImport.setOnClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        items = new ArrayList<>( );
        {
            Planet item = new Planet( );
            item.setCoinType( CoinType.BTC.getCoinType( ) );
            items.add( item );
        }
        {
            Planet item = new Planet( );
            item.setCoinType( CoinType.ETH.getCoinType( ) );
            items.add( item );
        }
        adapter = new PopupWalletAddAdapter( this, items );
    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnCreate ) {

            popupCreate = ListPopup.newInstance( this,
                    !getPlanetWalletApplication( ).getCurrentTheme( ) ).
                    setAdapter( adapter ).
                    setOnListPopupItemClickListener( this );
            popupCreate.show( );

        } else if ( v == viewMapper.btnImport ) {

            popupImport = ListPopup.newInstance( this,
                    !getPlanetWalletApplication( ).getCurrentTheme( ) ).
                    setAdapter( adapter ).
                    setOnListPopupItemClickListener( this );
            popupImport.show( );

        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.PLANET_ADD && resultCode == RESULT_OK ) {
            setResult( RESULT_OK );
            onBackPressed( );
        }
    }

    @Override
    public void onListPopupItemClick( PopupView popup, View view, int position ) {
        super.onBackPressed( );
        if ( popup == popupCreate ) {
            new Handler( ).postDelayed( ( ) -> {
                setTransition( Transition.SLIDE_UP );
                sendAction( C.requestCode.PLANET_ADD, PlanetGenerateActivity.class, Utils.createIntBundle( C.bundleKey.COINTYPE, items.get( position ).getCoinType( ) ) );
            }, 200 );
        } else if ( popup == popupImport ) {
            new Handler( ).postDelayed( ( ) -> {
                setTransition( Transition.SLIDE_SIDE );
                sendAction( C.requestCode.PLANET_ADD, WalletImportActivity.class, Utils.createIntBundle( C.bundleKey.COINTYPE, items.get( position ).getCoinType( ) ) );
            }, 200 );
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
        View btnCreate, btnImport;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            btnCreate = findViewById( R.id.btn_wallet_add );
            btnImport = findViewById( R.id.btn_wallet_add_import );

        }

    }
}