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
import io.grabity.planetwallet.Views.p6_Transfer.Activity.TransferActivity;
import io.grabity.planetwallet.Views.p8_Tx.Adapter.TxAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.BarcodeView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TxListActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, AdvanceRecyclerView.OnItemClickListener {

    //Todo 임시작업

    private ViewMapper viewMapper;
    private Planet planet;
    private ERC20 erc20;

    ArrayList< Tx > txlist;

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

        viewMapper.btnQRCode.setOnClickListener( this );
        viewMapper.btnTransfer.setOnClickListener( this );

        viewMapper.listView.setOnItemClickListener( this );


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

        if ( getSerialize( C.bundleKey.ERC20 ) != null ) {

            viewMapper.textBalance.setText( String.format( "%s " + erc20.getName( ), erc20.getBalance( ) ) );
            ImageLoader.getInstance( ).displayImage( "http://test.planetwallet.io" + erc20.getImg_path( ), viewMapper.imageIcon );
        } else {
            viewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), planet.getBalance( ) ) );
            viewMapper.imageIcon.setImageResource( planet.getIconRes( ) );
        }

        viewMapper.textName.setText( planet.getName( ) );
        viewMapper.planetView.setData( planet.getAddress( ) );
        viewMapper.textAddress.setText( Utils.addressReduction( planet.getAddress( ) ) );
        viewMapper.barcodeView.setData( planet.getAddress( ) );

        getBalance( );
        getTxList( );
    }

    void getTxList( ) {

        new Get( this ).setDeviceKey( C.DEVICE_KEY ).
                action( Route.URL( "tx", "list", getSerialize( C.bundleKey.ERC20 ) != null ? erc20.getSymbol( ) : CoinType.of( planet.getCoinType( ) ).name( ), planet.getName( ) ), 1, 0, null );
//        viewMapper.listView.setAdapter( new TxAdapter( this, items ) );

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
        if ( v == viewMapper.btnQRCode ) {
            viewMapper.barcodeView.setVisibility( viewMapper.barcodeView.getVisibility( ) == View.GONE ? View.VISIBLE : View.GONE );
        } else if ( v == viewMapper.btnTransfer ) {
            if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                sendAction( TransferActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.PLANET, planet ), Utils.createSerializableBundle( C.bundleKey.ERC20, erc20 ) ) );
            } else {
                sendAction( TransferActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
            }
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
                        viewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), planet.getBalance( ) ) );
                    } else if ( resultCode == Integer.valueOf( erc20.get_id( ) ) ) {
                        ERC20 e = ( ERC20 ) returnVO.getResult( );
                        erc20.setBalance( e.getBalance( ) );
                        viewMapper.textBalance.setText( String.format( "%s " + erc20.getName( ), erc20.getBalance( ) ) );
                    }
                }
            } else if ( requestCode == 1 ) {
                PLog.e( "tx check : " + result );
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Tx.class );
                if ( statusCode == 200 ) {
                    if ( returnVO.isSuccess( ) ) {
                        txlist = ( ArrayList< Tx > ) returnVO.getResult( );
                        PLog.e( "txlist size : " + txlist.size( ) );


                        viewMapper.listView.setAdapter( new TxAdapter( this, txlist, planet.getName( ) ) );
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
        PlanetView planetView;
        BarcodeView barcodeView;

        TextView textBalance;
        TextView textAddress;
        TextView textName;
        StretchImageView imageIcon;

        View btnQRCode;
        View btnTransfer;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            listView = findViewById( R.id.listView );
            planetView = findViewById( R.id.planet_tx_list_planetview );
            barcodeView = findViewById( R.id.barcode_tx_list_barcodeView );


            textBalance = findViewById( R.id.text_tx_list_balance );
            textAddress = findViewById( R.id.text_tx_list_address );
            textName = findViewById( R.id.text_tx_list_name );

            imageIcon = findViewById( R.id.image_tx_list_icon );

            btnQRCode = findViewById( R.id.btn_tx_list_qr_code );
            btnTransfer = findViewById( R.id.btn_tx_list_transfer );
        }
    }
}
