package io.grabity.planetwallet.MiniFramework.wallet.store;

import java.util.ArrayList;

import io.grabity.planetwallet.MiniFramework.managers.DatabaseManager.PWDBManager;
import io.grabity.planetwallet.VO.Planet;

public class SearchStore {

    private static final SearchStore instance = new SearchStore( );

    public SearchStore( ) {
    }

    public static SearchStore getInstance( ) {
        return instance;
    }

    public ArrayList< Planet > getSearchList( String keyId ) {
        return getSearchList( keyId, null );
    }

    public ArrayList< Planet > getSearchList( String keyId, String symbol ) {
        if ( symbol == null ) {
            return PWDBManager.getInstance( ).select( Planet.class, "Search", "keyId='" + keyId + "'" + " ORDER BY date DESC", null );
        } else {
            return PWDBManager.getInstance( ).select( Planet.class, "Search", "keyId='" + keyId + "'" + " AND " + "symbol='" + symbol + "'" + " ORDER BY date DESC", null );
        }
    }

    public void save( Planet planet ) {

        if ( PWDBManager.getInstance( ).select( Planet.class, "Search", "keyId='" + planet.getKeyId( ) + "'" + " AND " + "address='" + planet.getAddress( ) + "'" + " AND " + "symbol='" + planet.getSymbol( ) + "'", null ).size( ) == 0 ) {
            PWDBManager.getInstance( ).insertData( "Search", planet );
        } else {
            update( planet );
        }

    }

    public void update( Planet planet ) {
        PWDBManager.getInstance( ).updateData( "Search", planet, "keyId='" + planet.getKeyId( ) + "'" + " AND " + "address='" + planet.getAddress( ) + "'" + " AND " + "symbol='" + planet.getSymbol( ) + "'" );
    }


    public void delete( Planet planet ) {
        PWDBManager.getInstance( ).deleteData( "Search", planet, "keyId='" + planet.getKeyId( ) + "'" + " AND " + "address='" + planet.getAddress( ) + "'" + " AND " + "symbol='" + planet.getSymbol( ) + "'" );
    }

}
