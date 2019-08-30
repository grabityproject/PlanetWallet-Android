package io.grabity.planetwallet.MiniFramework.wallet.store;


import java.util.ArrayList;
import java.util.Locale;

import io.grabity.planetwallet.MiniFramework.managers.DatabaseManager.PWDBManager;
import io.grabity.planetwallet.VO.MainItems.MainItem;

public class MainItemStore {

    private static final MainItemStore instance = new MainItemStore( );

    private MainItemStore( ) {
    }

    public static MainItemStore getInstance( ) {
        return instance;
    }

    public ArrayList< MainItem > getMainItem( String keyId ) {
        return getMainItem( keyId, true );
    }

    public ArrayList< MainItem > getMainItem( String keyId, boolean all ) {
        if ( all ) {
            return PWDBManager.getInstance( ).select( MainItem.class, "MainItem", "keyId='" + keyId + "'", null );
        } else {
            return PWDBManager.getInstance( ).select( MainItem.class, "MainItem", String.format( "keyId = '%s' AND hide = 'N'", keyId ), null );
        }
    }

    public String save( MainItem mainItem ) {
        if ( PWDBManager.getInstance( ).select( MainItem.class, "MainItem", String.format( Locale.US, "_id = %d", mainItem.get_id( ) ), null ).size( ) == 0 ) {
            PWDBManager.getInstance( ).insertData( mainItem );
        } else {
            update( mainItem );
        }
        return mainItem.getKeyId( );
    }

    public String tokenSave( MainItem mainItem ) {
        if ( PWDBManager.getInstance( ).select( MainItem.class, "MainItem", String.format( Locale.US, "keyId = '%s' AND contract = '%s'", mainItem.getKeyId( ), mainItem.getContract( ) ), null ).size( ) == 0 ) {
            PWDBManager.getInstance( ).insertData( mainItem );
        } else {
            tokenUpdate( mainItem );
        }

        return mainItem.getKeyId( );
    }

    public String update( MainItem mainItem ) {
        PWDBManager.getInstance( ).updateData( mainItem, String.format( Locale.US, "_id = %d", mainItem.get_id( ) ) );
        return mainItem.getKeyId( );
    }

    public String tokenUpdate( MainItem mainItem ) {
        PWDBManager.getInstance( ).updateData( mainItem, String.format( Locale.US, "KeyId = '%s' AND contract = '%s'", mainItem.getKeyId( ), mainItem.getContract( ) ) );
        return mainItem.getKeyId( );
    }

}
