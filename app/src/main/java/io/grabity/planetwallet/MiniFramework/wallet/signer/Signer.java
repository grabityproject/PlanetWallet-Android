package io.grabity.planetwallet.MiniFramework.wallet.signer;

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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Signer {

    private static Signer instance;

    public static Signer getInstance( ) {
        if ( instance == null ) instance = new Signer( );
        return instance;
    }

    private ECDomainParameters CURVE;

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

        StringBuffer stringBuffer = new StringBuffer( );
        stringBuffer.append( r ).append( s ).append( v );
        return stringBuffer.toString( );
    }

    //Test 메소드
    private static String encodeChecked( int version, byte[] payload ) {
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

    private static byte[] hashTwice( byte[] input, int offset, int length ) {
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            digest.update( input, offset, length );
            return digest.digest( digest.digest( ) );
        } catch ( NoSuchAlgorithmException e ) {
        }
        return null;
    }

    private static String encode( byte[] input ) {
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

    public ECPoint publicPointFromPrivate( BigInteger privKey ) {
        /*
         * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
         * order, but that could change in future versions.
         */
        if ( privKey.bitLength( ) > CURVE.getN( ).bitLength( ) ) {
            privKey = privKey.mod( CURVE.getN( ) );
        }
        return new FixedPointCombMultiplier( ).multiply( CURVE.getG( ), privKey );
    }

    private static byte[] privateByteToEncode( byte[] keyBytes, boolean compressed ) {
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

    private static byte[] bigIntegerToBytes( BigInteger b, int numBytes ) {
        byte[] src = b.toByteArray( );
        byte[] dest = new byte[ numBytes ];
        boolean isFirstByteOnlyForSign = src[ 0 ] == 0;
        int length = isFirstByteOnlyForSign ? src.length - 1 : src.length;
        int srcPos = isFirstByteOnlyForSign ? 1 : 0;
        int destPos = numBytes - length;
        System.arraycopy( src, srcPos, dest, destPos, length );
        return dest;
    }

    private static String bytesToHexString( byte[] bytes ) {
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


    private static String sha256( String str ) {
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
}
