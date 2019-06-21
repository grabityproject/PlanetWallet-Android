package io.grabity.planetwallet.MiniFramework.wallet.store;


import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.managers.DatabaseManager.PWDBManager;
import io.grabity.planetwallet.VO.MainItems.ERC20;

public class ERC20Store {

    private static final ERC20Store instance = new ERC20Store( );

    private ERC20Store( ) {
    }

    public static ERC20Store getInstance( ) {
        return instance;
    }

    public ArrayList< ERC20 > getTokenList( String keyId ) {
        return getTokenList( keyId, true );
    }

    public ArrayList< ERC20 > getTokenList( String keyId, boolean all ) {
        if ( all ) {
            return PWDBManager.getInstance( ).select( ERC20.class, "ERC20", "keyId='" + keyId + "'", null );
        } else {
            return PWDBManager.getInstance( ).select( ERC20.class, "ERC20", String.format( "keyId = '%s' AND hide = 'N'", keyId ), null );
        }
    }

    public String save( ERC20 erc20 ) {
        String id = erc20.getKeyId( );
        if ( PWDBManager.getInstance( ).select( ERC20.class, "ERC20", "keyId='" + id + "' AND contract='" + erc20.getContract( ) + "'", null ).size( ) == 0 ) {
            PWDBManager.getInstance( ).insertData( erc20 );
        } else {
            update( erc20 );
        }
        return id;
    }

    public String update( ERC20 erc20 ) {
        String id = erc20.getKeyId( );
        PWDBManager.getInstance( ).updateData( erc20, "keyId='" + id + "' AND contract='" + erc20.getContract( ) + "'" );
        return id;
    }

    public void delete( String s ) {
        ERC20 erc20 = new ERC20( );
        erc20.setKeyId( s );
        PWDBManager.getInstance( ).deleteData( erc20 );
    }
}
