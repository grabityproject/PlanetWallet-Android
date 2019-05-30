package io.grabity.planetwallet.Views.p3_Wallet.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.AbsPopupView.PopupView;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Coin;
import io.grabity.planetwallet.Views.p3_Wallet.Adapter.PopupWalletAddAdapter;
import io.grabity.planetwallet.Widgets.ListPopupView.ListPopup;
import io.grabity.planetwallet.Widgets.ToolBar;


public class WalletAddActivity extends PlanetWalletActivity implements ListPopup.OnListPopupItemClickListener, ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private PopupWalletAddAdapter adapter;
    private ArrayList< Coin > items;

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
        viewMapper.btnCreate.setOnClickListener( this );
        viewMapper.btnImport.setOnClickListener( this );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        if ( !Utils.getPreferenceData( this, C.pref.WALLET_GENERATE, "" ).equals( C.wallet.CREATE ) )
            viewMapper.toolBar.getButtonItems( ).get( 0 ).getView( ).setVisibility( View.GONE );
    }

    @Override
    protected void setData( ) {
        super.setData( );
    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnCreate ) {

            if ( Utils.getPreferenceData( this, C.pref.WALLET_GENERATE, "" ).equals( C.wallet.CREATE ) ) {

                items = new ArrayList<>( );
                items.add( new Coin( "BTC Universe", R.drawable.icon_bit ) );
                items.add( new Coin( "ETH Universe", R.drawable.icon_eth ) );

                adapter = new PopupWalletAddAdapter( this, items );
                ListPopup.newInstance( this, !getPlanetWalletApplication( ).getCurrentTheme( ) ).
                        setAdapter( adapter ).
                        setOnListPopupItemClickListener( this ).
                        show( );

            } else {
                setTransition( Transition.SLIDE_UP );
                sendAction( C.requestCode.WALLET_CREATE, PlanetGenerateActivity.class );
            }


        } else if ( v == viewMapper.btnImport ) {
            setTransition( Transition.SLIDE_SIDE );
            sendAction( WalletImportActivity.class );
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.WALLET_CREATE && resultCode == RESULT_OK ) {
            finish( );
        }
    }

    @Override
    public void onListPopupItemClick( PopupView popup, View view, int position ) {
        //Todo BTC,ETH 분기로 지갑생성처리
        setTransition( Transition.SLIDE_UP );
        sendAction( C.requestCode.WALLET_CREATE, PlanetGenerateActivity.class );
        super.onBackPressed( );
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
            btnCreate = findViewById( R.id.btn_wallet_add_createSubmit );
            btnImport = findViewById( R.id.btn_wallet_add_importSubmit );

        }

    }
}