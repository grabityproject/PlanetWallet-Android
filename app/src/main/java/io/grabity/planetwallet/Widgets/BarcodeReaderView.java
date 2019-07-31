package io.grabity.planetwallet.Widgets;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import io.grabity.planetwallet.MiniFramework.utils.PLog;

public class BarcodeReaderView extends SurfaceView implements Detector.Processor< Barcode >, SurfaceHolder.Callback {

    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private OnBarcodeDetectListener onBarcodeDetectListener;


    public BarcodeReaderView( Context context ) {
        super( context );
        init( );
    }

    public BarcodeReaderView( Context context, AttributeSet attrs ) {
        super( context, attrs );
        init( );
    }


    void init( ) {

        barcodeDetector = new BarcodeDetector.Builder( getContext( ) )
                .build( );
        barcodeDetector.setProcessor( this );
    }

    @Override
    public void surfaceCreated( SurfaceHolder surfaceHolder ) {

    }

    @Override
    public void surfaceChanged( SurfaceHolder surfaceHolder, int i, int i1, int i2 ) {
        PLog.e( "surfaceChanged : " + cameraSource );
        try {
            if ( ActivityCompat.checkSelfPermission( getContext( ), Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                return;
            }
            cameraSource.start( surfaceHolder );
        } catch ( IOException e ) {
            e.printStackTrace( );
        }
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder surfaceHolder ) {
        cameraSource.stop( );    // SurfaceView가 종료되었을 때, Mobile Vision API 종료
    }

    public void barCodeDetectorStop( ) {
        barcodeDetector.release( );
    }


    public void resourceRelease( ) {
        this.post( ( ) -> {
            cameraSource.release( ); //카메라 해제
            barcodeDetector.release( ); // 바코드인식기능해제
        } );
    }


    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );

        if ( cameraSource != null ) {
            cameraSource.release( );
            cameraSource.stop( );
        }
        cameraSource = new CameraSource
                .Builder( getContext( ), barcodeDetector )
                .setFacing( CameraSource.CAMERA_FACING_BACK )
                .setRequestedPreviewSize( h, w )
                .setAutoFocusEnabled( true )  // AutoFocus를 안하면 초점을 못 잡아서 화질이 많이 흐립니다.
                .build( );


        getHolder( ).removeCallback( this );
        getHolder( ).addCallback( this );

    }

    @Override
    public void release( ) {
        Log.e( "NowStatus", "BarcodeDetector SetProcessor Released" );
    }

    @Override
    public void receiveDetections( Detector.Detections< Barcode > detections ) {
        final SparseArray< Barcode > barcodes = detections.getDetectedItems( );
        if ( barcodes.size( ) != 0 ) {
            String barcodeContents = barcodes.valueAt( 0 ).displayValue; // 바코드 인식 결과물
            Log.e( "Detection", barcodeContents );
            if ( onBarcodeDetectListener != null ) {
                onBarcodeDetectListener.onBarcodeDetect( barcodeContents );
            }
        }
    }


    public OnBarcodeDetectListener getOnBarcodeDetectListener( ) {
        return onBarcodeDetectListener;
    }

    public void setOnBarcodeDetectListener( OnBarcodeDetectListener onBarcodeDetectListener ) {
        this.onBarcodeDetectListener = onBarcodeDetectListener;
    }


    public interface OnBarcodeDetectListener {
        void onBarcodeDetect( String contents );
    }
}
