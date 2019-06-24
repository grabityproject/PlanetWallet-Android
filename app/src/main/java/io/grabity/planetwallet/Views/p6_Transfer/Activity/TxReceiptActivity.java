package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TxReceiptActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    //Todo
    private ViewMapper viewMapper;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_tx_receipt );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem(  ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnTxHash.setOnClickListener( this );
        viewMapper.btnShare.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );

        viewMapper.imageIconBackground.setBorderColor( !getCurrentTheme() ? Color.parseColor( "#FFFFFF") : Color.parseColor( "#000000" ) );

    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnTxHash ){

        } else if ( v == viewMapper.btnShare ){

        } else if ( v == viewMapper.btnSubmit ){

        }
    }

    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {

    }


    public class ViewMapper {

        ToolBar toolBar;

        ViewGroup groupPlabnet;
        ViewGroup groupAddress;

        View btnShare;
        View btnSubmit;
        View btnTxHash;

        PlanetView planetView;

        TextView textPlanetName;
        TextView textPlanetAddress;
        TextView textAmount;
        TextView textFromName;
        TextView textAmountList;
        TextView textFee;
        TextView textDate;


        CircleImageView imageIconBackground;
        StretchImageView imageIcon;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );

            groupPlabnet = findViewById( R.id.group_tx_receipt_planet );
            groupAddress = findViewById( R.id.group_tx_receipt_address );

            btnShare = findViewById( R.id.group_tx_receipt_share );
            btnSubmit = findViewById( R.id.btn_submit );
            btnTxHash = findViewById( R.id.text_tx_receipt_txhash );

            planetView = findViewById( R.id.planet_tx_receipt_planetview );
            imageIconBackground = findViewById( R.id.image_tx_receipt_address_background );
            imageIcon = findViewById( R.id.image_tx_receipt_address_icon );

            textPlanetName = findViewById( R.id.text_tx_receipt_planet_name );
            textPlanetAddress = findViewById( R.id.text_tx_receipt_planet_address );
            textAmount = findViewById( R.id.text_tx_receipt_amount );
            textAmountList = findViewById( R.id.text_tx_receipt_amount_ );
            textFromName = findViewById( R.id.text_tx_receipt_from_name );
            textFee = findViewById( R.id.text_tx_receipt_fee );
            textDate = findViewById( R.id.text_tx_receipt_date );
        }
    }
}
