package io.grabity.planetwallet.Views.p1_Splash.Activity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.Preconditions;

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

    //Test 메소드
    public static String encodeChecked( int version, byte[] payload ) {
        if ( version < 0 || version > 255 )
            throw new IllegalArgumentException( "Version not in range." );

        // A stringified buffer is:
        // 1 byte version + data bytes + 4 bytes check code (a truncated hash)
        byte[] addressBytes = new byte[ 1 + payload.length + 4 ];
        addressBytes[ 0 ] = ( byte ) version;
        System.arraycopy( payload, 0, addressBytes, 1, payload.length );
        byte[] checksum = hashTwice( addressBytes, 0, payload.length + 1 );
        System.arraycopy( checksum, 0, addressBytes, payload.length + 1, 4 );
        return encode( addressBytes );
    }
    public static byte[] hashTwice(byte[] input, int offset, int length) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(input, offset, length);
            return digest.digest(digest.digest());
        }
        catch ( NoSuchAlgorithmException e ){}
        return null;
    }
    public static String encode( byte[] input ) {
          final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray( );
          final char ENCODED_ZERO = ALPHABET[ 0 ];
        if ( input.length == 0 ) {
            return "";
        }
        // Count leading zeros.
        int zeros = 0;
        while ( zeros < input.length && input[ zeros ] == 0 ) {
            ++zeros;
        }
        // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
        input = Arrays.copyOf( input, input.length ); // since we modify it in-place
        char[] encoded = new char[ input.length * 2 ]; // upper bound
        int outputStart = encoded.length;
        for ( int inputStart = zeros; inputStart < input.length; ) {
            encoded[ --outputStart ] = ALPHABET[ divmod( input, inputStart, 256, 58 ) ];
            if ( input[ inputStart ] == 0 ) {
                ++inputStart; // optimization - skip leading zeros
            }
        }
        // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
        while ( outputStart < encoded.length && encoded[ outputStart ] == ENCODED_ZERO ) {
            ++outputStart;
        }
        while ( --zeros >= 0 ) {
            encoded[ --outputStart ] = ENCODED_ZERO;
        }
        // Return encoded string (including encoded leading zeros).
        return new String( encoded, outputStart, encoded.length - outputStart );
    }
    private static byte divmod( byte[] number, int firstDigit, int base, int divisor ) {
        // this is just long division which accounts for the base of the input digits
        int remainder = 0;
        for ( int i = firstDigit; i < number.length; i++ ) {
            int digit = ( int ) number[ i ] & 0xFF;
            int temp = remainder * base + digit;
            number[ i ] = ( byte ) ( temp / divisor );
            remainder = temp % divisor;
        }
        return ( byte ) remainder;
    }

    public byte[] publicKeyFromPrivateCCC( BigInteger privateKey, boolean compressed ) {
        ECPoint point = publicPointFromPrivate( privateKey );
        return point.getEncoded( compressed );
    }
    public ECPoint publicPointFromPrivate(BigInteger privKey) {
        /*
         * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
         * order, but that could change in future versions.
         */
        if (privKey.bitLength() > CURVE.getN().bitLength()) {
            privKey = privKey.mod(CURVE.getN());
        }
        return new FixedPointCombMultiplier().multiply(CURVE.getG(), privKey);
    }
    public static byte[] privateByteToEncode( byte[] keyBytes, boolean compressed ) {
        if ( !compressed ) {
            return keyBytes;
        } else {
            // Keys that have compressed public components have an extra 1 byte on the end in dumped form.
            byte[] bytes = new byte[ 33 ];
            System.arraycopy( keyBytes, 0, bytes, 0, 32 );
            bytes[ 32 ] = 1;
            return bytes;
        }
    }

//        PLog.e( "pubkey : " + bytesToHexString( publickeyByte ) );
//        PLog.e( "pubkey.size : " + bytesToHexString( publickeyByte ).length() );
//        PLog.e( "pubkey 1 : " + bytesToHexString( publicKeyFromPrivateCCC( new BigInteger( privatekey, 16 ), true ) ) );
//        PLog.e( "pubkey 1.size : " + bytesToHexString( publicKeyFromPrivateCCC( new BigInteger( privatekey, 16 ), true ) ).length() );
//    PLog.e( "real prKey : " + encodeChecked( 239 , privateByteToEncode( bigIntegerToBytes( new BigInteger( privatekey , 16 ), 32 ), true ) ) );
    // 테스트넷 실제 prkey 확인
    //


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