package io.grabity.planetwallet.Views.p6_Transfer.Activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.Widgets.BarcodeReaderView;
import io.grabity.planetwallet.Widgets.CustomToast;
import io.grabity.planetwallet.Widgets.ToolBar;

public class ScanQRActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, BarcodeReaderView.OnBarcodeDetectListener {

    private ViewMapper viewMapper;
    private Planet planet;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setStatusTransparent( true );
        setContentView( R.layout.activity_scan_qr );
        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        viewMapper.toolBar.addLeftButton( new ToolBar.ButtonItem( R.drawable.image_toolbar_close_white ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setTitle( localized( R.string.scan_qr_toolbar_title ) );
        viewMapper.toolBar.setTitleColor( Color.parseColor( "#FFFFFF" ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );
        viewMapper.barcodeReaderView.setOnBarcodeDetectListener( this );

        viewMapper.toolBar.getViewTreeObserver( ).addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener( ) {
            @Override
            public void onGlobalLayout( ) {
                viewMapper.toolBar.getViewTreeObserver( ).removeOnGlobalLayoutListener( this );
                ( ( ViewGroup.MarginLayoutParams ) viewMapper.toolBar.getLayoutParams( ) ).height = ( int ) ( Utils.dpToPx( ScanQRActivity.this, 68 ) + getResources( ).getDimensionPixelSize( getResources( ).getIdentifier( "status_bar_height", "dimen", "android" ) ) );
                viewMapper.toolBar.requestLayout( );
            }
        } );


    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        requestPermissions( C.requestCode.QR_CODE, Manifest.permission.CAMERA );
    }

    @Override
    protected void neverNotAllowed( int code, String permission ) {
        super.neverNotAllowed( code, permission );
        if ( Utils.equals( code, C.requestCode.QR_CODE ) ) {
            CustomToast.makeText( this , localized( R.string.camera_permission_never_not_allowed_title ) );
            super.onBackPressed( );
        }
    }

    @Override
    protected void permissionNotAllowed( int code, String permission ) {
        super.permissionNotAllowed( code, permission );
        if ( Utils.equals( code, C.requestCode.QR_CODE ) ) {
            CustomToast.makeText( this , localized( R.string.camera_permission_not_allowed_title ) );
            super.onBackPressed( );
        }
    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            finish( );
        } else {
            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
        }
    }

    @Override
    public void onBarcodeDetect( String contents ) {
        String address = null;
        if ( CoinType.ETH.getCoinType( ).equals( planet.getCoinType( ) ) ) {
            Pattern p = Pattern.compile( "0x[0-9a-fA-F]{40}$" );
            Matcher m = p.matcher( contents );
            if ( m.find( ) ) {
                address = m.group( 0 );
                PLog.e( "ETH address : " + address );
            }
        } else if ( CoinType.BTC.getCoinType( ).equals( planet.getCoinType( ) ) ) {
            Pattern p = Pattern.compile( "1[0-9a-fA-F]{33}$" );
            Matcher m = p.matcher( contents );

            if ( m.find( ) ) {
                address = m.group( 0 );
                PLog.e( "BTC address : " + address );
            }
        }

        if ( address == null ) return;
        setResult( RESULT_OK, new Intent( ).putExtra( C.bundleKey.QRCODE, address ) );
        runOnUiThread( ScanQRActivity.super::onBackPressed );

    }

    public class ViewMapper {

        ToolBar toolBar;
        BarcodeReaderView barcodeReaderView;


        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            barcodeReaderView = findViewById( R.id.barcodeReaderView );

        }
    }
}
