package io.grabity.planetwallet.Views.p8_Tx.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Views.p6_Transfer.Activity.TransferActivity;
import io.grabity.planetwallet.Views.p8_Tx.Adapter.TxAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TxListActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, AdvanceRecyclerView.OnItemClickListener, AdvanceArrayAdapter.OnAttachViewListener {

    private ViewMapper viewMapper;

    private Planet planet;
    private MainItem mainItem;

    private TxAdapter adapter;
    private ArrayList< Tx > items;
    private ArrayList< Tx > resultItems;

    HeaderViewMapper headerViewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tx_list );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );

        viewMapper.listView.setOnItemClickListener( this );
        viewMapper.listView.setOnAttachViewListener( this );
        viewMapper.listView.addHeaderView( R.layout.header_tx );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.MAIN_ITEM ) == null ) {
            finish( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            mainItem = ( MainItem ) getSerialize( C.bundleKey.MAIN_ITEM );
            viewMapper.toolBar.setTitle( mainItem.getName( ) );

            items = getCacheTxList( );
            viewMapper.listView.setAdapter( adapter = new TxAdapter( this, items == null ? items = new ArrayList<>( ) : items ) );
        }

    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        new Get( this ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "balance", mainItem.getSymbol( ), planet.getAddress( ) ), 0, 0, null );
        new Get( this ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "tx", "list", mainItem.getSymbol( ), planet.getAddress( ) ), 1, 0, null );
    }

    @Override
    public void onAttachView( int resId, int position, View view, boolean screenshotFlag ) {
        if ( !screenshotFlag ) {
            if ( resId == R.layout.header_tx && position == 0 ) {
                headerViewMapper = new HeaderViewMapper( view );
                headerViewMapper.btnTransfer.setOnClickListener( this );
                headerViewMapper.btnReceive.setOnClickListener( this );
                setDataToHeaderView( );
            }
        }
    }


    private void setDataToHeaderView( ) {
        if ( headerViewMapper != null ) {

            headerViewMapper.textBalance.setText( String.format( Locale.US, "%s %s", Utils.balanceReduction( Utils.toMaxUnit( mainItem, mainItem.getBalance( ) ) ), mainItem.getSymbol( ) ) );
            headerViewMapper.textCurrency.setText( String.format( "%s USD", "-" ) );
            if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.BTC ) {
                headerViewMapper.imageIcon.setImageResource( R.drawable.icon_btc );
            } else if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ETH ) {
                headerViewMapper.imageIcon.setImageResource( R.drawable.icon_eth );
            } else {
                ImageLoader.getInstance( ).displayImage( Route.URL( mainItem.getImg_path( ) ), headerViewMapper.imageIcon );
            }
        }
    }

    private ArrayList< Tx > getCacheTxList( ) {

        String prefTx = Utils.emptyToNull( Utils.getPreferenceData( this, Utils.prefKey( CoinType.of( mainItem.getCoinType( ) ).getParent( ), mainItem.getSymbol( ), planet.getKeyId( ) ) ) );
        if ( prefTx != null ) {
            ReturnVO returnVO = Utils.jsonToVO( prefTx, ReturnVO.class, Tx.class );
            if ( returnVO.isSuccess( ) ) {
                try {
                    return ( ArrayList< Tx > ) returnVO.getResult( );
                } catch ( ClassCastException | NullPointerException e ) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == headerViewMapper.btnReceive ) {
            setTransition( Transition.SLIDE_SIDE );
            sendAction( DetailAddressActivity.class,
                    Utils.mergeBundles(
                            Utils.createSerializableBundle( C.bundleKey.PLANET, planet ),
                            Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, mainItem ) ) );

        } else if ( v == headerViewMapper.btnTransfer ) {
            setTransition( Transition.SLIDE_SIDE );
            sendAction( TransferActivity.class,
                    Utils.mergeBundles(
                            Utils.createSerializableBundle( C.bundleKey.PLANET, planet ),
                            Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, mainItem ) ) );

        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( !error ) {

            if ( requestCode == 0 ) {

                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, MainItem.class );
                if ( returnVO.isSuccess( ) ) {
                    MainItem balance = ( MainItem ) returnVO.getResult( );
                    mainItem.setBalance( balance.getBalance( ) );
                    setDataToHeaderView( );
                }

            } else if ( requestCode == 1 ) {

                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Tx.class );
                if ( returnVO.isSuccess( ) ) {

                    Utils.setPreferenceData( this, Utils.prefKey( CoinType.of( mainItem.getCoinType( ) ).getParent( ), mainItem.getSymbol( ), planet.getKeyId( ) ), result );
                    resultItems = ( ArrayList< Tx > ) returnVO.getResult( );

                    if ( resultItems.size( ) == 0 ) return;
                    if ( resultItems.size( ) != items.size( ) || !Utils.equals( items.get( 0 ).getStatus( ), resultItems.get( 0 ).getStatus( ) ) ) {
                        items = getCacheTxList( );
                        adapter.setObjects( items );
                        Objects.requireNonNull( viewMapper.listView.getAdapter( ) ).notifyItemRangeChanged( 1, items.size( ) );
                    }

//                    items = getCacheTxList( );
//                    if ( items != null && items.size( ) > 0 ) adapter.setObjects( items );
//                    Objects.requireNonNull( viewMapper.listView.getAdapter( ) ).notifyItemRangeChanged( 1, items.size( ) );
                }

            }

        } else {

        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {

        setTransition( Transition.SLIDE_UP );
        if ( mainItem != null ) {
            sendAction( DetailTxActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.TX, items.get( position ) ),
                    Utils.createSerializableBundle( C.bundleKey.PLANET, planet ), Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, mainItem ) ) );
        } else {
            sendAction( DetailTxActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.TX, items.get( position ) ),
                    Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) ) );
        }


    }

    public class ViewMapper {

        ToolBar toolBar;
        AdvanceRecyclerView listView;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            listView = findViewById( R.id.listView );
        }
    }

    public class HeaderViewMapper {
        View headerView;

        StretchImageView imageIcon;
        TextView textBalance;
        TextView textCurrency;
        View btnReceive;
        View btnTransfer;

        public HeaderViewMapper( View headerView ) {
            this.headerView = headerView;
            imageIcon = headerView.findViewById( R.id.image_tx_header_icon );
            textBalance = headerView.findViewById( R.id.text_tx_header_balance );
            textCurrency = headerView.findViewById( R.id.text_tx_header_currency );
            btnReceive = headerView.findViewById( R.id.btn_tx_header_receive );
            btnTransfer = headerView.findViewById( R.id.btn_tx_header_transfer );
        }
    }
}
