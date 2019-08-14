package io.grabity.planetwallet.MiniFramework.wallet.store;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.managers.DatabaseManager.PWDBManager;
import io.grabity.planetwallet.VO.Search;

public class SearchStore {

    private static final SearchStore instance = new SearchStore( );

    public SearchStore( ) {
    }

    public static SearchStore getInstance( ) {
        return instance;
    }

    public ArrayList< Search > getSearchList( String keyId ) {
        return getSearchList( keyId, null );
    }

    public ArrayList< Search > getSearchList( String keyId, String symbol ) {
        if ( symbol == null ) {
            return PWDBManager.getInstance( ).select( Search.class, "Search", "keyId='" + keyId + "'" + " ORDER BY _id DESC", null );
        } else {
            return PWDBManager.getInstance( ).select( Search.class, "Search", "keyId='" + keyId + "'" + " AND " + "symbol='" + symbol + "'" + " ORDER BY _id DESC", null );
        }
    }

    public void save( Search search ) {

        if ( PWDBManager.getInstance( ).select( Search.class, "Search", "keyId='" + search.getKeyId( ) + "'" + " AND " + "name='" + search.getName( ) + "'" + " AND " + "symbol='" + search.getSymbol( ) + "'", null ).size( ) == 0 ) {
            PWDBManager.getInstance( ).insertData( search );
        }

    }

    public void delete( Search search ) {
        PWDBManager.getInstance( ).deleteData( search );
    }

}
