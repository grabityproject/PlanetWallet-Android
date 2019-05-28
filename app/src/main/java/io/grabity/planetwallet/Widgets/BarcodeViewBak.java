package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.Map;

import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;

/**
 * Created by. JeongHyun 2019. 05. 24
 */

public class BarcodeViewBak extends RelativeLayout {
    private Context context;
    private Paint paintBorder;

    private float border_width;
    private int border_color;

    float width;
    float height;
    float radius;

    private String data;
    private PlanetView planetView;
    private StretchImageView stretchImageView;
    private CircleImageView circleImageView;
    private MultiFormatWriter multiFormatWriter;
    private BitMatrix bitMatrix;
    private BarcodeEncoder barcodeEncoder;
    private Bitmap bitmap;
    private int barcodeSize = 200;

    public BarcodeViewBak( Context context ) {
        super( context );
        this.context = context;

        border_width = Utils.dpToPx( getContext( ), 1 );
        border_color = Color.parseColor( "#EDEDED" );
        data = "";

        this.viewInit( );
    }

    public BarcodeViewBak( Context context, AttributeSet attrs ) {
        this( context, attrs, 0 );
        this.viewInit( );
    }

    public BarcodeViewBak( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.BarcodeViewBak, defStyleAttr, 0 );
        border_width = a.getDimensionPixelSize( R.styleable.BarcodeViewBak_borderWidth, ( int ) Utils.dpToPx( getContext( ), 1 ) );
        border_color = a.getColor( R.styleable.BarcodeViewBak_borderColor, Color.parseColor( "#EDEDED" ) );
        data = a.getString( R.styleable.BarcodeViewBak_addr );
        a.recycle( );
        this.viewInit( );
    }

    private void viewInit( ) {
        paintBorder = new Paint( Paint.ANTI_ALIAS_FLAG );
        paintBorder.setColor( border_color );
        paintBorder.setStrokeWidth( border_width );
        paintBorder.setStyle( Paint.Style.STROKE );

        {
            stretchImageView = new StretchImageView( getContext( ) );
            RelativeLayout.LayoutParams params = new LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
            params.setMargins( ( int ) Utils.dpToPx( getContext( ), 2 ), ( int ) Utils.dpToPx( getContext( ), 2 ), ( int ) Utils.dpToPx( getContext( ), 2 ), ( int ) Utils.dpToPx( getContext( ), 2 ) );
            addView( stretchImageView, params );
        }

        {
            circleImageView = new CircleImageView( getContext( ) );
            RelativeLayout.LayoutParams params = new LayoutParams( ( int ) Utils.dpToPx( getContext( ), 36 ), ( int ) Utils.dpToPx( getContext( ), 36 ) );
            params.addRule( CENTER_IN_PARENT );
            circleImageView.setImageDrawable( new ColorDrawable( Color.WHITE ) );
            addView( circleImageView, params );
        }

        {
            planetView = new PlanetView( getContext( ) );
            RelativeLayout.LayoutParams params = new LayoutParams( ( int ) Utils.dpToPx( getContext( ), 26 ), ( int ) Utils.dpToPx( getContext( ), 26 ) );
            params.addRule( CENTER_IN_PARENT );
            addView( planetView, params );
            planetView.setData( "가즈아" );
        }

        multiFormatWriter = new MultiFormatWriter( );
        barcodeEncoder = new BarcodeEncoder( );
        super.setBackgroundColor( Color.WHITE );
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        width = w;
        height = h;
        setMeasuredDimension( MeasureSpec.getSize( ( int ) width ), MeasureSpec.getSize( ( int ) height ) );
    }

    @Override
    protected void dispatchDraw( Canvas canvas ) {
        canvas.drawRoundRect( new RectF( border_width / 2.0f, border_width / 2.0f, width - border_width / 2.0f, height - border_width / 2.0f ), radius, radius, paintBorder );
        super.dispatchDraw( canvas );
    }

    public String getData( ) {
        return data;
    }

    public void setData( String data ) {
        this.data = data;

        if ( this.data == null ) {
            this.data = "sample";
        }
        planetView.setData( this.data );

        if ( bitMatrix != null ) bitMatrix.clear( );
        if ( bitmap != null ) bitmap.recycle( );

        try {

            Map< EncodeHintType, ErrorCorrectionLevel > hints = new HashMap<>( );
            hints.put( EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H );
            bitMatrix = multiFormatWriter.encode( this.data, BarcodeFormat.QR_CODE, barcodeSize, barcodeSize, hints );
            bitmap = barcodeEncoder.createBitmap( bitMatrix );
            stretchImageView.setImageBitmap( bitmap );

        } catch ( WriterException e ) {
            e.printStackTrace( );
        }


    }

    public int getBorder_color( ) {
        return border_color;
    }

    public void setBorder_color( int border_color ) {
        this.border_color = border_color;
        invalidate( );
    }

    public float getBorder_width( ) {
        return border_width;
    }

    public void setBorder_width( float border_width ) {
        this.border_width = border_width;
    }


}
