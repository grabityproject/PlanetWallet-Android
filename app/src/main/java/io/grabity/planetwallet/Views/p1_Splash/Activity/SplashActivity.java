package io.grabity.planetwallet.Views.p1_Splash.Activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9IntegerConverter;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.math.ec.FixedPointCombMultiplier;
import org.spongycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeCertificationActivity;
import io.grabity.planetwallet.Views.p2_Pincode.Activity.PinCodeRegistrationActivity;
import io.grabity.planetwallet.Widgets.FontTextView;

import static android.support.v4.util.Preconditions.checkArgument;


public class SplashActivity extends PlanetWalletActivity implements Animator.AnimatorListener {

    private ViewMapper viewMapper;

    private ECDomainParameters CURVE;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );

//        setContentView( R.layout.item_test );

        viewMapper = new ViewMapper( );
        viewInit( );
        setData( );

//        SpannableString content = new SpannableString( viewMapper.textView.getText( ).toString( ) );
//        content.setSpan( new UnderlineSpan(  ),0,content.length(), 0 );
//        viewMapper.textView.setText( content );

//        Rect bounds = new Rect();
//        viewMapper.text1.getPaint().getTextBounds( viewMapper.text1.getText().toString() , 0 , viewMapper.text1.getText().length(),bounds );
//        PLog.e( "bounds1 : " + bounds.width() );
//        viewMapper.text2.getPaint().getTextBounds( viewMapper.text2.getText().toString() , 0 , viewMapper.text2.getText().length(),bounds );
//        PLog.e( "bounds2 : " + bounds.width() );
//        viewMapper.text3.getPaint().getTextBounds( viewMapper.text3.getText().toString() , 0 , viewMapper.text3.getText().length(),bounds );
//        PLog.e( "bounds3 : " + bounds.width() );
//        viewMapper.text4.getPaint().getTextBounds( viewMapper.text4.getText().toString() , 0 , viewMapper.text4.getText().length(),bounds );
//        PLog.e( "bounds4 : " + bounds.width() );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

//        new Post( this ).action( "http://test.planetwallet.io/planet/ETH", 0 ,0, "{\n" +
//                " \"address\" : \"92131af7b4c671131217b507ee12930f3bda6c58\",\n" +
//                " \"signature\" : \"485a2604d48d598402e032e28e55895416a6703c0c9e879d259403b3142c527923a71f57a708e2b55cbe798a50092656d7f253eff0b124597d4a4a68cafe354801\",\n" +
//                " \"planet\" : \"hello\"\n" +
//                "}" );

//        String prKey = "5f8a1143c1f7483494afa3e880eee15bf6a32d5e5c4e00789d5b377553ab8acb";
//        String prKey = "b28b4246bdc4d827fdadc61fef6c3ed93e88aa173c4114b3b9c7e3ffb4ad9293";
        String prKey = "00e0a79bb2a63a07c5d78d79741c35407686c9e1db129dd34078462e068d7beb";

