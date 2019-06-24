package io.grabity.planetwallet.Views.p6_Transfer.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.ListPopupView.FeePopup;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferConfirmActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, SeekBar.OnSeekBarChangeListener, FeePopup.OnFeePopupSaveClickListener {

    private ViewMapper viewMapper;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_transfer_confimr );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.seekBar.setOnSeekBarChangeListener( this );

        viewMapper.toolBar.setLeftButton( new ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnFeeReset.setOnClickListener( this );
        viewMapper.btnFeeOption.setOnClickListener( this );
        viewMapper.btnSubmit.setOnClickListener( this );


    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnFeeOption ) {
            FeePopup.newInstance( this )
                    .setOnFeePopupSaveClickListener( this )
                    .show( );
        } else if ( v == viewMapper.btnFeeReset ) {
            viewMapper.groupSeekBar.setVisibility( View.VISIBLE );
            viewMapper.groupFeeOption.setVisibility( View.VISIBLE );
            viewMapper.btnFeeReset.setVisibility( View.GONE );
        } else if ( v == viewMapper.btnSubmit ) {
            setTransition( Transition.NO_ANIMATION );
            sendAction( C.requestCode.TRANSFER, PinCodeCertificationActivity.class, Utils.createIntBundle( C.bundleKey.TRANSFER, C.pincertification.TRANSFER ) );
        }

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.TRANSFER && resultCode == RESULT_OK ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( TxReceiptActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TASK, Intent.FLAG_ACTIVITY_NEW_TASK );
        } else if ( requestCode == C.requestCode.TRANSFER && resultCode == RESULT_CANCELED ) {
            Toast.makeText( this, "Your password is different.", Toast.LENGTH_SHORT ).show( );
        }

    }

    @Override
    public void onFeePopupSaveClick( String fee ) {
        super.onBackPressed( );
        viewMapper.textFee.setText( String.format( "%s ETH", fee ) );
        viewMapper.groupSeekBar.setVisibility( View.GONE );
        viewMapper.groupFeeOption.setVisibility( View.GONE );
        viewMapper.btnFeeReset.setVisibility( View.VISIBLE );
    }

    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        }
    }


    @Override
    public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
        PLog.e( "onProgressChanged progress : " + progress );
    }

    @Override
    public void onStartTrackingTouch( SeekBar seekBar ) {

    }

    @Override
    public void onStopTrackingTouch( SeekBar seekBar ) {

    }

    public class ViewMapper {

        ToolBar toolBar;

        ViewGroup groupPlanet;
        ViewGroup groupAddress;
        ViewGroup groupSeekBar;
        ViewGroup groupFeeOption;

        PlanetView planetView;

        SeekBar seekBar;

        CircleImageView imageIconBackground;
        StretchImageView imageIcon;

        TextView textPlanetName;
        TextView textPlanetAddress;
        TextView textNameAddress;
        TextView textAmount;
        TextView textFromName;
        TextView textAmountList;
        TextView textFee;

        View btnFeeReset;
        View btnFeeOption;
        View btnSubmit;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );

            groupPlanet = findViewById( R.id.group_transfer_confirm_planet );
            groupAddress = findViewById( R.id.group_transfer_confirm_address );
            groupSeekBar = findViewById( R.id.group_transfer_confirm_seekbar );
            groupFeeOption = findViewById( R.id.group_transfer_confirm_fee_option );

            planetView = findViewById( R.id.planet_transfer_confirm_planetview );
            seekBar = findViewById( R.id.seekBar );
            imageIconBackground = findViewById( R.id.image_transfer_confirm_address_background );
            imageIcon = findViewById( R.id.image_transfer_confirm_address_icon );

            textPlanetName = findViewById( R.id.text_transfer_confirm_planet_name );
            textPlanetAddress = findViewById( R.id.text_transfer_confirm_planet_address );
            textNameAddress = findViewById( R.id.text_transfer_confirm_address );
            textAmount = findViewById( R.id.text_transfer_confirm_amount );
            textAmountList = findViewById( R.id.text_transfer_confirm_amount_ );
            textFromName = findViewById( R.id.text_transfer_confirm_from_name );
            textFee = findViewById( R.id.text_transfer_confirm_fee );

            btnFeeReset = findViewById( R.id.text_transfer_confirm_fee_reset );
            btnFeeOption = findViewById( R.id.image_transfer_confirm_fee_option );
            btnSubmit = findViewById( R.id.btn_submit );

        }
    }
}
