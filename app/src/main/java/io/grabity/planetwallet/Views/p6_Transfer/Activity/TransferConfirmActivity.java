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
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.transaction.Transaction;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.ETHGasProvider;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Transfer;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p6_Transfer.Popups.FeePopup;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.StretchImageView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferConfirmActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, SeekBar.OnSeekBarChangeListener, FeePopup.OnFeePopupSaveClickListener {


    private ViewMapper viewMapper;
    private Planet planet;
    private ERC20 erc20;
    private Transfer transfer;
    private ArrayList< String > fee;
    private Transaction transaction;
    private ETHGasProvider ethGasProvider;

    private String gasPrice;
    private String gasLimit;

    private boolean isERC = false;

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

        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
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

                isERC = true;

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
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, ETHGasProvider.class );
                if ( returnVO.isSuccess( ) ) {
                    ethGasProvider = ( ETHGasProvider ) returnVO.getResult( );

                    fee.add( new BigDecimal( ethGasProvider.getSafeLow( ) ).movePointLeft( 9 ).multiply( !isERC ? ETHGasProvider.ETH_DEFAULT_GAS_LIMIT : ETHGasProvider.ERC_DEFAULT_GAS_LIMIT ).stripTrailingZeros( ).toString( ) );
                    fee.add( new BigDecimal( ethGasProvider.getStandard( ) ).movePointLeft( 9 ).multiply( !isERC ? ETHGasProvider.ETH_DEFAULT_GAS_LIMIT : ETHGasProvider.ERC_DEFAULT_GAS_LIMIT ).stripTrailingZeros( ).toString( ) );
                    fee.add( new BigDecimal( ethGasProvider.getFast( ) ).movePointLeft( 9 ).multiply( !isERC ? ETHGasProvider.ETH_DEFAULT_GAS_LIMIT : ETHGasProvider.ERC_DEFAULT_GAS_LIMIT ).stripTrailingZeros( ).toString( ) );
                    fee.add( new BigDecimal( ethGasProvider.getFastest( ) ).movePointLeft( 9 ).multiply( !isERC ? ETHGasProvider.ETH_DEFAULT_GAS_LIMIT : ETHGasProvider.ERC_DEFAULT_GAS_LIMIT ).stripTrailingZeros( ).toString( ) );

                }
            } else {
                for ( int i = 0; i < 4; i++ ) {
                    fee.add( !isERC ? ETHGasProvider.ETH_DEFAULT_FEE : ETHGasProvider.ERC_DEFAULT_FEE );
                }
            }
            feeSetting( );
        } else {
            for ( int i = 0; i < 4; i++ ) {
                fee.add( !isERC ? ETHGasProvider.ETH_DEFAULT_FEE : ETHGasProvider.ERC_DEFAULT_FEE );
            }
            feeSetting( );
        }

    }

    void gasPriceAndLimitSetting( int progress, boolean isERC ) {
        if ( ethGasProvider == null ) {
            gasPrice = !isERC ? ETHGasProvider.ETH_DEFAULT_GAS_GWEI : ETHGasProvider.ERC_DEFAULT_GAS_GWEI;
            gasLimit = !isERC ? ETHGasProvider.ETH_DEFAULT_GAS_LIMIT.toString( ) : ETHGasProvider.ERC_DEFAULT_GAS_LIMIT.toString( );
            return;
        }

        switch ( progress ) {
            case 0:
                gasPrice = ethGasProvider.getSafeLow( );
                break;
            case 1:
                gasPrice = ethGasProvider.getStandard( );
                break;
            case 2:
                gasPrice = ethGasProvider.getFast( );
                break;
            case 3:
                gasPrice = ethGasProvider.getFastest( );
                break;
            default:
                gasPrice = !isERC ? ETHGasProvider.ETH_DEFAULT_GAS_GWEI : ETHGasProvider.ERC_DEFAULT_GAS_GWEI;
        }
        gasLimit = !isERC ? ETHGasProvider.ETH_DEFAULT_GAS_LIMIT.toString( ) : ETHGasProvider.ERC_DEFAULT_GAS_LIMIT.toString( );


    }


    void feeSetting( ) {
        viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( 2 ) ) );
        gasPriceAndLimitSetting( 2, isERC );
    }

    void viewSetting( ) {
        viewMapper.groupPlanet.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ? View.VISIBLE : View.GONE );
        viewMapper.groupAddress.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ? View.VISIBLE : View.GONE );
        viewMapper.groupSeekBar.setVisibility( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? View.VISIBLE : View.GONE );
        viewMapper.groupFeeOption.setVisibility( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? View.VISIBLE : View.GONE );
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
                    .setFee( !isERC ? ETHGasProvider.ETH_DEFAULT_FEE : ETHGasProvider.ERC_DEFAULT_FEE, CoinType.of( planet.getCoinType( ) ).name( ) )
                    .setERC( isERC )
                    .show( );
        } else if ( v == viewMapper.btnFeeReset ) {
            viewMapper.groupSeekBar.setVisibility( View.VISIBLE );
            viewMapper.groupFeeOption.setVisibility( View.VISIBLE );
            viewMapper.btnFeeReset.setVisibility( View.GONE );

            //reset fee
            viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( 2 ) ) );
            viewMapper.seekBar.setProgress( 2 );

            gasPriceAndLimitSetting( 2, isERC );


        } else if ( v == viewMapper.btnSubmit ) {
            setTransition( Transition.SLIDE_UP );
            sendAction( C.requestCode.TRANSFER, PinCodeCertificationActivity.class );
        }

    }

    private String transaction( Planet planet, ERC20 erc20 ) {
        String serializeTx;
        if ( erc20 != null ) {
            transaction = new Transaction( erc20 )
                    .setDeviceKey( C.DEVICE_KEY )
                    .from( planet.getAddress( ) )
                    .to( transfer.getToAddress( ) )
                    .value( new BigDecimal( transfer.getToBalance( ) ).movePointRight( 18 ).toString( ) )
                    .gasPrice( new BigDecimal( gasPrice ).movePointRight( 9 ).toString( ) )
                    .gasLimit( gasLimit );

            serializeTx = transaction.getRawTransaction( planet.getPrivateKey( KeyPairStore.getInstance( ), C.PINCODE ) );
            PLog.e( "ERC serializeTx : " + serializeTx );
        } else {
            transaction = new Transaction( new ETH( ) )
                    .setDeviceKey( C.DEVICE_KEY )
                    .from( planet.getAddress( ) )
                    .to( transfer.getToAddress( ) )
                    .value( new BigDecimal( transfer.getToBalance( ) ).movePointRight( 18 ).toString( ) )
                    .gasPrice( new BigDecimal( gasPrice ).movePointRight( 9 ).toString( ) )
                    .gasLimit( gasLimit );

            serializeTx = transaction.getRawTransaction( planet.getPrivateKey( KeyPairStore.getInstance( ), C.PINCODE ) );
            PLog.e( "ETH serializeTx : " + serializeTx );
        }

        return serializeTx;

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

                    transfer.setSerializeTx( transaction( planet, erc20 ) );

                } else {
                    bundle.putSerializable( C.bundleKey.PLANET, planet );
                    transfer.setSerializeTx( transaction( planet, null ) );
                }
            }
            transfer.setFee( viewMapper.textFee.getText( ).toString( ) );
            PLog.e( "transfer.getSerializeTx() : " + transfer.getSerializeTx( ) );

            new Post( ( error, requestCode1, resultCode1, statusCode, result ) -> {
                PLog.e( "result : " + result );
                if ( !error ) {
                    if ( statusCode == 200 && requestCode1 == 0 ) {
                        ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Transfer.class );
                        if ( returnVO.isSuccess( ) ) {
                            Transfer t = ( Transfer ) returnVO.getResult( );
                            transfer.setTxHash( t.getTxHash( ) );
                            bundle.putSerializable( C.bundleKey.TRANSFER, transfer );
                            setTransition( Transition.SLIDE_UP );
                            sendAction( TxReceiptActivity.class, bundle );
                        }
                    } else {
                        CustomToast.makeText( this, "현재 거래를 완료할 수 없는 상태입니다." ).show( );
                    }
                } else {
                    CustomToast.makeText( this, "현재 거래를 완료할 수 없는 상태입니다." ).show( );
                }
            } ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "transfer", transaction.getSymbol( ) ), 0, 0, new Transfer( transfer.getSerializeTx( ) ) );
        } else if ( requestCode == C.requestCode.TRANSFER && resultCode == RESULT_CANCELED ) {

            //            Toast.makeText( this, localized( R.string.transfer_confirm_not_password_title ), Toast.LENGTH_SHORT ).show( );

        }

    }

    @Override
    public void onFeePopupSaveClick( String fee, String gasPrice, String gasLimit ) {
        super.onBackPressed( );
        viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ), fee ) );
        viewMapper.groupSeekBar.setVisibility( View.GONE );
        viewMapper.groupFeeOption.setVisibility( View.GONE );
        viewMapper.btnFeeReset.setVisibility( View.VISIBLE );

        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;

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
        if ( fee.get( progress ) == null ) {
            viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), ETHGasProvider.ETH_DEFAULT_FEE ) );
            return;
        }
        viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( progress ) ) );
        gasPriceAndLimitSetting( progress, isERC );
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
