package io.grabity.planetwallet.Views.p6_Transfer.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.internal.LinkedTreeMap;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.MiniFramework.wallet.transaction.Transaction;
import io.grabity.planetwallet.MiniFramework.wallet.transaction.UTXO;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p6_Transfer.Popups.EthFeePopup;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class TransferConfirmActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, SeekBar.OnSeekBarChangeListener, EthFeePopup.OnEthFeePopupListener {


    private ViewMapper viewMapper;

    private Planet planet;
    private MainItem mainItem;
    private Tx tx;

    private ArrayList< String > fee;

    private Transaction transaction;

    private int networkTaskCount = 0;


    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        getPlanetWalletApplication( ).recordActvityStack( this );
        setContentView( R.layout.activity_transfer_confimr );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.seekBar.setOnSeekBarChangeListener( this );

        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnSubmit.setOnClickListener( this );
        viewMapper.seekBar.setEnabled( false );
        viewMapper.btnSubmit.setEnabled( false );

        viewMapper.seekBar.setThumb( getResources( ).getDrawable( R.drawable.seekbar_thumb, this.getTheme( ) ) );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null || getSerialize( C.bundleKey.MAIN_ITEM ) == null || getSerialize( C.bundleKey.TX ) == null ) {
            finish( );
        } else {
            fee = new ArrayList<>( );

            // Data getting
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            mainItem = ( MainItem ) getSerialize( C.bundleKey.MAIN_ITEM );
            tx = ( Tx ) getSerialize( C.bundleKey.TX );

            // Set Transaction
            transaction = Transaction.create( mainItem ).setTx( tx ).setDeviceKey( C.DEVICE_KEY );

            // toolbar, top amount binding
            viewMapper.toolBar.setTitle( localized( R.string.transfer_confirm_toolbar_title, mainItem.getSymbol( ) ) );
            viewMapper.textAmount.setText( String.format( Locale.US, "%s %s", Utils.removeLastZero( Utils.toMaxUnit( mainItem, tx.getAmount( ) ) ), mainItem.getSymbol( ) ) );

            // planet, address check
            viewMapper.groupAddress.setVisibility( tx.getTo_planet( ) == null ? View.VISIBLE : View.GONE );
            viewMapper.groupPlanet.setVisibility( tx.getTo_planet( ) == null ? View.GONE : View.VISIBLE );

            // planet, address data Biding
            Utils.addressForm( viewMapper.textAddress, tx.getTo( ) );
            viewMapper.planetView.setData( tx.getTo( ) );
            viewMapper.textPlanetName.setText( tx.getTo_planet( ) );
            viewMapper.textPlanetAddress.setText( Utils.addressReduction( tx.getTo( ) ) );

            // bottom list view data binding
            viewMapper.textFromName.setText( planet.getName( ) );
            viewMapper.textAmountList.setText( String.format( Locale.US, "%s %s", Utils.removeLastZero( Utils.toMaxUnit( mainItem, tx.getAmount( ) ) ), mainItem.getSymbol( ) ) );
            viewMapper.textFee.setText( String.format( Locale.US, "%s %s", "-", CoinType.of( mainItem.getCoinType( ) ).getParent( ) ) );

            //fee api
            new Get( this ).action( Route.URL( "fee", planet.getSymbol( ) ), 0, 0, null );

            // CoinType
            if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.BTC ) {

                viewMapper.groupFeeOption.setVisibility( View.GONE );
                viewMapper.imageIcon.setImageResource( R.drawable.icon_btc );
                networkTaskCount = 0;
                new Get( this ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "utxo", "list", CoinType.BTC.name( ), planet.getAddress( ) ), 1, 0, null );

            } else if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ETH ) {

                viewMapper.groupFeeOption.setVisibility( View.VISIBLE );
                viewMapper.imageIcon.setImageResource( R.drawable.icon_eth );
                networkTaskCount = 0;
                new Get( this ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "nonce", CoinType.ETH.name( ), planet.getAddress( ) ), 2, 0, null );

            } else if ( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ERC20 ) {

                viewMapper.groupFeeOption.setVisibility( View.VISIBLE );
                ImageLoader.getInstance( ).displayImage( Route.URL( mainItem.getImg_path( ) ), viewMapper.imageIcon );
                networkTaskCount = 0;
                new Get( this ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "nonce", CoinType.ETH.name( ), planet.getAddress( ) ), 2, 0, null );

            }
        }
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( !error ) {
            if ( statusCode == 200 ) { //  fee
                if ( requestCode == 0 ) {
                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class );
                    if ( returnVO.isSuccess( ) ) {
                        networkTaskCount++;
                        LinkedTreeMap< String, String > map = ( LinkedTreeMap< String, String > ) returnVO.getResult( );
                        fee = new ArrayList<>( map.values( ) );
                        fee.sort( ( o1, o2 ) -> new BigDecimal( o1 ).compareTo( new BigDecimal( o2 ) ) );

                    }
                } else if ( requestCode == 1 ) {
                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, UTXO.class );
                    if ( returnVO.isSuccess( ) ) {
                        networkTaskCount++;
                        ArrayList< UTXO > utxos = ( ArrayList< UTXO > ) returnVO.getResult( );
                        tx.setUtxos( utxos );

                    }
                } else if ( requestCode == 2 ) {
                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Tx.class );
                    if ( returnVO.isSuccess( ) ) {
                        networkTaskCount++;
                        Tx nonce = ( Tx ) returnVO.getResult( );
                        tx.setNonce( nonce.getNonce( ) );

                    }
                }
            }
        }

        if ( networkTaskCount == 2 ) {
            feeViewSetting( );
            viewMapper.btnSubmit.setEnabled( true );
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnFeeOption ) {

            EthFeePopup.newInstance( this )
                    .setOnEthFeePopupListener( this )
                    .setCoinType( CoinType.of( mainItem.getCoinType( ) ) )
                    .setEthBalance( planet.getMainItem( ).getBalance( ) )
                    .setEthAmount( CoinType.of( mainItem.getCoinType( ) ) == CoinType.ETH ? tx.getAmount( ) : null )
                    .show( );

        } else if ( v == viewMapper.btnFeeReset ) {

            viewMapper.groupSeekBar.setVisibility( View.VISIBLE );
            viewMapper.groupFeeOption.setVisibility( View.VISIBLE );
            viewMapper.btnFeeReset.setVisibility( View.GONE );

            if ( fee != null && fee.size( ) > 0 ) {
                try {
                    tx.setGasPrice( fee.get( viewMapper.seekBar.getProgress( ) ) );
                    tx.setGasLimit( null );
                } catch ( NullPointerException | IndexOutOfBoundsException e ) {
                    tx.setGasPrice( null );
                    tx.setGasLimit( null );
                }


                viewMapper.textFee.setText( String.format( Locale.US, "%s %s",
                        Utils.removeLastZero( Utils.toMaxUnit( mainItem, transaction.estimateFee( ) ) ),
                        CoinType.of( mainItem.getCoinType( ) ).getParent( ) )
                );
            }

        } else if ( v == viewMapper.btnSubmit ) {

            if ( tooLargeFee( ) ) {
                CustomToast.makeText( this, "보내려는 금액과 수수료의 합이 총 금액을 넘습니다." ).show( );
            } else {
                setTransition( Transition.SLIDE_UP );
                sendAction( C.requestCode.TRANSFER, PinCodeCertificationActivity.class );
            }


        }
    }

    private boolean tooLargeFee( ) {
        BigDecimal totalBalance = new BigDecimal( planet.getMainItem( ).getBalance( ) );
        BigDecimal amount = new BigDecimal( tx.getAmount( ) );
        BigDecimal fee = new BigDecimal( transaction.estimateFee( ) );

        return totalBalance.subtract( amount ).subtract( fee ).signum( ) <= 0;
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == C.requestCode.TRANSFER && resultCode == RESULT_OK ) {
            viewMapper.btnSubmit.setEnabled( false );

            String rawTx = transaction.getRawTransaction( planet.getPrivateKey( KeyPairStore.getInstance( ), getPlanetWalletApplication( ).getPINCODE( ) ) );

            new Post( ( error, requestCode1, resultCode1, statusCode, result ) -> {
                if ( !error ) {
                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class );
                    if ( returnVO.isSuccess( ) ) {
                        LinkedTreeMap< String, String > map = ( LinkedTreeMap< String, String > ) returnVO.getResult( );
                        tx.setTx_id( map.get( "txHash" ) );

                        setTransition( Transition.SLIDE_UP );
                        sendAction( TxReceiptActivity.class,
                                Utils.mergeBundles(
                                        Utils.createSerializableBundle( C.bundleKey.PLANET, planet ),
                                        Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, mainItem ),
                                        Utils.createSerializableBundle( C.bundleKey.TX, tx ) ) );

                    } else {
                        CustomToast.makeText( this, "현재 거래를 완료할 수 없는 상태입니다." ).show( );
                    }
                } else {
                    CustomToast.makeText( this, "현재 거래를 완료할 수 없는 상태입니다." ).show( );
                }
            } ).setDeviceKey( C.DEVICE_KEY )
                    .action( Route.URL( "transfer", mainItem.getSymbol( ) ), 0, 0, Utils.createStringHashMap( "serializeTx", rawTx ) );
        }
    }

    public void feeViewSetting( ) {
        if ( fee.size( ) > 0 ) {
            viewMapper.seekBar.setEnabled( true );
            viewMapper.seekBar.setMax( fee.size( ) - 1 );
            viewMapper.seekBar.setProgress( ( int ) Math.floor( ( double ) fee.size( ) / 2.0d ) );

            viewMapper.btnFeeReset.setOnClickListener( this );
            viewMapper.btnFeeOption.setOnClickListener( this );
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        }
    }

    @Override
    public void onBackPressed( ) {
        super.onBackPressed( );

    }

    @Override
    public void onFeePopupSaveClick( EthFeePopup ethFeePopup, String gasPriceWei, String gasLimit ) {

        tx.setGasPrice( gasPriceWei ); // ETH
        tx.setGasLimit( gasLimit );

        viewMapper.textFee.setText( String.format( Locale.US, "%s %s",
                Utils.removeLastZero( Utils.toMaxUnit( mainItem, transaction.estimateFee( ) ) ),
                CoinType.of( mainItem.getCoinType( ) ).getParent( ) )
        );

        viewMapper.groupSeekBar.setVisibility( View.GONE );
        viewMapper.groupFeeOption.setVisibility( View.GONE );
        viewMapper.btnFeeReset.setVisibility( View.VISIBLE );
    }

    @Override
    public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {

        try {
            // Set GasPrice Changed
            tx.setGasPrice( fee.get( progress ) ); // ETH
            tx.setGasLimit( null );
            tx.setActualFee( fee.get( progress ) ); // BTC

            viewMapper.textFee.setText( String.format( Locale.US, "%s %s",
                    Utils.removeLastZero( Utils.toMaxUnit( mainItem, transaction.estimateFee( ) ) ),
                    CoinType.of( mainItem.getCoinType( ) ).getParent( ) )
            );

        } catch ( IndexOutOfBoundsException | NullPointerException e ) {
            e.printStackTrace( );
        }

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

        CircleImageView imageIcon;

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
