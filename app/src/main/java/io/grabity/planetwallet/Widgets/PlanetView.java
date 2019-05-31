package io.grabity.planetwallet.Widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.grabity.planetwallet.R;

public class PlanetView extends View {

    private String data;

    private float mWidth;
    private float mHeight;

    private int[] patterns = {
            0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
    };

    private String[] colors = {
            "#FFFD00",
            "#FEB900",
            "#EFA288",
            "#EEA5B0",
            "#FF99D6",
            "#DD9278",
            "#FC8F79",
            "#E45641",
            "#E62D38",
            "#F94A62",
            "#EB526F",
            "#FF54B0",
            "#FA198C",
            "#C5147D",
            "#E1A9E8",
            "#9B5FE5",
            "#7C00C7",
            "#531CB3",
            "#CCFF66",
            "#00E291",
            "#35D1BF",
            "#028A81",
            "#0A3748",
            "#4F51B3",
            "#1D00FF",
            "#090080",
            "#0A104D",
            "#575756"
    };

    private byte[] hash;

    public PlanetView( Context context ) {
        super( context );
        setWillNotDraw( false );
    }

    public PlanetView( Context context, @Nullable AttributeSet attrs ) {
        this( context, attrs, 0 );
    }

    public PlanetView( Context context, AttributeSet attrs, int defStyleAttr ) {
        super( context, attrs, defStyleAttr );
        TypedArray a = context.obtainStyledAttributes( attrs, R.styleable.PlanetView, defStyleAttr, 0 );
        setData( a.getString( R.styleable.PlanetView_data ) );
        a.recycle( );
        setWillNotDraw( false );
    }

    public void setData( String data ) {
        if ( data == null ) data = "";
        this.data = data;
        this.hash = sha256( data );
        invalidate( );
    }

    public String getData( ) {
        return data;
    }

    public int getValueFromByte( byte input, int range ) {
        int inputInt = ( input & 0xFF );
        if ( range == 0 ) return 0;
        double percent = 256d / ( double ) range;
        for ( int i = 0; i < range; i++ ) {
            if ( ( ( double ) i * percent ) < inputInt && inputInt < ( ( double ) ( i + 1 ) * percent ) ) {
                return i;
            }
        }
        return 0;
    }

    public static byte[] sha256( String msg ) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance( "SHA-256" );
            md.update( msg.getBytes( ) );

