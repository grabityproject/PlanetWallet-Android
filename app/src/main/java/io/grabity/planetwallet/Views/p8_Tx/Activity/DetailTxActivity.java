package io.grabity.planetwallet.Views.p8_Tx.Activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Date;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Widgets.FontTextView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class DetailTxActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private Tx tx;
    private Planet planet;
    private ERC20 erc20;

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

        if ( getSerialize( C.bundleKey.TX ) == null || getSerialize( C.bundleKey.PLANET ) == null ) {
            finish( );
        } else {
            tx = ( Tx ) getSerialize( C.bundleKey.TX );
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );

            if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                erc20 = ( ERC20 ) getSerialize( C.bundleKey.ERC20 );
            }
            setUpView( );
        }
    }

    void setUpView( ) {
        Utils.addressForm( viewMapper.textAddress, tx.getTo( ) );
        viewMapper.groupPlanet.setVisibility( tx.getTo_planet( ) != null ? View.VISIBLE : View.GONE );
        viewMapper.groupAddress.setVisibility( tx.getTo_planet( ) != null ? View.GONE : View.VISIBLE );

        if ( tx.getTo_planet( ) != null ) {
            viewMapper.planetViewTo.setData( tx.getTo( ) );
            viewMapper.textPlanetName.setText( tx.getTo_planet( ) );
            viewMapper.textTo.setText( tx.getTo_planet( ) );
            viewMapper.textTo.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 16 );
        } else {
            viewMapper.textTo.setText( tx.getTo( ) );
            viewMapper.textTo.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 14 );
            if ( getSerialize( C.bundleKey.ERC20 ) != null )
                ImageLoader.getInstance( ).displayImage( Route.URL( erc20.getImg_path( ) ), viewMapper.imageCoinIcon );
        }

        viewMapper.textTo.setGravity( tx.getTo_planet( ) != null ? Gravity.CENTER | Gravity.END : Gravity.CENTER | Gravity.START );
        viewMapper.textPlanetToAddress.setText( Utils.addressReduction( tx.getTo( ) ) );
        viewMapper.textBalance.setText( String.format( "%s " + tx.getSymbol( ), Utils.balanceReduction( Utils.moveLeftPoint( tx.getAmount( ), 18 ) ) ) );
        viewMapper.textStatus.setText( Utils.equals( tx.getStatus( ), C.transferStatus.PENDING ) ? "Pending" : "Completed" );
        viewMapper.textType.setText( Utils.equals( planet.getAddress( ), tx.getFrom( ) ) ? "Send" : "Received" );
        viewMapper.textFrom.setText( tx.getFrom_planet( ) );
        viewMapper.textAmount.setText( String.format( "%s " + tx.getSymbol( ), Utils.balanceReduction( Utils.moveLeftPoint( tx.getAmount( ), 18 ) ) ) );
        viewMapper.textFee.setText( Utils.feeCalculation( Utils.moveLeftPoint( tx.getGasPrice( ), 18 ), tx.getGasLimit( ) ) );
        viewMapper.textTxID.setText( tx.getTx_id( ) );
        viewMapper.textTxID.underLine( );
        if ( Utils.equals( tx.getStatus( ), C.transferStatus.CONFIRMED ) )
            viewMapper.textDate.setText( Utils.dateFormat( new Date( Long.valueOf( tx.getCreated_at( ) ) ), "yyyy. MM. dd HH:mm:ss" ) );
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
            PLog.e( "uri : " + "https://ropsten.etherscan.io/tx/" + tx.getTx_id( ) );
            sendAction( "https://ropsten.etherscan.io/tx/" + tx.getTx_id( ) );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;
        StretchImageView imageCoinIcon;
        PlanetView planetViewTo;

        ViewGroup groupPlanet;
        ViewGroup groupAddress;

        View btnSubmit;

        TextView textPlanetToAddress;
        TextView textPlanetName;
        TextView textAddress;
        TextView textBalance;
        TextView textStatus;
        TextView textType;
        TextView textFrom;
        TextView textTo;
        TextView textAmount;
        TextView textFee;
        TextView textDate;
        FontTextView textTxID;


        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            imageCoinIcon = findViewById( R.id.image_detail_tx_coin_icon );
            planetViewTo = findViewById( R.id.planet_detail_tx_planetview );
            groupPlanet = findViewById( R.id.group_detail_tx_planet );
            groupAddress = findViewById( R.id.group_detail_tx_address );

            textPlanetToAddress = findViewById( R.id.text_detail_tx_planet_address );
            textPlanetName = findViewById( R.id.text_detail_tx_planet_name );
            textAddress = findViewById( R.id.text_detail_tx_address );
            textBalance = findViewById( R.id.text_detail_tx_amount );
            textStatus = findViewById( R.id.text_detail_tx_status );
            textType = findViewById( R.id.text_detail_tx_type );
            textFrom = findViewById( R.id.text_detail_tx_from_name );
            textTo = findViewById( R.id.text_detail_tx_to );
            textAmount = findViewById( R.id.text_detail_tx_amount_ );
            textFee = findViewById( R.id.text_detail_tx_fee );
            textDate = findViewById( R.id.text_detail_tx_date );
            textTxID = findViewById( R.id.text_detail_tx_txhash );

            btnSubmit = findViewById( R.id.btn_submit );
        }
    }
}
