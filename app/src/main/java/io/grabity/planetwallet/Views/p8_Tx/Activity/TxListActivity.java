package io.grabity.planetwallet.Views.p8_Tx.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Objects;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
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
    private ERC20 erc20;
    private TxAdapter adapter;

    ArrayList< Tx > txItems;

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


        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            finish( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );

            if ( getSerialize( C.bundleKey.MAIN_ITEM ) != null ) {
                if ( Utils.equals( getSerialize( C.bundleKey.MAIN_ITEM ).getClass( ), ERC20.class ) ) {
                    erc20 = ( ERC20 ) getSerialize( C.bundleKey.MAIN_ITEM );
                }
            }

            viewMapper.toolBar.setTitle( erc20 != null ? erc20.getName( ) : CoinType.of( planet.getCoinType( ) ).getCoinName( ) );
            viewMapper.listView.setAdapter( adapter = new TxAdapter( this, new ArrayList<>( ), erc20 != null ? erc20.getDecimals( ) : CoinType.of( planet.getCoinType( ) ).getPrecision( ).toString( ) ) );
        }
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        getBalance( );
        getTxList( );
    }

    @Override
    public void onAttachView( int resId, int position, View view, boolean screenshotFlag ) {
        if ( !screenshotFlag ) {
            if ( resId == R.layout.header_tx && position == 0 ) {
                headerViewMapper = new HeaderViewMapper( view );
                headerViewMapper.btnTransfer.setOnClickListener( this );
                headerViewMapper.btnReceive.setOnClickListener( this );

                if ( erc20 != null ) {
                    headerViewMapper.textBalance.setText( String.format( "%s " + erc20.getSymbol( ), Utils.balanceReduction( Utils.toMaxUnit( erc20, erc20.getBalance( ) ) ) ) );
                    headerViewMapper.textCurrency.setText( String.format( "%s USD", erc20.getBalance( ) ) );
                    ImageLoader.getInstance( ).displayImage( Route.URL( erc20.getImg_path( ) ), headerViewMapper.imageIcon );

                } else {
                    headerViewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), Utils.balanceReduction( Utils.toMaxUnit( CoinType.of( planet.getCoinType( ) ), planet.getBalance( ) ) ) ) );
                    headerViewMapper.textCurrency.setText( String.format( "%s USD", planet.getBalance( ) ) );
                    headerViewMapper.imageIcon.setImageResource( Utils.equals( CoinType.ETH.getCoinType( ), planet.getCoinType( ) ) ? R.drawable.icon_eth : R.drawable.icon_btc );

                }

            }
        }
    }

    void getTxList( ) {

        new Get( this ).setDeviceKey( C.DEVICE_KEY ).
                action( Route.URL( "tx", "list", erc20 != null ? erc20.getSymbol( ) : CoinType.of( planet.getCoinType( ) ).name( ), planet.getName( ) ), 1, 0, null );
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
            if ( erc20 != null ) {
                sendAction( DetailAddressActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.PLANET, planet ), Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, erc20 ) ) );
            } else {
                sendAction( DetailAddressActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
            }

        } else if ( v == headerViewMapper.btnTransfer ) {
            if ( erc20 != null ) {
                sendAction( TransferActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.PLANET, planet ), Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, erc20 ) ) );
            } else {
                sendAction( TransferActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
            }
//
        }
    }


    private void getBalance( ) {
        if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {
            if ( erc20 != null ) { //ERC20
                new Get( this ).setDeviceKey( C.DEVICE_KEY )
                        .action( Route.URL( "balance", erc20.getSymbol( ), planet.getName( ) ), 0, Integer.valueOf( erc20.get_id( ) ), null );
            } else { // ETH
                new Get( this ).setDeviceKey( C.DEVICE_KEY )
                        .action( Route.URL( "balance", CoinType.ETH.name( ), planet.getName( ) ), 0, 0, null );
            }
        } else if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) { //BTC

        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( !error ) {
            if ( requestCode == 0 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, resultCode == 0 ? Planet.class : ERC20.class );
                if ( returnVO.isSuccess( ) ) {
                    if ( resultCode == 0 ) {
                        Planet p = ( Planet ) returnVO.getResult( );
                        planet.setBalance( p.getBalance( ) );

                        headerViewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), Utils.balanceReduction( Utils.toMaxUnit( CoinType.of( planet.getCoinType( ) ), planet.getBalance( ) ) ) ) );
                        headerViewMapper.textCurrency.setText( String.format( "%s USD", planet.getBalance( ) ) );

                    } else if ( resultCode == Integer.valueOf( erc20.get_id( ) ) ) {
                        ERC20 e = ( ERC20 ) returnVO.getResult( );
                        erc20.setBalance( e.getBalance( ) );

                        headerViewMapper.textBalance.setText( String.format( "%s " + erc20.getSymbol( ), Utils.balanceReduction( Utils.toMaxUnit( erc20, erc20.getBalance( ) ) ) ) );
                        headerViewMapper.textCurrency.setText( String.format( "%s USD", erc20.getBalance( ) ) );
                    }
                }
            } else if ( requestCode == 1 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Tx.class );
                if ( statusCode == 200 ) {
                    if ( returnVO.isSuccess( ) ) {
                        txItems = ( ArrayList< Tx > ) returnVO.getResult( );
                        adapter.setObjects( txItems );
                        Objects.requireNonNull( viewMapper.listView.getAdapter( ) ).notifyItemRangeChanged( 1, txItems.size( ) );

                    }
                }

            }
        } else {

        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {

        if ( erc20 != null ) {
            sendAction( DetailTxActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.TX, txItems.get( position ) ),
                    Utils.createSerializableBundle( C.bundleKey.PLANET, planet ), Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, erc20 ) ) );
        } else {
            sendAction( DetailTxActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.TX, txItems.get( position ) ),
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
