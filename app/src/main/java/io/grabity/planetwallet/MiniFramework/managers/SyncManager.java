package io.grabity.planetwallet.MiniFramework.managers;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import io.grabity.planetwallet.MiniFramework.networktask.Post;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;

public class SyncManager {

    private static SyncManager instance;

    public static SyncManager getInstance( ) {
        if ( instance == null ) instance = new SyncManager( );
        return instance;
    }

    public void syncPlanet( OnSyncListener onSyncListener ) {
        ArrayList< Planet > planets = PlanetStore.getInstance( ).getPlanetList( );
        HashMap< String, String > addresses = new HashMap<>( );
        for ( int i = 0; i < planets.size( ); i++ ) {
            addresses.put( String.format( Locale.US, "%s[%d]", CoinType.of( planets.get( i ).getCoinType( ) ).name( ), i ), planets.get( i ).getAddress( ) );
        }
        new Post( ( error, requestCode, resultCode, statusCode, result ) -> {
            if ( !error ) {
                if ( requestCode == 0 && statusCode == 200 ) {
                    AtomicBoolean isUpdated = new AtomicBoolean( false );
                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class );
                    if ( returnVO.isSuccess( ) ) {
                        try {
                            LinkedTreeMap resultMap = ( LinkedTreeMap ) returnVO.getResult( );
                            planets.forEach( planet -> {
                                if ( resultMap.get( planet.getAddress( ) ) != null ) {
                                    LinkedTreeMap resultPlanet = ( LinkedTreeMap ) resultMap.get( planet.getAddress( ) );
                                    if ( resultPlanet != null && resultPlanet.get( "name" ) != null && !planet.getName( ).equals( resultPlanet.get( "name" ) ) ) {
                                        planet.setName( String.valueOf( resultPlanet.get( "name" ) ) );
                                        PlanetStore.getInstance( ).update( planet );
                                        isUpdated.set( true );
                                    }
                                }
                            } );

                            onSyncListener.onSyncComplete( SyncType.PLANET, true, isUpdated.get( ) );

                        } catch ( ClassCastException e ) {
                            PLog.e( e.getMessage( ) );
                            onSyncListener.onSyncComplete( SyncType.PLANET, false, false );
                        }
                    }
                }

            } else {
                onSyncListener.onSyncComplete( SyncType.PLANET, false, false );
            }


        } ).action( Route.URL( "sync", "planets" ), 0, 0, addresses );
    }

    public enum SyncType {
        PLANET,
        ERC20
    }

    public interface OnSyncListener {
        void onSyncComplete( SyncType syncType, boolean complete, boolean isUpdated );
    }

}
