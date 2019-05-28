package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

import io.grabity.planetwallet.R;

public class BarcodeView extends View {

    private PlanetView planetView;

    private float cornerRadius = 0.0f;
    private float borderWidth = 0.0f;
    private int borderColor = Color.TRANSPARENT;

    private float width;
    private float height;

    private String data;

    private MultiFormatWriter multiFormatWriter;
    private BarcodeEncoder barcodeEncoder;

    private Paint mainPaint;
    private Paint borderPaint;
    private Paint circlePaint;

    private RectF rectF;

    public BarcodeView( Context context ) {
        super( context );
    }

    public BarcodeView( Context context, @Nullable AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public BarcodeView( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );

        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.BarcodeView, defStyleAttr, 0 );

        cornerRadius = a.getDimensionPixelSize( R.styleable.BarcodeView_cornerRadius, 0 );
        borderWidth = a.getDimensionPixelSize( R.styleable.BarcodeView_borderWidth, 0 );
        borderColor = a.getColor( R.styleable.BarcodeView_borderColor, Color.TRANSPARENT );
        data = a.getString( R.styleable.BarcodeView_data );

        a.recycle( );
        viewInit( );
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        width = w;
        height = h;
        rectF = new RectF( 0 + borderWidth / 2.0f, 0 + borderWidth / 2.0f, w - borderWidth / 2.0f, h - borderWidth / 2.0f );
    }

    void viewInit( ) {
        multiFormatWriter = new MultiFormatWriter( );
        barcodeEncoder = new BarcodeEncoder( );
        borderPaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        borderPaint.setColor( borderColor );
        borderPaint.setStyle( Paint.Style.STROKE );
        borderPaint.setStrokeWidth( borderWidth );

        mainPaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        circlePaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        circlePaint.setColor( Color.WHITE );
        circlePaint.setStyle( Paint.Style.FILL );

        planetView = new PlanetView( getContext( ) );
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw( canvas );
        canvas.drawRoundRect( rectF, cornerRadius, cornerRadius, borderPaint );
        if ( getData( ) != null ) {
            Bitmap qrCodeBitmap = generateQRCode( getData( ) );
            canvas.drawBitmap( qrCodeBitmap, getPaddingLeft( ), getPaddingTop( ), mainPaint );
            if ( planetView != null ) {
                float radius = Math.max( width, height ) / 8.0f;
                circlePaint.setColor( Color.WHITE );
                canvas.drawCircle( width / 2.0f, height / 2.0f, radius, circlePaint );
                planetView.setData( data );
                circlePaint.setColor( Color.BLACK );
                canvas.drawCircle( width / 2.0f, height / 2.0f, ( radius ) * 5.0f / 6.0f, circlePaint );
                canvas.drawBitmap( planetView.getPlanetImage( ( radius * 2.0f ) * 5.0f / 6.0f, ( radius * 2.0f ) * 5.0f / 6.0f ),
                        width / 2.0f - ( radius * 5.0f / 6.0f ),
                        height / 2.0f - ( radius * 5.0f / 6.0f ),
                        mainPaint );
            }
        }
    }

    public String getData( ) {
        return data;
    }

    public void setData( String data ) {
        this.data = data;
        invalidate( );
    }

    private Bitmap generateQRCode( String data ) {
        Bitmap bitmap = null;
        try {
            Map< EncodeHintType, Object > hints = new HashMap<>( );
            hints.put( EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H );
            hints.put( EncodeHintType.MARGIN, 0 );
            BitMatrix bitMatrix = multiFormatWriter.encode( data,
                    BarcodeFormat.QR_CODE,
                    ( int ) ( width - getPaddingLeft( ) - getPaddingRight( ) ),
                    ( int ) ( height - getPaddingTop( ) - getPaddingBottom( ) ),
                    hints );
            bitmap = barcodeEncoder.createBitmap( bitMatrix );
        } catch ( WriterException e ) {
            e.printStackTrace( );
        }
        return bitmap;
    }

    public PlanetView getPlanetView( ) {
        return planetView;
    }

    public void setPlanetView( PlanetView planetView ) {
        this.planetView = planetView;
        if ( planetView != null ) {
            setData( planetView.getData( ) );
        }
    }

    public float getCornerRadius( ) {
        return cornerRadius;
    }

    public void setCornerRadius( float cornerRadius ) {
        this.cornerRadius = cornerRadius;
        rectF = new RectF( 0 + borderWidth / 2.0f, 0 + borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f );
        invalidate( );
    }

    public float getBorderWidth( ) {
        return borderWidth;
    }

    public void setBorderWidth( float borderWidth ) {
        this.borderWidth = borderWidth;
        rectF = new RectF( 0 + borderWidth / 2.0f, 0 + borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f );
        invalidate( );
    }

    public int getBorderColor( ) {
        return borderColor;
    }

    public void setBorderColor( int borderColor ) {
        this.borderColor = borderColor;
        rectF = new RectF( 0 + borderWidth / 2.0f, 0 + borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f );
        invalidate( );
    }
}
