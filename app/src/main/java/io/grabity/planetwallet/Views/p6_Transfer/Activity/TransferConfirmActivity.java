package io.grabity.planetwallet.Views.p6_Transfer.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.core.ImageLoader;

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
import io.grabity.planetwallet.VO.BitCoinFee;
import io.grabity.planetwallet.VO.ErrorResult;
import io.grabity.planetwallet.VO.EthereumFee;
import io.grabity.planetwallet.VO.MainItems.BTC;
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
    private EthereumFee ethereumFee;
    private BitCoinFee bitCoinFee;

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
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.TRANSFER ) == null ) {
            finish( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            transfer = ( Transfer ) getSerialize( C.bundleKey.TRANSFER );

            if ( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ) {
                new Get( this ).action( Route.URL( "gas" ), 0, 0, null );
            } else {
                new Get( this ).action( Route.URL( "fee", "BTC" ), 1, 0, null );
            }


            if ( getSerialize( C.bundleKey.MAIN_ITEM ) != null ) {
                if ( Utils.equals( getSerialize( C.bundleKey.MAIN_ITEM ).getClass( ), ERC20.class ) ) {
                    erc20 = ( ERC20 ) getSerialize( C.bundleKey.MAIN_ITEM );
                    viewMapper.toolBar.setTitle( localized( R.string.transfer_confirm_toolbar_title, erc20.getName( ) ) );
                    amountViewSetting( erc20.getSymbol( ) );
                    isERC = true;
                }
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
            fee = new ArrayList<>( );
            if ( statusCode == 200 && requestCode == 0 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, EthereumFee.class );
                if ( returnVO.isSuccess( ) ) {
                    ethereumFee = ( EthereumFee ) returnVO.getResult( );
                    //GWei -> wei
                    fee.add( Utils.feeCalculation( Utils.convertUnit( ethereumFee.getSafeLow( ), 9, 18 ), !isERC ? EthereumFee.ETH_DEFAULT_GAS_LIMIT : EthereumFee.ERC_DEFAULT_GAS_LIMIT ) );
                    fee.add( Utils.feeCalculation( Utils.convertUnit( ethereumFee.getStandard( ), 9, 18 ), !isERC ? EthereumFee.ETH_DEFAULT_GAS_LIMIT : EthereumFee.ERC_DEFAULT_GAS_LIMIT ) );
                    fee.add( Utils.feeCalculation( Utils.convertUnit( ethereumFee.getFast( ), 9, 18 ), !isERC ? EthereumFee.ETH_DEFAULT_GAS_LIMIT : EthereumFee.ERC_DEFAULT_GAS_LIMIT ) );
                    fee.add( Utils.feeCalculation( Utils.convertUnit( ethereumFee.getFastest( ), 9, 18 ), !isERC ? EthereumFee.ETH_DEFAULT_GAS_LIMIT : EthereumFee.ERC_DEFAULT_GAS_LIMIT ) );

                }
            } else if ( statusCode == 200 && requestCode == 1 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, BitCoinFee.class );
                if ( returnVO.isSuccess( ) ) {
                    bitCoinFee = ( BitCoinFee ) returnVO.getResult( );
                    //satoshi -> bit
                    fee.add( Utils.ofZeroClear( Utils.convertUnit( bitCoinFee.getHourFee( ), 8, 16 ) ) );
                    fee.add( Utils.ofZeroClear( Utils.convertUnit( bitCoinFee.getHalfHourFee( ), 8, 16 ) ) );
                    fee.add( Utils.ofZeroClear( Utils.convertUnit( bitCoinFee.getFastestFee( ), 8, 16 ) ) );

                }
            }

            viewMapper.textFee.setText( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( 2 ) ) :
                    String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( 1 ) ) );
            gasPriceAndLimitSetting( planet.getCoinType( ), Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? 2 : 1, isERC );

        }

    }

    void gasPriceAndLimitSetting( Integer coinType, int progress, boolean isERC ) {
        if ( Utils.equals( coinType, CoinType.ETH.getCoinType( ) ) ) {
            switch ( progress ) {
                case 0:
                    gasPrice = ethereumFee.getSafeLow( );
                    break;
                case 1:
                    gasPrice = ethereumFee.getStandard( );
                    break;
                case 2:
                    gasPrice = ethereumFee.getFast( );
                    break;
                case 3:
                    gasPrice = ethereumFee.getFastest( );
                    break;
                default:
                    gasPrice = !isERC ? EthereumFee.ETH_DEFAULT_GAS_GWEI : EthereumFee.ERC_DEFAULT_GAS_GWEI;
            }
            gasLimit = !isERC ? EthereumFee.ETH_DEFAULT_GAS_LIMIT : EthereumFee.ERC_DEFAULT_GAS_LIMIT;

        } else {
            switch ( progress ) {
                case 0:
                    gasPrice = bitCoinFee.getHourFee( );
                    break;
                case 1:
                    gasPrice = bitCoinFee.getHalfHourFee( );
                    break;
                case 2:
                    gasPrice = bitCoinFee.getFastestFee( );
                    break;
            }

        }


    }

    void viewSetting( ) {
        viewMapper.groupPlanet.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ? View.VISIBLE : View.GONE );
        viewMapper.groupAddress.setVisibility( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ? View.VISIBLE : View.GONE );
        viewMapper.groupFeeOption.setVisibility( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? View.VISIBLE : View.GONE );
        viewMapper.textFromName.setText( planet.getName( ) );
        viewMapper.seekBar.setMax( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? 3 : 2 );

        if ( Utils.equals( transfer.getChoice( ), C.transferChoice.PLANET_NAME ) ) {
            viewMapper.planetView.setData( transfer.getToAddress( ) );
            viewMapper.textPlanetName.setText( transfer.getToName( ) );
            viewMapper.textPlanetAddress.setText( Utils.addressReduction( transfer.getToAddress( ) ) );
        } else if ( Utils.equals( transfer.getChoice( ), C.transferChoice.ADDRESS ) ) {
            if ( getSerialize( C.bundleKey.MAIN_ITEM ) != null ) {
                Utils.setPadding( viewMapper.imageIcon, 0, 0, 0, 0 );
                ImageLoader.getInstance( ).displayImage( Route.URL( erc20.getImg_path( ) ), viewMapper.imageIcon );
                viewMapper.imageIconBackground.setVisibility( View.INVISIBLE );
            } else {
                if ( !getCurrentTheme( ) ) {
                    viewMapper.imageIcon.setImageResource( Utils.equals( CoinType.BTC.getCoinType( ), planet.getCoinType( ) ) ? R.drawable.icon_transfer_bit_black : R.drawable.icon_transfer_eth_black );
                } else {
                    viewMapper.imageIcon.setImageResource( Utils.equals( CoinType.BTC.getCoinType( ), planet.getCoinType( ) ) ? R.drawable.icon_transfer_bit_white : R.drawable.icon_transfer_eth_white );
                }
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
                    .setFee( !isERC ? EthereumFee.ETH_DEFAULT_FEE : EthereumFee.ERC_DEFAULT_FEE, CoinType.of( planet.getCoinType( ) ).name( ) )
                    .setERC( isERC )
                    .show( );
        } else if ( v == viewMapper.btnFeeReset ) {
            viewMapper.groupSeekBar.setVisibility( View.VISIBLE );
            viewMapper.groupFeeOption.setVisibility( View.VISIBLE );
            viewMapper.btnFeeReset.setVisibility( View.GONE );

            //reset fee
            viewMapper.textFee.setText( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( 2 ) ) :
                    String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( 1 ) ) );
            viewMapper.seekBar.setProgress( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? 2 : 1 );
            gasPriceAndLimitSetting( planet.getCoinType( ), Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ? 2 : 1, isERC );


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
                    .value( Utils.convertUnit( transfer.getToBalance( ), 18, 0 ) )
                    .gasPrice( Utils.convertUnit( gasPrice, 9, 0 ) )
                    .gasLimit( gasLimit );

            serializeTx = transaction.getRawTransaction( planet.getPrivateKey( KeyPairStore.getInstance( ), C.PINCODE ) );
        } else if ( Utils.equals( planet.getCoinType( ), CoinType.ETH.getCoinType( ) ) ) {
            transaction = new Transaction( new ETH( ) )
                    .setDeviceKey( C.DEVICE_KEY )
                    .from( planet.getAddress( ) )
                    .to( transfer.getToAddress( ) )
                    .value( Utils.convertUnit( transfer.getToBalance( ), 18, 0 ) )
                    .gasPrice( Utils.convertUnit( gasPrice, 9, 0 ) )
                    .gasLimit( gasLimit );

            serializeTx = transaction.getRawTransaction( planet.getPrivateKey( KeyPairStore.getInstance( ), C.PINCODE ) );
        } else {
            transaction = new Transaction( new BTC( ) )
                    .setDeviceKey( C.DEVICE_KEY )
                    .from( planet.getAddress( ) )
                    .to( transfer.getToAddress( ) )
                    .value( Utils.convertUnit( transfer.getToBalance( ), 8, 0 ) )
                    .gasPrice( gasPrice );

            serializeTx = transaction.getRawTransaction( planet.getPrivateKey( KeyPairStore.getInstance( ), C.PINCODE ) );
        }

        return serializeTx;

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.TRANSFER && resultCode == RESULT_OK ) {
            Bundle bundle = new Bundle( );
            if ( getSerialize( C.bundleKey.MAIN_ITEM ) != null ) {
                if ( Utils.equals( getSerialize( C.bundleKey.MAIN_ITEM ).getClass( ), ERC20.class ) ) {
                    bundle.putSerializable( C.bundleKey.PLANET, planet );
                    bundle.putSerializable( C.bundleKey.MAIN_ITEM, erc20 );
                    transfer.setSerializeTx( transaction( planet, erc20 ) );
                }
            } else {
                bundle.putSerializable( C.bundleKey.PLANET, planet );
                transfer.setSerializeTx( transaction( planet, null ) );
            }
            transfer.setFee( viewMapper.textFee.getText( ).toString( ) );

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
                        ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, ErrorResult.class );
                        ErrorResult errorResult = ( ErrorResult ) returnVO.getResult( );
                        if ( errorResult == null ) return;
                        CustomToast.makeText( this, errorResult.getErrorMsg( ) ).show( );
                    }
                }
            } ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "transfer", transaction.getSymbol( ) ), 0, 0, new Transfer( transfer.getSerializeTx( ) ) );
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
        if ( fee == null ) return;
        viewMapper.textFee.setText( String.format( "%s " + CoinType.of( planet.getCoinType( ) ).name( ), fee.get( progress ) ) );
        gasPriceAndLimitSetting( planet.getCoinType( ), progress, isERC );
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
