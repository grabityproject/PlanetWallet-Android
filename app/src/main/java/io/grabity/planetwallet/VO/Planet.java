package io.grabity.planetwallet.VO;

import com.pentasecurity.cryptowallet.JniWrapper;
import com.pentasecurity.cryptowallet.key.HDKeyPair;

import org.spongycastle.util.Arrays;

import java.io.Serializable;
import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.KeyPairStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.MainItems.MainItem;

public class Planet implements Serializable {

    private Integer _id;
    private String keyId;
    private Integer pathIndex;
    private Integer coinType;
    private String symbol;
    private String decimals;
    private String hide;
    private String address;
    private String name;
    private String balance;

    private String signature;
    private String planet;

    private ArrayList< MainItem > items;

    public Planet( ) {

    }

    public Integer get_id( ) {
        return _id;
    }

    public void set_id( Integer _id ) {
        this._id = _id;
    }

    public String getKeyId( ) {
        return keyId;
    }

    public void setKeyId( String keyId ) {
        this.keyId = keyId;
    }

    public Integer getPathIndex( ) {
        return pathIndex;
    }

    public void setPathIndex( Integer pathIndex ) {
        this.pathIndex = pathIndex;
    }

    public Integer getCoinType( ) {
        return coinType;
    }

    public void setCoinType( Integer coinType ) {
        this.coinType = coinType;
    }

    public String getSymbol( ) {
        return symbol;
    }

    public void setSymbol( String symbol ) {
        this.symbol = symbol;
    }

    public String getDecimals( ) {
        return decimals;
    }

    public void setDecimals( String decimals ) {
        this.decimals = decimals;
    }

    public String getHide( ) {
        return hide;
    }

    public void setHide( String hide ) {
        this.hide = hide;
    }

    public String getAddress( ) {
        return address;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getName( ) {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public int getIconRes( ) {
        if ( getCoinType( ) != null && getCoinType( ).equals( CoinType.BTC.getCoinType( ) ) ) {
            return R.drawable.icon_bit;
        } else if ( getCoinType( ) != null && getCoinType( ).equals( CoinType.ETH.getCoinType( ) ) ) {
            return R.drawable.icon_eth;
        }
        return 0;
    }

    public ArrayList< MainItem > getItems( ) {
        return items;
    }

    public void setItems( ArrayList< MainItem > items ) {
        this.items = items;
    }

    public String getPrivateKey( KeyPairStore keyPairStore, char[] pinCode ) {
        if ( keyPairStore != null ) {
            HDKeyPair keyPair = keyPairStore.getKeyPair( getKeyId( ), pinCode );
            if ( keyPair != null ) {
                return byteArrayToHexString( keyPair.getPrivateKey( ) );
            }
        }
        return null;
    }

    //bitCoin privateKey
    public String getPrivateKeyBase58Encode( KeyPairStore keyPairStore, char[] pinCode ) {
        if ( keyPairStore != null ) {
            HDKeyPair keyPair = keyPairStore.getKeyPair( getKeyId( ), pinCode );

            if ( keyPair != null ) {
                return JniWrapper.GenBase58CheckEncode( Arrays.concatenate( new byte[]{ ( byte ) 0x80 }, keyPair.getPrivateKey( ), new byte[]{ 0x01 } ) );
            }
        }
        return null;
    }


    public String getMnemonic( KeyPairStore keyPairStore, char[] pinCode ) {
        if ( getPathIndex( ) == -2 ) {
            return keyPairStore.getPhrase( getKeyId( ), pinCode );
        } else if ( getPathIndex( ) >= 0 ) {
            return keyPairStore.getPhrase( getCoinType( ), pinCode );
        }
        return null;
    }

    private static String byteArrayToHexString( byte[] var0 ) {
        StringBuilder var1 = new StringBuilder( );
        int var2 = ( var0 = var0 ).length;

        for ( int var3 = 0; var3 < var2; ++var3 ) {
            byte var4 = var0[ var3 ];
            var1.append( String.format( "%02x", var4 ) );
        }

        return var1.toString( );
    }

    public String getSignature( ) {
        return signature;
    }

    public void setSignature( String signature ) {
        this.signature = signature;
    }

    public String getPlanet( ) {
        return planet;
    }

    public void setPlanet( String planet ) {
        this.planet = planet;
    }

    public String getBalance( ) {
        if ( balance == null ) return "0";
        return balance;
    }

    public void setBalance( String balance ) {
        this.balance = balance;
    }

    @Override
    public String toString( ) {
        return "{ path=" + getPathIndex( ) + ", keyId = " + getKeyId( ) + ", name=" + getName( ) + ", address=" + getAddress( ) + ", symbol=" + getSymbol( ) + ", coinType=" + getCoinType( ) + " }";
    }
}
