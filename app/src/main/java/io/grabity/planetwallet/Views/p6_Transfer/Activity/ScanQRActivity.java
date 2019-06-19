package io.grabity.planetwallet.Views.p6_Transfer.Activity;

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
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Widgets.BarcodeReaderView;
import io.grabity.planetwallet.Widgets.ToolBar;

public class ScanQRActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, BarcodeReaderView.OnBarcodeDetectListener {

    private ViewMapper viewMapper;

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
        viewMapper.toolBar.setTitle( "Scan QR code" );
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
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }

    @Override
    protected void setData( ) {
        super.setData( );
    }

    @Override
    public void onBarcodeDetect( String contents ) {
        //Todo BTC,ETH 정규식 적용
//        Pattern p = Pattern.compile( "0x[0-9a-fA-F]{40}$" );
//        Matcher m = p.matcher( contents );
//        String address;
//        if ( m.find( ) ) {
//            address = m.group( 0 );
//            PLog.e( "adderss : " + address );
//        }

        setResult( RESULT_OK, new Intent( ).putExtra( C.bundleKey.QRCODE, contents ) );
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
