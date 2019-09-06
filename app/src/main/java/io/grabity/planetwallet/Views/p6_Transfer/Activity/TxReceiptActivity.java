package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Locale;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.SearchStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TxReceiptActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;

    private Planet planet;
    private MainItem mainItem;
    private Tx tx;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tx_receipt );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
        getPlanetWalletApplication( ).removeAllStack( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnTxHash.setOnClickListener( this );
        viewMapper.btnShare.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );

        viewMapper.btnShare.setBorder_color_normal( !getCurrentTheme( ) ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#000000" ) );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.MAIN_ITEM ) == null || getSerialize( C.bundleKey.TX ) == null ) {
            finish( );
        } else {

            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            mainItem = ( MainItem ) getSerialize( C.bundleKey.MAIN_ITEM );
            tx = ( Tx ) getSerialize( C.bundleKey.TX );

            // Save Search History
            saveRecentSearch( );

            viewMapper.groupPlanet.setVisibility( tx.getTo_planet( ) != null ? View.VISIBLE : View.GONE );
            viewMapper.groupAddress.setVisibility( tx.getTo_planet( ) != null ? View.GONE : View.VISIBLE );


            viewMapper.planetView.setData( tx.getTo( ) );
            viewMapper.textPlanetName.setText( tx.getTo_planet( ) );
            viewMapper.textPlanetAddress.setText( Utils.addressReduction( tx.getTo( ) ) );
            viewMapper.textAddress.setText( tx.getTo( ) );
            Utils.addressForm( viewMapper.textAddress, viewMapper.textAddress.getText( ).toString( ) );


            viewMapper.textAmount.setText( String.format( Locale.US, "%s %s", Utils.removeLastZero( Utils.toMaxUnit( mainItem, tx.getAmount( ) ) ), mainItem.getSymbol( ) ) );
            viewMapper.textAmountList.setText( String.format( Locale.US, "%s %s", Utils.removeLastZero( Utils.toMaxUnit( mainItem, tx.getAmount( ) ) ), mainItem.getSymbol( ) ) );

            viewMapper.textFromName.setText( planet.getName( ) );
            viewMapper.textFee.setText( String.format( Locale.US, "%s %s", Utils.removeLastZero( Utils.toMaxUnit( CoinType.of( planet.getCoinType( ) ), tx.getFee( ) ) ), CoinType.of( planet.getCoinType( ) ).name( ) ) );
            viewMapper.btnTxHash.setText( tx.getTx_id( ) );
            viewMapper.btnTxHash.underLine( );

            // CoinType
            if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.BTC ) {
                viewMapper.imageIcon.setImageResource( R.drawable.icon_btc );
            } else if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ETH ) {
                viewMapper.imageIcon.setImageResource( R.drawable.icon_eth );
            } else if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ERC20 ) {
                ImageLoader.getInstance( ).displayImage( Route.URL( mainItem.getImg_path( ) ), viewMapper.imageIcon );
            }

        }

    }


    private void saveRecentSearch( ) {

        if ( tx.getTo_planet( ) == null ) return;
        Planet searchPlanet = new Planet( );
        searchPlanet.setKeyId( planet.getKeyId( ) );
        searchPlanet.setAddress( tx.getTo( ) );
        searchPlanet.setName( tx.getTo_planet( ) );
        searchPlanet.setSymbol( tx.getSymbol( ) );
        searchPlanet.setCoinType( planet.getCoinType( ) );
        searchPlanet.setDate( String.valueOf( System.currentTimeMillis( ) ) );

        SearchStore.getInstance( ).save( searchPlanet );

    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnTxHash ) {
            if ( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ) {
                sendActionUri( "https://ropsten.etherscan.io/tx/" + tx.getTx_id( ) );
            } else {
                sendActionUri( "https://live.blockcypher.com/btc-testnet/tx/" + tx.getTx_id( ) );
            }

        } else if ( v == viewMapper.btnShare ) {
            Intent intent = new Intent( Intent.ACTION_SEND )
                    .addCategory( Intent.CATEGORY_DEFAULT )
                    .setType( "text/plain" )
                    .putExtra( Intent.EXTRA_SUBJECT, "서브젝트" )
                    .putExtra( Intent.EXTRA_TEXT, "텍스트 전송" );
            startActivity( Intent.createChooser( intent, "공유기능" ) );

        } else if ( v == viewMapper.btnSubmit ) {
            super.onBackPressed( );
        }
    }


    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            onBackPressed( );
        }
    }


    public class ViewMapper {

        ToolBar toolBar;

        ViewGroup groupPlanet;
        ViewGroup groupAddress;

        RoundRelativeLayout btnShare;
        View btnSubmit;

        PlanetView planetView;

        TextView textPlanetName;
        TextView textPlanetAddress;
        TextView textAddress;
        TextView textAmount;
        TextView textFromName;
        TextView textAmountList;
        TextView textFee;
        TextView textDate;
        FontTextView btnTxHash;


        CircleImageView imageIcon;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );

            groupPlanet = findViewById( R.id.group_tx_receipt_planet );
            groupAddress = findViewById( R.id.group_tx_receipt_address );

            btnShare = findViewById( R.id.group_tx_receipt_share );
            btnSubmit = findViewById( R.id.btn_submit );
            btnTxHash = findViewById( R.id.text_tx_receipt_txhash );

            planetView = findViewById( R.id.planet_tx_receipt_planetview );
            imageIcon = findViewById( R.id.image_tx_receipt_coin_icon );

            textPlanetName = findViewById( R.id.text_tx_receipt_planet_name );
            textPlanetAddress = findViewById( R.id.text_tx_receipt_planet_address );
            textAddress = findViewById( R.id.text_tx_receipt_address );

            textAmount = findViewById( R.id.text_tx_receipt_amount );
            textAmountList = findViewById( R.id.text_tx_receipt_amount_ );
            textFromName = findViewById( R.id.text_tx_receipt_from_name );
            textFee = findViewById( R.id.text_tx_receipt_fee );
            textDate = findViewById( R.id.text_tx_receipt_date );
        }
    }
}
