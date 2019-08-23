package io.grabity.planetwallet.MiniFramework.wallet.transaction;

import com.pentasecurity.cryptowallet.JniWrapper;
import com.pentasecurity.cryptowallet.utils.PcwfUtils;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Base58.Base58;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.VO.ReturnVO;

public class BtcRawTx {


    public static String generateRawTx( Transaction tx, String privateKey ) {

        byte[] publicKey = JniWrapper.GenPubkeyFromPrikey( PcwfUtils.hexStringToByteArray( privateKey ) );

        long feePerByte = 10;

        try {
            feePerByte = new BigDecimal( tx.getGasPrice( ) ).longValue( );
        } catch ( ClassCastException | NumberFormatException | NullPointerException e ) {
            // Do not disturb;
        }

        // Getting utxo  list
        ArrayList< UTXO > utxos = getUtxoList( tx );
        if ( utxos == null ) return null; // Error, getting utxo List;
        if ( utxos.size( ) == 0 ) return null; // Have not utxo list;

        // input coin selection
        ArrayList< UTXO > inputs = selection( tx.getAmount( ), feePerByte, utxos );
        if ( inputs.size( ) == 0 ) return null; // no input || balance is less then amount

        // total balance from inputs & estimate Fee
        BigDecimal amount = new BigDecimal( tx.getAmount( ) );
        BigDecimal inputTotal = new BigDecimal( "0" );
        BigDecimal estimateFee = new BigDecimal( ( inputs.size( ) * 148 + 2 * 34 + 10 + 2 ) ).multiply( new BigDecimal( feePerByte ) );
        for ( UTXO input : inputs )
            inputTotal = inputTotal.add( new BigDecimal( input.getValue( ) ) );

        BigDecimal change = inputTotal.subtract( estimateFee ).subtract( amount );

        // Fee SetUp
        tx.fee = estimateFee.toBigInteger( ).toString( 10 );

        // Make outputs
        ArrayList< UTXO > outputs = new ArrayList<>( );

        UTXO outputTo = new UTXO( );
        outputTo.setScript( scriptPubKey( tx.getToAddress( ) ) );
        outputTo.setValue( tx.getAmount( ) );

        UTXO outputChange = new UTXO( );
        outputChange.setScript( scriptPubKey( tx.getFromAddress( ) ) );
        outputChange.setValue( change.toBigInteger( ).toString( 10 ) );

        outputs.add( outputTo );
        outputs.add( outputChange );

        // Signing
        for ( int i = 0; i < inputs.size( ); i++ ) {
            String unsignedScriptSig = unsignedScriptSig( inputs, outputs, i );
            String der = signer( unsignedScriptSig, privateKey );
            String pubKey = Utils.byteArrayToHexString( publicKey );
            inputs.get( i ).setSignedScript( Utils.decimalToHex( ( der.length( ) / 2 ) + 1 ) + der + decimalToHexString( 1 ) + decimalToHexString( publicKey.length ) + pubKey );
        }

        return signedTx( inputs, outputs );
    }


    private static String unsignedScriptSig( ArrayList< UTXO > inputs, ArrayList< UTXO > outputs, int inputIndex ) {
        StringBuilder builder = new StringBuilder( );

        String version = reverseHexString( paddingZeroLeft( decimalToHexString( 1 ), 8 ) );
        String inputCount = decimalToHexString( inputs.size( ) );
        String inputScript = inputScript( inputs, inputIndex );
        String outputCount = decimalToHexString( outputs.size( ) );
        String outputScript = outputScript( outputs );
        String lockTime = paddingZero( 8 );
        String sigHashCode = reverseHexString( paddingZeroLeft( decimalToHexString( 1 ), 8 ) );

        builder
                .append( version )
                .append( inputCount )
                .append( inputScript )
                .append( outputCount )
                .append( outputScript )
                .append( lockTime )
                .append( sigHashCode );

        return Utils.byteArrayToHexString( Utils.sha256Binary( Utils.sha256Binary( Utils.hexStringToByteArray( builder.toString( ) ) ) ) );
    }

