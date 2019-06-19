package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.BTC;
import io.grabity.planetwallet.VO.MainItems.CoinType;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p6_Transfer.Adapter.TransferAdapter;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.DetailPlanetActivity;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.BarcodeReaderView;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, TextWatcher, AdvanceRecyclerView.OnItemClickListener {

    private ViewMapper viewMapper;


    //Dummy
    private ArrayList< Planet > allPlanets;
    private ArrayList< Planet > filterPlanets;

    private TransferAdapter adapter;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_transfer );
        viewMapper = new ViewMapper( );

        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setRightButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_TRANSFER_QRCODE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnClip.setOnClickListener( this );
        viewMapper.btnClear.setOnClickListener( this );

        viewMapper.listView.setOnItemClickListener( this );
        viewMapper.groupSearchAddress.setOnClickListener( this );

        viewMapper.etSearch.addTextChangedListener( this );

        searchViewThemeSet( );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        allPlanets = new ArrayList<>( );
        filterPlanets = new ArrayList<>( );

        setDummy( );

    }

    void searchViewThemeSet( ) {
        if ( getCurrentTheme( ) ) {
            viewMapper.etSearch.setTextColor( Color.parseColor( "#000000" ) );
            viewMapper.etSearch.setHintTextColor( Color.parseColor( "#aaaaaa" ) );
            viewMapper.etSearch.setBackgroundColor( Color.parseColor( "#FCFCFC" ) );
        } else {
            viewMapper.etSearch.setTextColor( Color.parseColor( "#FFFFFF" ) );
            viewMapper.etSearch.setHintTextColor( Color.parseColor( "#5C5964" ) );
            viewMapper.etSearch.setBackgroundColor( Color.parseColor( "#111117" ) );
        }
    }

    private void updateSearchView( ) {
        viewMapper.imageNotSearch.setVisibility( viewMapper.etSearch.getText( ).length( ) == 0 ? View.VISIBLE : View.INVISIBLE );
        viewMapper.btnClear.setVisibility( viewMapper.etSearch.getText( ).length( ) == 0 ? View.GONE : View.VISIBLE );
        viewMapper.imageSearch.setVisibility( viewMapper.etSearch.getText( ).length( ) >= 1 ? View.VISIBLE : View.INVISIBLE );
        viewMapper.groupSearchAddress.setVisibility( viewMapper.etSearch.getText( ).length( ) < 34 ? View.GONE : View.VISIBLE );


        if ( viewMapper.etSearch.getText( ).length( ) == 0 ) {
            filterPlanets.clear( );
        }
    }


    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            super.onBackPressed( );
        } else if ( Utils.equals( tag, C.tag.TOOLBAR_TRANSFER_QRCODE ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.QR_CODE, ScanQRActivity.class );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnClear ) {
            viewMapper.etSearch.setText( "" );
            updateSearchView( );
        } else if ( v == viewMapper.groupSearchAddress ) {
            //Todo 임시
            setTransition( Transition.SLIDE_SIDE );
            sendAction( TransferAmountActivity.class, Utils.createStringBundle( "address", viewMapper.textAddress.getText( ).toString( ) ) );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {
        //Todo 임시
        setTransition( Transition.SLIDE_SIDE );
        sendAction( TransferAmountActivity.class, Utils.createStringBundle( "planet", filterPlanets.get( position ).getAddress( ) ) );

    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
    }

    @Override
    public void afterTextChanged( Editable s ) {
        filterPlanets.clear( );
        //Todo 임시 : 우선 주소자릿수만 비교해서 이름검색인지 주소검색인지 판별
        if ( viewMapper.etSearch.getText( ).length( ) < 34 ) {
            for ( int i = 0; i < allPlanets.size( ); i++ ) {
                if ( allPlanets.get( i ).getName( ).toLowerCase( ).contains( viewMapper.etSearch.getText( ).toString( ).toLowerCase( ) ) ) {
                    filterPlanets.add( allPlanets.get( i ) );
                }
            }

            if ( filterPlanets.size( ) == 0 ) {
                viewMapper.textNoItem.setVisibility( View.VISIBLE );
            } else {
                viewMapper.textNoItem.setVisibility( View.GONE );
            }


        } else {
            viewMapper.textAddress.setText( viewMapper.etSearch.getText( ).toString( ) );

        }
        adapter.notifyDataSetChanged( );
        updateSearchView( );

    }


    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.QR_CODE && resultCode == RESULT_OK ) {
            if ( data == null ) return;
            String address = data.getStringExtra( C.bundleKey.QRCODE );
            viewMapper.etSearch.setText( address );
        }
    }

    public class ViewMapper {

        ViewGroup groupSearchAddress;
        StretchImageView imageIcon;
        TextView textAddress;

        ToolBar toolBar;
        EditText etSearch;
        StretchImageView imageNotSearch;
        StretchImageView imageSearch;
        CircleImageView btnClear;
        AdvanceRecyclerView listView;

        View textNoItem;
        TextView btnClip;

        public ViewMapper( ) {

            groupSearchAddress = findViewById( R.id.group_transfer_address_search );
            imageIcon = findViewById( R.id.image_transfer_address_search_icon );
            textAddress = findViewById( R.id.text_transfer_address_search_address );

            toolBar = findViewById( R.id.toolBar );
            etSearch = findViewById( R.id.et_transfer_search );
            imageNotSearch = findViewById( R.id.image_transfer_nosearch_icon );
            imageSearch = findViewById( R.id.image_transfer_search_icon );
            btnClear = findViewById( R.id.btn_transfer_clear );
            listView = findViewById( R.id.listView );
            textNoItem = findViewById( R.id.text_transfer_noitem );
            btnClip = findViewById( R.id.btn_transfer_clip );

        }
    }


    void setDummy( ) {

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.BTC );
            planet.setName( "Jacob Park" );
            planet.setAddress( "n1XYu73xiYzzPeeXNRighWHVRHsNrCXPAF" );

            allPlanets.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.BTC );
            planet.setName( "choi" );
            planet.setAddress( "3QSfDbpgf1sysw73Hm9CDBHoVe54MVujQ7" );

            allPlanets.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.BTC );
            planet.setName( "Park" );
            planet.setAddress( "4QSfDbpgf1sysw72Am9CDBHoVe54MVujQ7" );
            allPlanets.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.BTC );
            planet.setName( "JeongHyun" );
            planet.setAddress( "1QSfDbpgf1sysw73Hm9CDBHoVe54MVujQ7" );
            allPlanets.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.ETH );
            planet.setName( "JeongHyun" );
            planet.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
            allPlanets.add( planet );

            ArrayList< MainItem > items = new ArrayList<>( );
            {
                ETH item = new ETH( );
                item.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
                item.setBalance( "12.0" );
                item.setName( "ETH" );
                item.setPrice( "3600$" );
                items.add( item );
            }

            {
                ERC20 item = new ERC20( );
                item.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
                item.setBalance( "244500" );
                item.setName( "GBT" );
                item.setPrice( "0$" );
                item.setIconRes( R.drawable.icon_gbt );
                items.add( item );
            }

            {
                ERC20 item = new ERC20( );
                item.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
                item.setBalance( "50.0" );
                item.setName( "iOTA" );
                item.setPrice( "0$" );
                item.setIconRes( R.drawable.icon_iota );
                items.add( item );
            }

            {
                ERC20 item = new ERC20( );
                item.setAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
                item.setBalance( "0.0" );
                item.setName( "OMG" );
                item.setPrice( "0$" );
                item.setIconRes( R.drawable.icon_omg );
                items.add( item );
            }
            planet.setItems( items );
            allPlanets.add( planet );
        }

        {
            Planet planet = new Planet( );
            planet.setCoinType( CoinType.ETH );
            planet.setName( "JeongHyun" );
            planet.setAddress( "0x9f1c94659d2c00b134a9ba418aa182f14bf72e56" );
            allPlanets.add( planet );
        }

        adapter = new TransferAdapter( this, filterPlanets );
        viewMapper.listView.setAdapter( adapter );
    }
}