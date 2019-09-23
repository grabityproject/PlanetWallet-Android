package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.managers.SyncManager;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.managers.BitCoinManager;
import io.grabity.planetwallet.MiniFramework.wallet.managers.EthereumManager;
import io.grabity.planetwallet.MiniFramework.wallet.store.SearchStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Views.p6_Transfer.Adapter.RecentSearchAdapter;
import io.grabity.planetwallet.Views.p6_Transfer.Adapter.TransferAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.OnInsideItemClickListener;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, TextWatcher, AdvanceRecyclerView.OnItemClickListener, OnInsideItemClickListener< Planet >, SyncManager.OnSyncListener {

    private ViewMapper viewMapper;
    private ArrayList< Planet > allPlanets;

    private Planet planet;
    private MainItem mainItem;
    private Tx tx;

    private boolean isQRScan = false;

    private ArrayList< Planet > searchPlanets;
    private RecentSearchAdapter recentSearchAdapter;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        getPlanetWalletApplication( ).recordActvityStack( this );
        setContentView( R.layout.activity_transfer );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setRightButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_TRANSFER_QRCODE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnClip.setOnClickListener( this );
        viewMapper.btnClear.setOnClickListener( this );

        viewMapper.listView.setOnItemClickListener( this );
        viewMapper.groupSearchAddress.setOnClickListener( this );

        viewMapper.etSearch.addTextChangedListener( this );
        viewMapper.etSearch.requestFocus( );

        searchViewThemeSet( );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.MAIN_ITEM ) == null ) {
            finish( );
        } else {
            allPlanets = new ArrayList<>( );

            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            mainItem = ( MainItem ) getSerialize( C.bundleKey.MAIN_ITEM );

            tx = new Tx( );
            tx.setFrom( planet.getAddress( ) );
            tx.setFrom_planet( planet.getName( ) );
            tx.setSymbol( mainItem.getSymbol( ) );

            if ( Utils.checkClipboard( this, planet.getCoinType( ) ) ) {
                if ( !Utils.equals( Utils.getClipboard( this ), planet.getAddress( ) ) )
                    viewMapper.btnClip.setVisibility( View.VISIBLE );
            }
            viewMapper.toolBar.setTitle( localized( R.string.transfer_toolbar_title ) );

            SyncManager.getInstance( ).recentSyncPlanet( this, planet, mainItem != null ? mainItem.getSymbol( ) : CoinType.of( planet.getCoinType( ) ).name( ) );
        }
    }

    @Override
    public void onSyncComplete( SyncManager.SyncType syncType, boolean complete, boolean isUpdated ) {
        searchPlanets = SearchStore.getInstance( ).getSearchList( planet.getKeyId( ), mainItem != null ? mainItem.getSymbol( ) : CoinType.of( planet.getCoinType( ) ).name( ) );
        if ( searchPlanets.size( ) >= 20 ) {
            SearchStore.getInstance( ).delete( searchPlanets.get( searchPlanets.size( ) - 1 ) );
            searchPlanets = SearchStore.getInstance( ).getSearchList( planet.getKeyId( ), mainItem != null ? mainItem.getSymbol( ) : CoinType.of( planet.getCoinType( ) ).name( ) );
        }
        recentSearchAdapter = new RecentSearchAdapter( this, searchPlanets == null ? new ArrayList<>( ) : searchPlanets );
        recentSearchAdapter.setOnInsideItemClickListener( this );
        viewMapper.listView.setAdapter( recentSearchAdapter );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            Utils.hideKeyboard( this, getCurrentFocus( ) );
            onBackPressed( );
        } else if ( Utils.equals( tag, C.tag.TOOLBAR_TRANSFER_QRCODE ) ) {

            if ( !isQRScan ) {
                isQRScan = true;
                Utils.hideKeyboard( this, getCurrentFocus( ) );
                requestPermissions( C.requestCode.QR_CODE, Manifest.permission.CAMERA );
            }

        }
    }

    @Override
    protected void neverNotAllowed( int code, String permission ) {
        super.neverNotAllowed( code, permission );
        if ( Utils.equals( code, C.requestCode.QR_CODE ) ) {
            CustomToast.makeText( this, localized( R.string.camera_permission_never_not_allowed_title ) ).show( );
        }

    }

    @Override
    protected void permissionsAllowed( int code ) {
        super.permissionsAllowed( code );
        if ( Utils.equals( code, C.requestCode.QR_CODE ) ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.QR_CODE, ScanQRActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
        }
    }

    @Override
    protected void permissionNotAllowed( int code, String permission ) {
        super.permissionNotAllowed( code, permission );
        if ( Utils.equals( code, C.requestCode.QR_CODE ) ) {
            CustomToast.makeText( this, localized( R.string.camera_permission_not_allowed_title ) ).show( );
        }

    }


    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnClear ) {
            viewMapper.etSearch.setText( "" );
        } else if ( v == viewMapper.btnClip ) {
            viewMapper.etSearch.setText( Utils.getClipboard( this ) );
            viewMapper.btnClip.setVisibility( View.GONE );
        } else if ( v == viewMapper.groupSearchAddress ) {
            //주소검색
            tx.setTo( viewMapper.textAddress.getText( ).toString( ) );
            tx.setTo_planet( null );

            setTransition( Transition.SLIDE_SIDE );
            sendAction( TransferAmountActivity.class,
                    Utils.mergeBundles(
                            Utils.createSerializableBundle( C.bundleKey.PLANET, planet ),
                            Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, mainItem ),
                            Utils.createSerializableBundle( C.bundleKey.TX, tx ) ) );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {

        if ( Utils.equals( Objects.requireNonNull( recyclerView.getAdapter( ) ).getClass( ), RecentSearchAdapter.class ) ) {
            tx.setTo( searchPlanets.get( position ).getAddress( ) );
            tx.setTo_planet( searchPlanets.get( position ).getName( ) );
        } else {
            tx.setTo( allPlanets.get( position ).getAddress( ) );
            tx.setTo_planet( allPlanets.get( position ).getName( ) );
        }

        setTransition( Transition.SLIDE_SIDE );
        sendAction( TransferAmountActivity.class,
                Utils.mergeBundles(
                        Utils.createSerializableBundle( C.bundleKey.PLANET, planet ),
                        Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, mainItem ),
                        Utils.createSerializableBundle( C.bundleKey.TX, tx ) ) );

    }

    @Override
    public void beforeTextChanged( CharSequence s, int start, int count, int after ) {

    }

    @Override
    public void onTextChanged( CharSequence s, int start, int before, int count ) {
    }

    @Override
    public void afterTextChanged( Editable s ) {
        updateSearchView( );
        new Get( this ).action( Route.URL( "planet", "search", CoinType.of( planet.getCoinType( ) ).name( ) ), 0, 0, "{\"q\"=\"" + viewMapper.etSearch.getText( ).toString( ) + "\"}" );
    }


    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Planet.class );
        if ( returnVO.isSuccess( ) ) {
            allPlanets = ( ArrayList< Planet > ) returnVO.getResult( );
            if ( allPlanets != null ) {
                if ( allPlanets.size( ) == 0 ) {
                    if ( Utils.equals( CoinType.BTC.getCoinType( ), planet.getCoinType( ) ) ) {
                        if ( BitCoinManager.getInstance( ).validateAddress( viewMapper.etSearch.getText( ).toString( ) ) ) {
                            noSearchView( false );
                            addressSearchView( true );
                            clipView( );
                            return;
                        }

                    } else if ( Utils.equals( CoinType.ETH.getCoinType( ), planet.getCoinType( ) ) ) {
                        if ( EthereumManager.getInstance( ).isValidAddress( viewMapper.etSearch.getText( ).toString( ) ) ) {
                            noSearchView( false );
                            addressSearchView( true );
                            clipView( );
                            return;
                        }
                    }
                    noSearchView( true );
                    addressSearchView( false );
                    clipView( );
                } else {
                    for ( int i = 0; i < allPlanets.size( ); i++ ) {
                        if ( Utils.equals( allPlanets.get( i ).getName( ), planet.getName( ) ) ) {
                            allPlanets.remove( i );
                            break;
                        }
                    }

                    noSearchView( allPlanets.size( ) == 0 );
                    addressSearchView( false );
                    clipView( );
                }
            }

            viewMapper.listView.setAdapter( viewMapper.etSearch.getText( ).length( ) != 0 ? new TransferAdapter( this, allPlanets ) : recentSearchAdapter );
        }
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

    @Override
    protected void onResume( ) {
        super.onResume( );
        isQRScan = false;
        clipView( );
    }

    @Override
    protected void onDestroy( ) {
        getPlanetWalletApplication( ).removeStack( );
        super.onDestroy( );
    }

    private void searchViewThemeSet( ) {
        viewMapper.etSearch.setTextColor( !getCurrentTheme( ) ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#000000" ) );
        viewMapper.etSearch.setHintTextColor( !getCurrentTheme( ) ? Color.parseColor( "#5C5964" ) : Color.parseColor( "#aaaaaa" ) );
        viewMapper.etSearch.setBackgroundColor( !getCurrentTheme( ) ? Color.parseColor( "#111117" ) : Color.parseColor( "#FCFCFC" ) );
    }

    private void updateSearchView( ) {
        viewMapper.imageNotSearch.setVisibility( viewMapper.etSearch.getText( ).length( ) == 0 ? View.VISIBLE : View.INVISIBLE );
        viewMapper.btnClear.setVisibility( viewMapper.etSearch.getText( ).length( ) == 0 ? View.GONE : View.VISIBLE );
        viewMapper.imageSearch.setVisibility( viewMapper.etSearch.getText( ).length( ) >= 1 ? View.VISIBLE : View.INVISIBLE );

    }

    private void noSearchView( boolean b ) {
        viewMapper.textNoItem.setVisibility( b && viewMapper.etSearch.getText( ).length( ) != 0 ? View.VISIBLE : View.GONE );
    }

    private void addressSearchView( boolean b ) {
        viewMapper.groupSearchAddress.setVisibility( b ? View.VISIBLE : View.GONE );
        if ( b ) {
            viewMapper.listView.setAdapter( new TransferAdapter( this, new ArrayList<>( ) ) );
            if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ETH ) {
                viewMapper.imageIcon.setImageResource( R.drawable.icon_eth );
            } else if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.BTC ) {
                viewMapper.imageIcon.setImageResource( R.drawable.icon_btc );
            } else if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ERC20 ) {
                ImageLoader.getInstance( ).displayImage( Route.URL( mainItem.getImg_path( ) ), viewMapper.imageIcon );
            }
            viewMapper.textAddress.setText( viewMapper.etSearch.getText( ).toString( ) );
            Utils.addressForm( viewMapper.textAddress, viewMapper.textAddress.getText( ).toString( ) );
        }


    }

    private void clipView( ) {
        viewMapper.btnClip.setVisibility( Utils.checkClipboard( this, planet.getCoinType( ) ) && viewMapper.etSearch.getText( ).length( ) == 0 && !Utils.equals( Utils.getClipboard( this ), planet.getAddress( ) ) ?
                View.VISIBLE : View.GONE );
    }

    @Override
    public void onInsideItemClick( Planet item, int position ) {
        Planet planet = new Planet( );
        planet.setKeyId( item.getKeyId( ) );
        planet.setAddress( item.getAddress( ) );
        planet.setName( item.getName( ) );
        planet.setSymbol( item.getSymbol( ) );

        searchPlanets.remove( position );
        Objects.requireNonNull( viewMapper.listView.getAdapter( ) ).notifyDataSetChanged( );
        SearchStore.getInstance( ).delete( planet );

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
        StretchImageView btnClip;

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
}