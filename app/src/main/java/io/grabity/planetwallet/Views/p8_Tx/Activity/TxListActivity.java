package io.grabity.planetwallet.Views.p8_Tx.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Views.p8_Tx.Adapter.TxAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TxListActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, AdvanceRecyclerView.OnItemClickListener, AdvanceArrayAdapter.OnAttachViewListener {

    private ViewMapper viewMapper;
    private Planet planet;
    private ERC20 erc20;

    ArrayList< Tx > txlist;

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

            if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                erc20 = ( ERC20 ) getSerialize( C.bundleKey.ERC20 );
            }
            setUpViews( );
        }
    }


    void setUpViews( ) {

        viewMapper.toolBar.setTitle( getSerialize( C.bundleKey.ERC20 ) != null ? erc20.getName( ) : CoinType.of( planet.getCoinType( ) ).getCoinName( ) );

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
                if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                    headerViewMapper.textBalance.setText( String.format( "%s " + erc20.getSymbol( ), Utils.balanceReduction( Utils.moveLeftPoint( erc20.getBalance( ), 18 ) ) ) );
                    headerViewMapper.textCurrency.setText( String.format( "%s USD", Utils.balanceReduction( Utils.moveLeftPoint( erc20.getBalance( ), 18 ) ) ) );
                    ImageLoader.getInstance( ).displayImage( Route.URL( erc20.getImg_path( ) ), headerViewMapper.imageIcon );
                } else {
                    headerViewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), Utils.balanceReduction( Utils.moveLeftPoint( planet.getBalance( ), 18 ) ) ) );
                    headerViewMapper.textCurrency.setText( String.format( "%s USD", Utils.balanceReduction( Utils.moveLeftPoint( planet.getBalance( ), 18 ) ) ) );
                    headerViewMapper.imageIcon.setImageResource( Utils.equals( CoinType.ETH.getCoinType( ), planet.getCoinType( ) ) ? R.drawable.icon_eth : R.drawable.icon_btc );
                }

            }
        }
    }

    void getTxList( ) {

        new Get( this ).setDeviceKey( C.DEVICE_KEY ).
                action( Route.URL( "tx", "list", getSerialize( C.bundleKey.ERC20 ) != null ? erc20.getSymbol( ) : CoinType.of( planet.getCoinType( ) ).name( ), planet.getName( ) ), 1, 0, null );
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
            CustomToast.makeText( this, "Receive" ).show( );
        } else if ( v == headerViewMapper.btnTransfer ) {
            CustomToast.makeText( this, "Transfer" ).show( );
        }
    }


    private void getBalance( ) {
        if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {
            if ( getSerialize( C.bundleKey.ERC20 ) != null ) { //ERC20
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
                    } else if ( resultCode == Integer.valueOf( erc20.get_id( ) ) ) {
                        ERC20 e = ( ERC20 ) returnVO.getResult( );
                        erc20.setBalance( e.getBalance( ) );
                    }
                }
            } else if ( requestCode == 1 ) {
                PLog.e( "tx check : " + result );
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Tx.class );
                if ( statusCode == 200 ) {
                    if ( returnVO.isSuccess( ) ) {
                        txlist = ( ArrayList< Tx > ) returnVO.getResult( );
                        PLog.e( "txlist size : " + txlist.size( ) );


                        viewMapper.listView.setAdapter( new TxAdapter( this, txlist, planet.getAddress( ) ) );
                    }
                }

            }
        } else {

        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {
        sendAction( DetailTxActivity.class, Utils.createSerializableBundle( C.bundleKey.TX, txlist.get( position ) ) );

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