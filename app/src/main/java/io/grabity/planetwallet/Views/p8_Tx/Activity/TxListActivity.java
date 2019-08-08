package io.grabity.planetwallet.Views.p8_Tx.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.BTC;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Views.p6_Transfer.Activity.TransferActivity;
import io.grabity.planetwallet.Views.p8_Tx.Adapter.TxAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceRecyclerView;
import io.grabity.planetwallet.Widgets.BarcodeView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TxListActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, AdvanceRecyclerView.OnItemClickListener {

    //Todo 임시작업

    private ViewMapper viewMapper;
    private Planet planet;
    private ERC20 erc20;
    private ETH eth;
    private BTC btc;

    private ArrayList< Tx > items;

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

            if ( getSerialize( C.bundleKey.ETH ) != null ) {
                eth = ( ETH ) getSerialize( C.bundleKey.ETH );
                setUpViews( eth );
            } else if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                erc20 = ( ERC20 ) getSerialize( C.bundleKey.ERC20 );
                setUpViews( erc20 );
            } else if ( getSerialize( C.bundleKey.BTC ) != null ) {
                btc = ( BTC ) getSerialize( C.bundleKey.BTC );
                setUpViews( btc );
            }

            setUpList( );

        }
    }

    void setUpViews( MainItem item ) {
        Integer type = item.getCoinType( );
        if ( Utils.equals( type, CoinType.ETH.getCoinType( ) ) ) {
            viewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), eth.getBalance( ) ) );
            viewMapper.imageIcon.setImageResource( eth.getIconRes( ) );
        } else if ( Utils.equals( type, CoinType.ERC20.getCoinType( ) ) ) {
            viewMapper.textBalance.setText( String.format( "%s " + erc20.getName( ), erc20.getBalance( ) ) );
            ImageLoader.getInstance( ).displayImage( "http://test.planetwallet.io" + erc20.getImg_path( ), viewMapper.imageIcon );
        } else if ( Utils.equals( type, CoinType.BTC.getCoinType( ) ) ) {
            viewMapper.textBalance.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), btc.getBalance( ) ) );
            viewMapper.imageIcon.setImageResource( btc.getIconRes( ) );

        }

        viewMapper.textName.setText( planet.getName( ) );
        viewMapper.planetView.setData( planet.getAddress( ) );
        viewMapper.textAddress.setText( Utils.addressReduction( planet.getAddress( ) ) );
        viewMapper.barcodeView.setData( planet.getAddress( ) );

        getBalance( );
    }

    void setUpList( ) {
        items = new ArrayList<>( );

        {
            Tx tx = new Tx( );
            tx.setToAddress( "0x896d5615376e551Feace5A808E03356314A44177" );
            tx.setFromAddress( planet.getAddress( ) );
            tx.setAmount( "1.2" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( planet.getAddress( ) );
            tx.setFromAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
            tx.setAmount( "-0.82" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( "0xc08Bd31D490145e6e3028BBF8BBD83fa354BB8e6" );
            tx.setFromAddress( planet.getAddress( ) );
            tx.setAmount( "42.1" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( planet.getAddress( ) );
            tx.setFromAddress( "0x54800670857d2c96D2cC1b4C6c79fc23991732ce" );
            tx.setAmount( "-19.2" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( "0x5DdDd4F7c394a388f755588fe43eD7C24B114718" );
            tx.setFromAddress( planet.getAddress( ) );
            tx.setAmount( "12.2" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( "0x896d5615376e551Feace5A808E03356314A44177" );
            tx.setFromAddress( planet.getAddress( ) );
            tx.setAmount( "1.2" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( planet.getAddress( ) );
            tx.setFromAddress( "0x501c94659d2c00b134a9ba418aa182f14bf72e56" );
            tx.setAmount( "-0.82" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( "0xc08Bd31D490145e6e3028BBF8BBD83fa354BB8e6" );
            tx.setFromAddress( planet.getAddress( ) );
            tx.setAmount( "42.1" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( planet.getAddress( ) );
            tx.setFromAddress( "0x54800670857d2c96D2cC1b4C6c79fc23991732ce" );
            tx.setAmount( "-19.2" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) - 1000 * 60 * 60 * 24 ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        {
            Tx tx = new Tx( );
            tx.setToAddress( "0x5DdDd4F7c394a388f755588fe43eD7C24B114718" );
            tx.setFromAddress( planet.getAddress( ) );
            tx.setAmount( "12.2" );
            tx.setFee( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), "0.0001" ) );
            tx.setTxId( "0xc0208af555b75c4230c406bfc61c632f38095b1f7a5bcf579837f9f9de07a906" );
            tx.setSymbol( getSerialize( C.bundleKey.ERC20 ) == null ? CoinType.of( planet.getCoinType( ) ).name( ) : erc20.getSymbol( ) );
            tx.setDate( Utils.dateFormat( new Date( System.currentTimeMillis( ) ), "MMMM dd, HH:mm" ) );
            items.add( tx );
        }

        viewMapper.listView.setAdapter( new TxAdapter( this, items ) );


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
            }
        } else {
            CustomToast.makeText( this, "네트워크 상태를 체크해주세요." ).show( );
            super.onBackPressed( );
        }
    }

    @Override
    public void onItemClick( AdvanceRecyclerView recyclerView, View view, int position ) {
        sendAction( DetailTxActivity.class, Utils.createSerializableBundle( C.bundleKey.TX, items.get( position ) ) );

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