    public static String signedTx( ArrayList< UTXO > inputs, ArrayList< UTXO > outputs ) {
        StringBuilder builder = new StringBuilder( );

        String version = reverseHexString( paddingZeroLeft( decimalToHexString( 1 ), 8 ) );
        String inputCount = decimalToHexString( inputs.size( ) );
        String inputScript = inputScriptSig( inputs );
        String outputCount = decimalToHexString( outputs.size( ) );
        String outputScript = outputScript( outputs );
        String lockTime = paddingZero( 8 );

        builder
                .append( version )
                .append( inputCount )
                .append( inputScript )
                .append( outputCount )
                .append( outputScript )
                .append( lockTime );

        return builder.toString( );
    }


    private static ArrayList< UTXO > selection( String amount, long fee, ArrayList< UTXO > utxos ) {
        ArrayList< UTXO > inputs = new ArrayList<>( );
        BigDecimal value = new BigDecimal( amount );
        BigDecimal total = new BigDecimal( 0 );

        for ( int i = 0; i < utxos.size( ); i++ ) {
            total = total.add( new BigDecimal( utxos.get( i ).getValue( ) ) );
            inputs.add( utxos.get( i ) );
            if ( total.compareTo( value.add( new BigDecimal( fee * ( ( i + 1 ) * 148 + 2 * 34 + 10 + i + 1 ) ) ) ) > 0 ) {
                break;
            }
        }

        return inputs;
    }

    private static Comparator< UTXO > heightSort = ( o1, o2 ) -> {

        if ( o1.getBlock_height( ) < o2.getBlock_height( ) ) {
            return -1;
        } else if ( o1.getBlock_height( ) > o2.getBlock_height( ) ) {
            return 1;
        } else {
            if ( o1.getTx_output_n( ) < o2.getTx_output_n( ) ) {
                return -1;
            } else if ( o1.getTx_output_n( ) > o2.getTx_output_n( ) ) {
                return 1;
            } else {
                return new BigDecimal( o2.getValue( ) ).compareTo( new BigDecimal( o1.getValue( ) ) );
            }
        }
    };

    private static ArrayList< UTXO > getUtxoList( Transaction tx ) {
        try {
            String[] nonceResult = new Get( null ).setDeviceKey( tx.getDeviceKey( ) ).execute( Route.URL( "utxo", "list", "BTC", tx.getFromAddress( ) ), 0, 0, null ).get( );
            if ( nonceResult.length == 3 && Utils.equals( nonceResult[ 0 ], String.valueOf( 200 ) ) ) {
                ReturnVO returnVO = Utils.jsonToVO( nonceResult[ 1 ], ReturnVO.class, UTXO.class );
                if ( returnVO.isSuccess( ) ) {
                    ArrayList< UTXO > utxos = ( ArrayList< UTXO > ) returnVO.getResult( );
                    Collections.sort( utxos, heightSort );
                    return utxos;
                }
            }
        } catch ( ExecutionException | InterruptedException e ) {
            e.printStackTrace( );
            PLog.e( "error, getting utxo list" );
        }
        return null;
    }

    private static String outputScript( ArrayList< UTXO > outputs ) {
        StringBuilder outputScript = new StringBuilder( );

        for ( int i = 0; i < outputs.size( ); i++ ) {

            String value = reverseHexString( paddingZeroLeft( decimalStringToHexString( outputs.get( i ).getValue( ) ), 16 ) );
            String script = outputs.get( i ).getScript( );
            String scriptLength = decimalToHexString( ( script.length( ) / 2 ) );

            outputScript
                    .append( value )
                    .append( scriptLength )
                    .append( script );
        }

        return outputScript.toString( );
    }

    private static String inputScript( ArrayList< UTXO > inputs, int inputIndex ) {
        StringBuilder inputScript = new StringBuilder( );
        for ( int i = 0; i < inputs.size( ); i++ ) {

            String prevHashReverse = reverseHexString( inputs.get( i ).getTx_hash( ) );
            String outputIndex = reverseHexString( paddingZeroLeft( decimalToHexString( inputs.get( i ).getTx_output_n( ).intValue( ) ), 8 ) );

            String script = i == inputIndex ? inputs.get( i ).getScript( ) : "";
            String scriptLength = decimalToHexString( ( script.length( ) / 2 ) );
            String sequence = paddingLeft( "", "f", 8 );

            inputScript
                    .append( prevHashReverse )
                    .append( outputIndex )
                    .append( scriptLength )
                    .append( script )
                    .append( sequence );
        }

        return inputScript.toString( );
    }

