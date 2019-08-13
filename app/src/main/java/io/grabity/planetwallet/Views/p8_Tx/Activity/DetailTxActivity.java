package io.grabity.planetwallet.Views.p8_Tx.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class DetailTxActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    //todo 임시
    private ViewMapper viewMapper;
    private Tx tx;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_tx );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );
    }

    @Override
    protected void setData( ) {
        super.setData( );

        if ( getSerialize( C.bundleKey.TX ) == null ) {
            finish( );
        } else {
            tx = ( Tx ) getSerialize( C.bundleKey.TX );
            setUpView( );
        }
    }

    void setUpView( ) {
        viewMapper.planetViewTo.setData( tx.getTo( ) );
        viewMapper.planetViewFrom.setData( tx.getFrom( ) );
        viewMapper.textToAddress.setText( tx.getTo( ) );
        viewMapper.textFromAddress.setText( tx.getFrom( ) );
//        viewMapper.textFee.setText( tx.getFee( ) );
//        viewMapper.textAmount.setText( String.format( "%s " + tx.getSymbol( ), tx.getAmount( ) ) );
//        viewMapper.textDate.setText( tx.getDate( ) );
//        viewMapper.textTxID.setText( tx.getTxId( ) );
        viewMapper.textTxID.underLine( );

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
        if ( v == viewMapper.btnSubmit ) {
//            PLog.e( "uri : " + "https://ropsten.etherscan.io/tx/" + tx.getTxId( ) );
//            sendAction( "https://ropsten.etherscan.io/tx/" + tx.getTxId( ) );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;
        PlanetView planetViewTo;
        PlanetView planetViewFrom;

        TextView textToAddress;
        TextView textFromAddress;
        TextView textFee;
        TextView textAmount;
        FontTextView textTxID;
        TextView textDate;

        View btnSubmit;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            planetViewTo = findViewById( R.id.planet_detail_tx_to_planetView );
            planetViewFrom = findViewById( R.id.planet_detail_tx_from_planetView );

            textToAddress = findViewById( R.id.text_detail_tx_to_address );
            textFromAddress = findViewById( R.id.text_detail_tx_from_address );
            textFee = findViewById( R.id.text_detail_tx_fee );
            textAmount = findViewById( R.id.text_detail_tx_amount );
            textDate = findViewById( R.id.text_detail_tx_date );
            textTxID = findViewById( R.id.text_detail_tx_txid );

            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
