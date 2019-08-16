package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.SearchStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.Search;
import io.grabity.planetwallet.VO.Transfer;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.RoundRelativeLayout;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TxReceiptActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    //Todo 거래완료후 그 값을 이용해 setting

    private ViewMapper viewMapper;
    private Planet planet;
    private ERC20 erc20;
    private Transfer transfer;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tx_receipt );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
        transferStackCleanUp( );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnTxHash.setOnClickListener( this );
        viewMapper.btnShare.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );

        viewMapper.imageIconBackground.setBorderColor( !getCurrentTheme( ) ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#000000" ) );
        viewMapper.btnShare.setBorder_color_normal( !getCurrentTheme( ) ? Color.parseColor( "#FFFFFF" ) : Color.parseColor( "#000000" ) );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.TRANSFER ) == null ) {
            finish( );
        } else {

            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            transfer = ( Transfer ) getSerialize( C.bundleKey.TRANSFER );

            if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) && getSerialize( C.bundleKey.ERC20 ) != null ) {
                erc20 = ( ERC20 ) getSerialize( C.bundleKey.ERC20 );
                amountViewSetting( erc20.getName( ) );
            } else {
                amountViewSetting( CoinType.of( planet.getCoinType( ) ).name( ) );
            }


            viewSetting( );
            searchSave( );
        }
    }


    private void transferStackCleanUp( ) {
        if ( C.transferClass.transferActivity != null && C.transferClass.transferAmountActivity != null && C.transferClass.transferConfirmActivity != null ) {
            C.transferClass.transferActivity.onBackPressed( );
            C.transferClass.transferAmountActivity.onBackPressed( );
            C.transferClass.transferConfirmActivity.onBackPressed( );
        }
    }

    private void viewSetting( ) {
        viewMapper.groupPlanet.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ? View.VISIBLE : View.GONE );
        viewMapper.groupAddress.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ? View.VISIBLE : View.GONE );
        viewMapper.textFromName.setText( planet.getName( ) );
        viewMapper.textFee.setText( transfer.getFee( ) );
        //Todo node
        viewMapper.textDate.setText( "2019. 06. 13 13:05:10" );
        viewMapper.btnTxHash.setText( transfer.getTxHash( ) );
        viewMapper.btnTxHash.underLine( );

        if ( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ) {
            viewMapper.planetView.setData( transfer.getToAddress( ) );
            viewMapper.textPlanetName.setText( transfer.getToName( ) );
            viewMapper.textPlanetAddress.setText( Utils.addressReduction( transfer.getToAddress( ) ) );
        } else if ( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ) {
            if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) {
                viewMapper.imageIcon.setImageResource( !getCurrentTheme( ) ? R.drawable.icon_transfer_bit_black : R.drawable.icon_transfer_bit_white );
            } else if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {
                viewMapper.imageIcon.setImageResource( !getCurrentTheme( ) ? R.drawable.icon_transfer_eth_black : R.drawable.icon_transfer_eth_white );
            }
            viewMapper.imageIconBackground.setBorderColor( Color.parseColor( !getCurrentTheme( ) ? "#1E1E28" : "#EDEDED" ) );
            viewMapper.textAddress.setText( transfer.getToAddress( ) );
            Utils.addressForm( viewMapper.textAddress, viewMapper.textAddress.getText( ).toString( ) );
        }
    }


    private void searchSave( ) {

        if ( !Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ) return;
        Search search = new Search( );
        search.setKeyId( planet.getKeyId( ) );
        search.setAddress( transfer.getToAddress( ) );
        search.setSymbol( getSerialize( C.bundleKey.ERC20 ) != null ? erc20.getSymbol( ) : CoinType.of( planet.getCoinType( ) ).name( ) );
        search.setName( transfer.getToName( ) );
        SearchStore.getInstance( ).save( search );

    }

    void amountViewSetting( String type ) {
        viewMapper.textAmount.setText( String.format( "%s " + type, transfer.getToBalance( ) ) );
        viewMapper.textAmountList.setText( String.format( "%s " + type, transfer.getToBalance( ) ) );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnTxHash ) {
            //Todo 이더스캔 및 거래결과 확인

        } else if ( v == viewMapper.btnShare ) {
            //Todo 공유기능

        } else if ( v == viewMapper.btnSubmit ) {
            super.onBackPressed( );
        }
    }

    @Override
    public void onBackPressed( ) {
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


        CircleImageView imageIconBackground;
        StretchImageView imageIcon;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );

            groupPlanet = findViewById( R.id.group_tx_receipt_planet );
            groupAddress = findViewById( R.id.group_tx_receipt_address );

            btnShare = findViewById( R.id.group_tx_receipt_share );
            btnSubmit = findViewById( R.id.btn_submit );
            btnTxHash = findViewById( R.id.text_tx_receipt_txhash );

            planetView = findViewById( R.id.planet_tx_receipt_planetview );
            imageIconBackground = findViewById( R.id.image_tx_receipt_address_background );
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