//        sign( "ansrbthd", prKey );
//        sign( "hi", prKey );
//        sign( "hello", prKey );


        viewMapper.view.addAnimatorListener( this );

        viewMapper.view.setAnimation( !getCurrentTheme( ) ? "lottie/splash_black.json" : "lottie/splash_white.json" );
        viewMapper.view.setRepeatCount( 0 );
        viewMapper.view.playAnimation( );


    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        PLog.e( "error : " + error );
        PLog.e( "statusCode : " + statusCode );
        PLog.e( "result : " + result );
    }

    /**
     * =========================================
     */


    public String sign( String data, String privatekey ) {
        String message = sha256( data );

        X9ECParameters CURVE_PARAMS = SECNamedCurves.getByName( "secp256k1" );
        CURVE = new ECDomainParameters(
                CURVE_PARAMS.getCurve( ), CURVE_PARAMS.getG( ), CURVE_PARAMS.getN( ), CURVE_PARAMS.getH( ) );

        ECDSASigner signer = new ECDSASigner( new HMacDSAKCalculator( new SHA256Digest( ) ) );
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters( new BigInteger( privatekey, 16 ), CURVE );
        signer.init( true, privKey );
        BigInteger[] components = signer.generateSignature( Hex.decode( message ) );

        components[ 1 ] = components[ 1 ].compareTo( CURVE.getN( ).shiftRight( 1 ) ) > 0 ? CURVE.getN( ).subtract( components[ 1 ] ) : components[ 1 ];

        String r = bytesToHexString( bigIntegerToBytes( components[ 0 ], 32 ) );
        String s = bytesToHexString( bigIntegerToBytes( components[ 1 ], 32 ) );

        byte[] messageByte = bigIntegerToBytes( new BigInteger( message, 16 ), message.length( ) );

        int recId = -1;
        byte[] publickeyByte = publicKeyFromPrivate( new BigInteger( privatekey, 16 ), false );
        for ( int i = 0; i < 4; i++ ) {
            byte[] k = recoverPubBytesFromSignature( i, components, messageByte );
            if ( k != null && Arrays.equals( k, publickeyByte ) ) {
                recId = i;
                break;
            }
        }
        if ( recId == -1 ) {
            return "";
        }

        String v = String.format( "%02x", recId );
        PLog.e( "r : " + r );
        PLog.e( "s : " + s );
        PLog.e( "v : " + v );

        StringBuffer stringBuffer = new StringBuffer( );
        stringBuffer.append( r ).append( s ).append( v );

        PLog.e( "signature : " + stringBuffer.toString( ) );

        return stringBuffer.toString( );
    }

    //Todo 밖으로 빼서 정리
    public static byte[] bigIntegerToBytes( BigInteger b, int numBytes ) {
        byte[] src = b.toByteArray( );
        byte[] dest = new byte[ numBytes ];
        boolean isFirstByteOnlyForSign = src[ 0 ] == 0;
        int length = isFirstByteOnlyForSign ? src.length - 1 : src.length;
        int srcPos = isFirstByteOnlyForSign ? 1 : 0;
        int destPos = numBytes - length;
        System.arraycopy( src, srcPos, dest, destPos, length );
        return dest;
    }

    public static String bytesToHexString( byte[] bytes ) {
        StringBuilder sb = new StringBuilder( bytes.length * 2 );
        for ( byte b : bytes )
            sb.append( String.format( "%02x", b ) );
        return sb.toString( );
    }

    public byte[] publicKeyFromPrivate( BigInteger privKey, boolean compressed ) {
        ECPoint point = CURVE.getG( ).multiply( privKey );
        return point.getEncoded( compressed );
    }

    public byte[] recoverPubBytesFromSignature( int recId, BigInteger[] b, byte[] messageHash ) {
        BigInteger n = CURVE.getN( );
        BigInteger i = BigInteger.valueOf( ( long ) recId / 2L );
        BigInteger x = b[ 0 ].add( i.multiply( n ) );

        ECCurve.Fp curve = ( ECCurve.Fp ) CURVE.getCurve( );
        BigInteger prime = curve.getQ( );
        if ( x.compareTo( prime ) >= 0 ) {
            return null;
        } else {
            ECPoint R = decompressKey( x, ( recId & 1 ) == 1 );
            if ( !R.multiply( n ).isInfinity( ) ) {
                return null;
            } else {
                BigInteger e = new BigInteger( 1, messageHash );
                BigInteger eInv = BigInteger.ZERO.subtract( e ).mod( n );
                BigInteger rInv = b[ 0 ].modInverse( n );
                BigInteger srInv = rInv.multiply( b[ 1 ] ).mod( n );
                BigInteger eInvrInv = rInv.multiply( eInv ).mod( n );
                org.spongycastle.math.ec.ECPoint.Fp q = ( org.spongycastle.math.ec.ECPoint.Fp ) ECAlgorithms.sumOfTwoMultiplies( CURVE.getG( ), eInvrInv, R, srInv );
                return q.getEncoded( false );
            }
        }
    }

    private ECPoint decompressKey( BigInteger xBN, boolean yBit ) {
        X9IntegerConverter x9 = new X9IntegerConverter( );
        byte[] compEnc = x9.integerToBytes( xBN, 1 + x9.getByteLength( CURVE.getCurve( ) ) );
        compEnc[ 0 ] = ( byte ) ( yBit ? 3 : 2 );
        return CURVE.getCurve( ).decodePoint( compEnc );
    }

    public String sha256( String str ) {
        String SHA = "";
        try {
            MessageDigest sh = MessageDigest.getInstance( "SHA-256" );
            sh.update( str.getBytes( ) );
            byte byteData[] = sh.digest( );
            StringBuffer sb = new StringBuffer( );
            for ( int i = 0; i < byteData.length; i++ ) {
                sb.append( Integer.toString( ( byteData[ i ] & 0xff ) + 0x100, 16 ).substring( 1 ) );
            }
            SHA = sb.toString( );
        } catch ( NoSuchAlgorithmException e ) {
            e.printStackTrace( );
            SHA = null;
        }
        return SHA;
    }

    /**
     * =========================================
     */


    @Override
    protected void setData( ) {
        super.setData( );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
    }

    public void sendActionSwitch( ) {
        //Todo 쉐어드로 패스워드등록여부 체크 후 PinActivity 분기
        if ( Utils.getPreferenceData( this, C.pref.PASSWORD ).length( ) > 1 ) {
            new Handler( ).postDelayed( ( ) -> {
                sendAction( PinCodeCertificationActivity.class );
                finish( );
            }, 500 );
        } else {
            new Handler( ).postDelayed( ( ) -> {
                sendAction( PinCodeRegistrationActivity.class );
                finish( );
            }, 500 );
        }

    }

    @Override
    public void onAnimationStart( Animator animation ) {
    }

    @Override
    public void onAnimationEnd( Animator animation ) {
        sendActionSwitch( );
    }

    @Override
    public void onAnimationCancel( Animator animation ) {
    }

    @Override
    public void onAnimationRepeat( Animator animation ) {
    }

    public class ViewMapper {

        LottieAnimationView view;

        public ViewMapper( ) {
            view = findViewById( R.id.lottie_splash );

        }

    }


}