            return md.digest( );
        } catch ( NoSuchAlgorithmException e ) {
            return null;
        }
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        int width = MeasureSpec.getSize( widthMeasureSpec );
        int height = MeasureSpec.getSize( widthMeasureSpec );
        setMeasuredDimension( width, height );
    }

    @Override
    protected void onSizeChanged( int w, int h, int oldw, int oldh ) {
        super.onSizeChanged( w, h, oldw, oldh );
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw( canvas );
        if ( mWidth != 0 && mHeight != 0 )
            drawCanvas( canvas, mWidth, mHeight );
    }

    void drawCanvas( Canvas canvas, float width, float height ) {
        Canvas patternCanvas;

        Bitmap original;
        Bitmap originalPattern;

        Canvas originalCanvas;
        Paint shaderPaint;

        original = Bitmap.createBitmap( ( int ) width, ( int ) height, Bitmap.Config.ARGB_8888 );
        originalCanvas = new Canvas( original );
        Shader shader = new BitmapShader( original, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP );
        shaderPaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        shaderPaint.setShader( shader );

        originalPattern = Bitmap.createBitmap( ( int ) width, ( int ) height, Bitmap.Config.ARGB_8888 );
        patternCanvas = new Canvas( originalPattern );
        Paint patternShaderPaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        patternShaderPaint.setShader( new BitmapShader( originalPattern, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP ) );

        if ( hash == null ) this.hash = sha256( "" );
        { // Main
            int pattern = patterns[ getValueFromByte( hash[ 0 ], patterns.length ) ];
            String colorCode = colors[ getValueFromByte( hash[ 1 ], colors.length ) ];
            drawMain( patternCanvas, width, height, pattern, colorCode );
        }

        { // Circle 1
            boolean visible = getValueFromByte( hash[ 8 ], 10 ) <= 9;
            if ( visible ) {
                double outlineRadius = 90.0d + getValueFromByte( hash[ 9 ], 40 ) * 0.5d;
                double degree = getValueFromByte( hash[ 10 ], 360 );
                double scale = 90.0d + getValueFromByte( hash[ 11 ], 40 ) * 0.5d;
                String colorCode = colors[ getValueFromByte( hash[ 12 ], colors.length ) ];
                drawCircle( patternCanvas, width, height, outlineRadius, degree, scale, colorCode );
            }
        }

        { // Circle 2
            boolean visible = getValueFromByte( hash[ 16 ], 10 ) <= 7;
            if ( visible ) {
                double outlineRadius = 90.0d + getValueFromByte( hash[ 17 ], 40 ) * 0.5d;
                double degree = getValueFromByte( hash[ 18 ], 360 );
                double scale = 90.0d + getValueFromByte( hash[ 19 ], 40 ) * 0.5d;
                String colorCode = colors[ getValueFromByte( hash[ 20 ], colors.length ) ];
                drawCircle( patternCanvas, width, height, outlineRadius, degree, scale, colorCode );
            }
        }

        { // Mask Circle
            boolean visible = getValueFromByte( hash[ 24 ], 10 ) <= 5;
            if ( visible ) {
                double outlineRadius = 30.0d + getValueFromByte( hash[ 25 ], 90 ) * 0.5d;
                double degree = getValueFromByte( hash[ 26 ], 360 );
                double scale = 60.0d + getValueFromByte( hash[ 27 ], 80 ) * 0.5d;
                drawMaskCircle( originalCanvas, originalPattern, width, height, outlineRadius, degree, scale );
            } else {
                drawMaskCircle( originalCanvas, originalPattern, width, height, 0, 0, 0 );
            }
        }

        { // Circle Clip
            canvas.drawCircle( width / 2.0f, height / 2.0f, width / 2.0f, shaderPaint );
        }
    }

    void drawMaskCircle( Canvas canvas, Bitmap originalPattern, float width, float height, double outlineRadius, double degree, double scale ) {
        Bitmap bitmap = Bitmap.createBitmap( ( int ) width, ( int ) height, Bitmap.Config.ARGB_8888 );
        Canvas c = new Canvas( bitmap );
        Shader shader = new BitmapShader( originalPattern, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP );
        Paint shaderPaint = new Paint( Paint.ANTI_ALIAS_FLAG );
        shaderPaint.setShader( shader );
        {
            Path path = new Path( );
            path.moveTo( 0, 0 );
            path.lineTo( width, 0 );
            path.lineTo( width, height );
            path.lineTo( 0, height );
            path.close( );

            path.addCircle( width / 2.0f + ( float ) ( outlineRadius / 100.0f * ( width / 2.0f ) * Math.cos( degree * Math.PI / 180.0f ) ),
                    height / 2.0f + ( float ) ( outlineRadius / 100.0f * ( width / 2.0f ) * Math.sin( degree * Math.PI / 180.0f ) ),
                    ( float ) scale / 100.0f * ( width / 2.0f ), Path.Direction.CCW );
            c.drawPath( path, shaderPaint );
        }
        canvas.drawBitmap( bitmap, 0, 0, shaderPaint );
    }

    void drawCircle( Canvas canvas, float width, float height, double outlineRadius, double degree, double scale, String colorCode ) {
        Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        paint.setStyle( Paint.Style.FILL );
        paint.setColor( Color.parseColor( colorCode ) );
        canvas.drawCircle(
                width / 2.0f + ( float ) ( outlineRadius / 100.0f * ( width / 2.0f ) * Math.cos( degree * Math.PI / 180.0f ) ),
                width / 2.0f + ( float ) ( outlineRadius / 100.0f * ( width / 2.0f ) * Math.sin( degree * Math.PI / 180.0f ) ),
                ( float ) scale / 100.0f * ( width / 2.0f ), paint );
    }

    void drawMain( Canvas canvas, float width, float height, int pattern, String colorCode ) {
        if ( pattern == 0 ) {
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setColor( Color.parseColor( colorCode ) );
            canvas.drawRect( 0, 0, width, height, paint );
        }

        if ( pattern == 1 ) { // Pattern 1 ( Wave )
            float waveWidth = width / 16;
            float waveHeight = height / 42;
            int waveCount = ( int ) Math.ceil( width / waveWidth );
            int pathCount = ( int ) Math.ceil( height / waveHeight );
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( height / 42 );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int j = 0; j < pathCount + 1; j++ ) {
                float pointY = j * waveHeight * 2;
                Path path = new Path( );
                path.moveTo( -waveWidth, pointY + waveHeight );
                for ( int i = 0; i < waveCount + 1; i++ ) {
                    path.quadTo( ( i * waveWidth * 4 ) + waveWidth * 0, pointY - waveHeight + waveHeight / 2.0f, ( i * waveWidth * 4 ) + waveWidth * 1, pointY + waveHeight / 2.0f );
                    path.quadTo( ( i * waveWidth * 4 ) + waveWidth * 2, pointY + waveHeight + waveHeight / 2.0f, ( i * waveWidth * 4 ) + waveWidth * 3, pointY + waveHeight / 2.0f );
                }
                canvas.drawPath( path, paint );
            }
        }

        if ( pattern == 2 ) { // Pattern 2 ( Diamond )
            float pathWidth = ( float ) Math.sqrt( Math.pow( width, 2 ) + Math.pow( height, 2 ) ) / 42.0f;
            float centerX = width / 2.0f;
            float centerY = height / 2.0f;

            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.FILL );
            paint.setStrokeWidth( pathWidth );
            paint.setColor( Color.BLACK );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int i = 0; i < 14; i++ ) {
                int multi = i == 0 ? 1 : ( i * 3 );
                Path path = new Path( );
                path.moveTo( centerX, centerY - pathWidth * multi );
                path.lineTo( centerX + pathWidth * multi, centerY );
                path.lineTo( centerX, centerY + pathWidth * multi );
                path.lineTo( centerX - pathWidth * multi, centerY );
                path.close( );
                canvas.drawPath( path, paint );
                paint.setStyle( Paint.Style.STROKE );
            }

        }


        if ( pattern == 3 ) {   // Pattern 3 ( Stripe )
            float pathWidth = width / 56;
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( pathWidth * 2 );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int i = 0; i < 28; i++ ) {
                Path path = new Path( );
                path.moveTo( pathWidth * ( i * 4 + 1 ), 0 );
                path.lineTo( pathWidth * ( i * 4 + 1 ), height );
                path.close( );
                canvas.drawPath( path, paint );
            }

        }

        if ( pattern == 4 ) {   // Pattern 4 ( Stripe twist )
            float pathWidth = width / 56;
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( pathWidth * 2 );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int i = 0; i < 28; i++ ) {
                Path path = new Path( );
                path.moveTo( pathWidth * ( i * 4 + 1 ), 0 );
                path.lineTo( pathWidth * ( i * 4 + 1 ), height / 2.0f );
                path.close( );

                path.moveTo( pathWidth * ( i * 4 + 3 ), height / 2.0f );
                path.lineTo( pathWidth * ( i * 4 + 3 ), height );
                path.close( );

                canvas.drawPath( path, paint );
            }

        }

        if ( pattern == 5 ) {  // Pattern 5 ( diagonal )
            float pathWidth = ( float ) Math.sqrt( Math.pow( width, 2 ) + Math.pow( height, 2 ) ) / 42.0f;
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( pathWidth );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int i = 0; i < 24; i++ ) {
                Path path = new Path( );
                path.moveTo( width + pathWidth - ( ( i - 12 ) * 3 ) * pathWidth, -pathWidth );
                path.lineTo( 0 - pathWidth - ( ( i - 12 ) * 3 ) * pathWidth, height + pathWidth );
                canvas.drawPath( path, paint );
            }

        }

        if ( pattern == 6 ) { // Pattern 6 ( Check )
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.parseColor( colorCode ) );

            float boxWidth = width / 24.0f;
            for ( int j = 0; j < 12; j++ ) {
                for ( int i = 0; i < 12; i++ ) {
                    canvas.drawRect( boxWidth * ( i * 2 ), boxWidth * ( j * 2 ), boxWidth * ( i * 2 + 1 ), boxWidth * ( j * 2 + 1 ), paint );
                    canvas.drawRect( boxWidth * ( i * 2 + 1 ), boxWidth * ( j * 2 + 1 ), boxWidth * ( i * 2 + 2 ), boxWidth * ( j * 2 + 2 ), paint );
                }
            }
        }


        if ( pattern == 7 ) { // Pattern 7 ( Check )
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.parseColor( colorCode ) );

            float patternWidth = width / 32.0f;
            float patternHeight = height / 8.0f;
            float patternDiagonal = ( float ) Math.sqrt( Math.pow( patternWidth, 2 ) );


            for ( int j = 0; j < 5; j++ ) {
                for ( int i = 0; i < 16; i++ ) {
                    Path path = new Path( );
                    path.moveTo( patternWidth * ( i * 2 ), patternHeight * ( j * 2 ) + -patternHeight / 2.0f );
                    path.lineTo( patternWidth * ( i * 2 + 1 ), patternHeight * ( j * 2 ) - patternHeight / 2.0f + patternDiagonal );
                    path.lineTo( patternWidth * ( i * 2 + 1 ), patternHeight * ( j * 2 ) - patternHeight / 2.0f + patternHeight + patternDiagonal );
                    path.lineTo( patternWidth * ( i * 2 ), patternHeight * ( j * 2 ) - patternHeight / 2.0f + patternHeight );
                    path.close( );

                    path.moveTo( patternWidth * ( i * 2 + 1 ), patternHeight * ( j * 2 ) - patternHeight / 2.0f + patternHeight + patternDiagonal );
                    path.lineTo( patternWidth * ( i * 2 + 2 ), patternHeight * ( j * 2 ) - patternHeight / 2.0f + patternHeight );
                    path.lineTo( patternWidth * ( i * 2 + 2 ), patternHeight * ( j * 2 ) - patternHeight / 2.0f + patternHeight + patternHeight );
                    path.lineTo( patternWidth * ( i * 2 + 1 ), patternHeight * ( j * 2 ) - patternHeight / 2.0f + patternHeight + patternHeight + patternDiagonal );
                    path.close( );

                    canvas.drawPath( path, paint );
                }
            }
        }


        if ( pattern == 8 ) { // Pattern 8 ( grill )
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.parseColor( colorCode ) );

            float pathWidth = ( width / 16.0f ) * 3.0f / 5.0f;
            float gapWidth = ( width / 16.0f ) * 2.0f / 5.0f;

            for ( int j = 0; j < 18; j++ ) {
                for ( int i = 0; i < 18; i++ ) {
                    Path path = new Path( );
                    path.moveTo( ( gapWidth * ( i + 1 ) ) + ( pathWidth * i ) + ( ( pathWidth / 2.0f ) * 1 ) - gapWidth / 2.0f, +( ( pathWidth / 2.0f ) * -1 ) + ( gapWidth + pathWidth ) * j );
                    path.lineTo( ( gapWidth * ( i + 1 ) ) + ( pathWidth * i ) + ( ( pathWidth / 2.0f ) * 2 ) - gapWidth / 2.0f, +( ( pathWidth / 2.0f ) * 0 ) + ( gapWidth + pathWidth ) * j );
                    path.lineTo( ( gapWidth * ( i + 1 ) ) + ( pathWidth * i ) + ( ( pathWidth / 2.0f ) * 1 ) - gapWidth / 2.0f, +( ( pathWidth / 2.0f ) * 1 ) + ( gapWidth + pathWidth ) * j );
                    path.lineTo( ( gapWidth * ( i + 1 ) ) + ( pathWidth * i ) + ( ( pathWidth / 2.0f ) * 0 ) - gapWidth / 2.0f, +( ( pathWidth / 2.0f ) * 0 ) + ( gapWidth + pathWidth ) * j );
                    path.close( );

                    path.moveTo( ( gapWidth * ( i + 1 ) ) + ( pathWidth * i ) + ( ( pathWidth / 2.0f ) * 1 ) - gapWidth - pathWidth / 2.0f, ( gapWidth / 2.0f + pathWidth / 2.0f ) + ( ( pathWidth / 2.0f ) * -1 ) + ( gapWidth + pathWidth ) * j );
                    path.lineTo( ( gapWidth * ( i + 1 ) ) + ( pathWidth * i ) + ( ( pathWidth / 2.0f ) * 2 ) - gapWidth - pathWidth / 2.0f, ( gapWidth / 2.0f + pathWidth / 2.0f ) + ( ( pathWidth / 2.0f ) * 0 ) + ( gapWidth + pathWidth ) * j );
                    path.lineTo( ( gapWidth * ( i + 1 ) ) + ( pathWidth * i ) + ( ( pathWidth / 2.0f ) * 1 ) - gapWidth - pathWidth / 2.0f, ( gapWidth / 2.0f + pathWidth / 2.0f ) + ( ( pathWidth / 2.0f ) * 1 ) + ( gapWidth + pathWidth ) * j );
                    path.lineTo( ( gapWidth * ( i + 1 ) ) + ( pathWidth * i ) + ( ( pathWidth / 2.0f ) * 0 ) - gapWidth - pathWidth / 2.0f, ( gapWidth / 2.0f + pathWidth / 2.0f ) + ( ( pathWidth / 2.0f ) * 0 ) + ( gapWidth + pathWidth ) * j );
                    path.close( );

                    canvas.drawPath( path, paint );
                }
            }
        }


        if ( pattern == 9 ) { // Pattern 9 ( half )
            float pathWidth = ( float ) Math.sqrt( Math.pow( width, 2 ) + Math.pow( height, 2 ) ) / 42.0f;
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( pathWidth );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int i = 0; i < 13; i++ ) {
                Path path = new Path( );
                path.moveTo( width + pathWidth + ( ( i - 12 ) * 3 ) * pathWidth - pathWidth * 2, -pathWidth );
                path.lineTo( 0 - pathWidth + ( ( i - 12 ) * 3 ) * pathWidth - pathWidth * 2, height + pathWidth );
                canvas.drawPath( path, paint );
            }

            Path path = new Path( );
            path.moveTo( width, 0 );
            path.lineTo( width, height );
            path.lineTo( 0, height );
            path.close( );
            paint.setStyle( Paint.Style.FILL );
            canvas.drawPath( path, paint );
        }


        if ( pattern == 10 ) { // Pattern 10 ( Zigzag )
            float patternWidth = width / 16.0f;
            float patternHeight = height / 16.0f;
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int i = 0; i < 8; i++ ) {
                Path path = new Path( );

                path.moveTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 0 + patternHeight * 0 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 0 + patternHeight * 0 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 0 + patternHeight * 1 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 0 + patternHeight * 2 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 0 + patternHeight * 3 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 0 + patternHeight * 4 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 0 + patternHeight * 4 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 0 + patternHeight * 3 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 0 + patternHeight * 2 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 0 + patternHeight * 1 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 0 + patternHeight * 0 );
                path.close( );

                path.moveTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 1 + patternHeight * 0 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 1 + patternHeight * 0 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 1 + patternHeight * 1 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 1 + patternHeight * 2 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 1 + patternHeight * 3 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 1 + patternHeight * 4 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 1 + patternHeight * 4 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 1 + patternHeight * 3 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 1 + patternHeight * 2 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 1 + patternHeight * 1 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 1 + patternHeight * 0 );
                path.close( );

                path.moveTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 2 + patternHeight * 0 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 2 + patternHeight * 0 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 2 + patternHeight * 1 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 2 + patternHeight * 2 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 2 + patternHeight * 3 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 2 + patternHeight * 4 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 2 + patternHeight * 4 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 2 + patternHeight * 3 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 2 + patternHeight * 2 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 2 + patternHeight * 1 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 2 + patternHeight * 0 );
                path.close( );

                path.moveTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 3 + patternHeight * 0 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 3 + patternHeight * 0 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 3 + patternHeight * 1 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 3 + patternHeight * 2 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 2, ( patternHeight * 4 ) * 3 + patternHeight * 3 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 3 + patternHeight * 4 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 3 + patternHeight * 4 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 3 + patternHeight * 3 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 3 + patternHeight * 2 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 1, ( patternHeight * 4 ) * 3 + patternHeight * 1 );
                path.lineTo( ( patternWidth * 2 * i ) + patternWidth * 0, ( patternHeight * 4 ) * 3 + patternHeight * 0 );
                path.close( );

                canvas.drawPath( path, paint );
            }

        }


        if ( pattern == 11 ) { // Pattern 11 ( triangle )
            float patternWidth = width / 12.0f;
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.FILL );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int j = 0; j < 12; j++ ) {
                for ( int i = 0; i < 12; i++ ) {
                    Path path = new Path( );
                    path.moveTo( patternWidth * i, patternWidth * j );
                    path.lineTo( patternWidth * ( i + 1 ), patternWidth * j );
                    path.lineTo( patternWidth * i, patternWidth * ( j + 1 ) );
                    path.close( );
                    canvas.drawPath( path, paint );
                }
            }


        }


        if ( pattern == 12 ) {   // Pattern 3 ( Stripe )
            float pathWidth = width / 56;
            Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
            paint.setStyle( Paint.Style.STROKE );
            paint.setStrokeWidth( pathWidth * 2 );
            paint.setColor( Color.parseColor( colorCode ) );

            for ( int i = 0; i < 28; i++ ) {
                Path path = new Path( );
                path.moveTo( 0, pathWidth * ( i * 4 + 1 ) );
                path.lineTo( width, pathWidth * ( i * 4 + 1 ) );
                path.close( );
                canvas.drawPath( path, paint );
            }

        }
    }

    public Bitmap getPlanetImage( float width, float height ) {
        Bitmap bitmap = Bitmap.createBitmap( ( int ) width, ( int ) height, Bitmap.Config.ARGB_8888 );
        Canvas canvas = new Canvas( bitmap );
        drawCanvas( canvas, width, height );
        return bitmap;
    }
}