    private static String inputScriptSig( ArrayList< UTXO > inputs ) {
        StringBuilder inputScript = new StringBuilder( );
        for ( int i = 0; i < inputs.size( ); i++ ) {

            String prevHashReverse = reverseHexString( inputs.get( i ).getTx_hash( ) );
            String outputIndex = reverseHexString( paddingZeroLeft( decimalToHexString( inputs.get( i ).getTx_output_n( ).intValue( ) ), 8 ) );

            String script = inputs.get( i ).getSignedScript( );
            String scriptLength = decimalToHexString( ( script.length( ) / 2 ) );
            String sequence = paddingLeft( "", "f", 8 );

            inputScript
                    .append( prevHashReverse )
                    .append( outputIndex )
                    .append( scriptLength )
                    .append( script )
                    .append( sequence );
        }

        return inputScript.toString( );
    }


    private static String signer( String hashMessage, String privateKey ) {
        ECKeyPair keyPair = ECKeyPair.create( Utils.hexStringToByteArray( privateKey ) );
        ECDSASignature signature = keyPair.sign( Hex.decode( hashMessage ) );
        return toDER( signature );
    }

    private static String toDER( ECDSASignature signature ) {

        String r = Utils.byteArrayToHexString( bigIntegerToBytes( signature.r, 32 ) );
        String s = Utils.byteArrayToHexString( bigIntegerToBytes( signature.s, 32 ) );
        String rPrefix = Integer.parseInt( r.substring( 0, 2 ), 16 ) > 127 ? "00" : "";
        String sPrefix = Integer.parseInt( s.substring( 0, 2 ), 16 ) > 127 ? "00" : "";
        return "30"
                + ( 44 + ( rPrefix.equals( "00" ) ? 1 : 0 ) + ( sPrefix.equals( "00" ) ? 1 : 0 ) )
                + "02"
                + ( rPrefix.equals( "00" ) ? 21 : 20 )
                + rPrefix
                + r
                + "02"
                + ( sPrefix.equals( "00" ) ? 21 : 20 )
                + sPrefix
                + s;
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


    private static String scriptPubKey( String address ) {
        String fromScript = Utils.byteArrayToHexString( Base58.decode( address ) );
        fromScript = fromScript.substring( 2, fromScript.length( ) - 8 );
        fromScript = "76a914" + fromScript + "88ac";
        return fromScript;
    }

    private static String paddingZero( int size ) {
        return paddingLeft( "", "0", size );
    }

    private static String paddingZeroLeft( String str, int size ) {
        return paddingLeft( str, "0", size );
    }

    private static String paddingZeroRight( String str, int size ) {
        return paddingRight( str, "0", size );
    }


    private static String paddingLeft( String str, String padding, int size ) {
        StringBuilder buffer = new StringBuilder( );
        for ( int i = 0; i < size; i++ ) {
            buffer.append( padding );
        }
        return buffer.substring( str.length( ) ) + str;
    }

    private static String paddingRight( String str, String padding, int size ) {
        StringBuilder buffer = new StringBuilder( );
        for ( int i = 0; i < size; i++ ) {
            buffer.append( padding );
        }
        return str + buffer.substring( str.length( ) );
    }

    private static String decimalStringToHexString( String decimal ) {
        String result = new BigDecimal( decimal ).toBigInteger( ).toString( 16 );
        if ( result.length( ) % 2 == 1 ) result = "0" + result;
        return result;
    }

    private static String decimalToHexString( int decimal ) {
        String result = new BigDecimal( decimal ).toBigInteger( ).toString( 16 );
        if ( result.length( ) % 2 == 1 ) result = "0" + result;
        return result;
    }

    private static String reverseHexString( String str ) {
        StringBuilder builder = new StringBuilder( );
        for ( int i = 0; i < str.length( ); i += 2 ) {
            String s = str.substring( i, i + 2 );
            builder.insert( 0, s );
        }
        return builder.toString( );
    }

}
