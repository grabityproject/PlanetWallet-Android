package io.grabity.planetwallet.Views.p8_Tx.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Views.p6_Transfer.Activity.TransferActivity;
import io.grabity.planetwallet.Widgets.BarcodeView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.ToolBar;

public class DetailAddressActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener {

    private ViewMapper viewMapper;
    private Planet planet;
    private ERC20 erc20;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail_address );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );
    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );
        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_BACK ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.btnCopy.setOnClickListener( this );
        viewMapper.btnTransfer.setOnClickListener( this );

    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            finish( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );

            if ( getSerialize( C.bundleKey.MAIN_ITEM ) != null ) {
                if ( Utils.equals( getSerialize( C.bundleKey.MAIN_ITEM ).getClass( ), ERC20.class ) ) {
                    erc20 = ( ERC20 ) getSerialize( C.bundleKey.MAIN_ITEM );
                }
            }
            setUpView( );
        }
    }

    void setUpView( ) {
        viewMapper.toolBar.setTitle( String.format( "Receive %s", erc20 != null ? erc20.getSymbol( ) : CoinType.of( planet.getCoinType( ) ).name( ) ) );
        viewMapper.textName.setText( planet.getName( ) );
        viewMapper.textAddress.setText( planet.getAddress( ) );
        viewMapper.barcodeView.setData( planet.getAddress( ) );
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnCopy ) {

            Utils.copyToClipboard( this, planet.getAddress( ) );
            CustomToast.makeText( this, localized( R.string.main_copy_to_clipboard ) ).show( );

        } else if ( v == viewMapper.btnTransfer ) {
            if ( erc20 != null ) {
                sendAction( TransferActivity.class, Utils.mergeBundles( Utils.createSerializableBundle( C.bundleKey.PLANET, planet ), Utils.createSerializableBundle( C.bundleKey.MAIN_ITEM, erc20 ) ) );
            } else {
                sendAction( TransferActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
            }
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_BACK ) ) {
            super.onBackPressed( );
        }
    }

    public class ViewMapper {

        ToolBar toolBar;
        BarcodeView barcodeView;
        TextView textName;
        TextView textAddress;

        View btnCopy;
        View btnTransfer;

        public ViewMapper( ) {

            toolBar = findViewById( R.id.toolBar );
            barcodeView = findViewById( R.id.barcode_detail_address );
            textName = findViewById( R.id.text_detail_address_name );
            textAddress = findViewById( R.id.text_detail_address_address );

            btnCopy = findViewById( R.id.btn_detail_address_copy );
            btnTransfer = findViewById( R.id.btn_detail_address_transfer );
        }
    }
}
