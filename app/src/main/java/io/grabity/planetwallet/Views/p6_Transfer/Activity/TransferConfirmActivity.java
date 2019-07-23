package io.grabity.planetwallet.Views.p6_Transfer.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.ETHGasPrice;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Transfer;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p6_Transfer.Popups.FeePopup;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferConfirmActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, SeekBar.OnSeekBarChangeListener, FeePopup.OnFeePopupSaveClickListener {


    private ViewMapper viewMapper;
    private Planet planet;
    private ERC20 erc20;
    private Transfer transfer;
    private ArrayList< String > fee;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_transfer_confimr );
        viewMapper = new ViewMapper( );
        C.transferClass.transferConfirmActivity = this;
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

        fee = new ArrayList<>( );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.TRANSFER ) == null ) {
            finish( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            transfer = ( Transfer ) getSerialize( C.bundleKey.TRANSFER );

            new Get( this ).action( Route.URL( "gas" ), 0, 0, null );

            if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) && getSerialize( C.bundleKey.ERC20 ) != null ) {
                erc20 = ( ERC20 ) getSerialize( C.bundleKey.ERC20 );
                viewMapper.toolBar.setTitle( localized( R.string.transfer_confirm_toolbar_title, erc20.getName( ) ) );
                amountViewSetting( erc20.getName( ) );
            } else {
                viewMapper.toolBar.setTitle( localized( R.string.transfer_confirm_toolbar_title, CoinType.of( planet.getCoinType( ) ).name( ) ) );
                amountViewSetting( CoinType.of( planet.getCoinType( ) ).name( ) );
            }


            viewSetting( );


        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( !error ) {
            if ( statusCode == 200 && requestCode == 0 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, ETHGasPrice.class );
                if ( returnVO.isSuccess( ) ) {
                    ETHGasPrice ethGasPrice = ( ETHGasPrice ) returnVO.getResult( );

                    fee.add( new BigDecimal( ethGasPrice.getSafeLow( ) ).movePointLeft( 9 ).multiply( ETHGasPrice.DEFALUT_GAS_LIMIT ).stripTrailingZeros( ).toString( ) );
                    fee.add( new BigDecimal( ethGasPrice.getStandard( ) ).movePointLeft( 9 ).multiply( ETHGasPrice.DEFALUT_GAS_LIMIT ).stripTrailingZeros( ).toString( ) );
                    fee.add( new BigDecimal( ethGasPrice.getFast( ) ).movePointLeft( 9 ).multiply( ETHGasPrice.DEFALUT_GAS_LIMIT ).stripTrailingZeros( ).toString( ) );
                    fee.add( new BigDecimal( ethGasPrice.getFastest( ) ).movePointLeft( 9 ).multiply( ETHGasPrice.DEFALUT_GAS_LIMIT ).stripTrailingZeros( ).toString( ) );

                }
            } else {
                for ( int i = 0; i < 4; i++ ) {
                    fee.add( new BigDecimal( ETHGasPrice.DEFALUT_GAS_GWEI ).movePointLeft( 9 ).multiply( ETHGasPrice.DEFALUT_GAS_LIMIT ).stripTrailingZeros( ).toEngineeringString( ) );
                }
            }
            feeSetting( );
        } else {
            for ( int i = 0; i < 4; i++ ) {
                fee.add( new BigDecimal( ETHGasPrice.DEFALUT_GAS_GWEI ).movePointLeft( 9 ).multiply( ETHGasPrice.DEFALUT_GAS_LIMIT ).stripTrailingZeros( ).toEngineeringString( ) );
            }
            feeSetting( );
        }

    }

    void feeSetting( ) {
        viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( 2 ) ) );
    }

    void viewSetting( ) {
        viewMapper.groupPlanet.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ? View.VISIBLE : View.GONE );
        viewMapper.groupAddress.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ? View.VISIBLE : View.GONE );
        viewMapper.textFromName.setText( planet.getName( ) );
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

    void amountViewSetting( String type ) {
        viewMapper.textAmount.setText( String.format( "%s " + type, transfer.getToBalance( ) ) );
        viewMapper.textAmountList.setText( String.format( "%s " + type, transfer.getToBalance( ) ) );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnFeeOption ) {
            FeePopup.newInstance( this )
                    .setOnFeePopupSaveClickListener( this )
                    .setFee( "0.00042", CoinType.of( planet.getCoinType( ) ).name( ) )
                    .show( );
        } else if ( v == viewMapper.btnFeeReset ) {
            viewMapper.groupSeekBar.setVisibility( View.VISIBLE );
            viewMapper.groupFeeOption.setVisibility( View.VISIBLE );
            viewMapper.btnFeeReset.setVisibility( View.GONE );

            //reset fee
            viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( 2 ) ) );
            viewMapper.seekBar.setProgress( 2 );

        } else if ( v == viewMapper.btnSubmit ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.TRANSFER, PinCodeCertificationActivity.class );
        }

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.TRANSFER && resultCode == RESULT_OK ) {
            Bundle bundle = new Bundle( );
            if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) {
                bundle.putSerializable( C.bundleKey.PLANET, planet );

            } else if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {

                if ( getSerialize( C.bundleKey.ERC20 ) != null ) {
                    bundle.putSerializable( C.bundleKey.PLANET, planet );
                    bundle.putSerializable( C.bundleKey.ERC20, erc20 );
                } else {
                    bundle.putSerializable( C.bundleKey.PLANET, planet );
                }
            }
            //fee setting
            transfer.setFee( viewMapper.textFee.getText( ).toString( ) );
            bundle.putSerializable( C.bundleKey.TRANSFER, transfer );
            setTransition( Transition.SLIDE_UP );
            sendAction( TxReceiptActivity.class, bundle );
        } else if ( requestCode == C.requestCode.TRANSFER && resultCode == RESULT_CANCELED ) {

            //            Toast.makeText( this, localized( R.string.transfer_confirm_not_password_title ), Toast.LENGTH_SHORT ).show( );

        }

    }

    @Override
    public void onFeePopupSaveClick( String fee ) {
        super.onBackPressed( );
        viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ), fee ) );
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
        if ( fee.get( progress ) == null )
            viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), new BigDecimal( ETHGasPrice.DEFALUT_GAS_GWEI ).movePointLeft( 9 ).multiply( ETHGasPrice.DEFALUT_GAS_LIMIT ).stripTrailingZeros( ).toEngineeringString( ) ) );
        viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( progress ) ) );
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
        TextView textAddress;
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
            textAddress = findViewById( R.id.text_transfer_confirm_address );
            textAmount = findViewById( R.id.text_transfer_confirm_amount );
            textAmountList = findViewById( R.id.text_transfer_confirm_amount_ );
            textFromName = findViewById( R.id.text_transfer_confirm_from_name );
            textFee = findViewById( R.id.text_transfer_confirm_fee );

            btnFeeReset = findViewById( R.id.text_transfer_confirm_fee_reset );
            btnFeeOption = findViewById( R.id.group_transfer_confirm_fee_option_sub );
            btnSubmit = findViewById( R.id.btn_submit );

        }
    }
}
