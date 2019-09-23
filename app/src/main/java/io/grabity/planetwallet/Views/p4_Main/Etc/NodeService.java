package io.grabity.planetwallet.Views.p4_Main.Etc;

import java.util.ArrayList;
import java.util.HashMap;

import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.networktask.NetworkInterface;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.MainItemStore;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;

public class NodeService {

    private HashMap< CoinType, NetworkInterface > networkInterfaces = new HashMap<>( );
    private Planet planet;

    private OnNodeServiceListener onNodeServiceListener;

    private static final NodeService ourInstance = new NodeService( );

    private PlanetWalletActivity activity;

    public static NodeService getInstance( ) {
        return ourInstance;
    }

    public int tokenCount = 0;

    private NodeService( ) {
        networkInterfaces.put( CoinType.BTC, btcNetworkListener );
        networkInterfaces.put( CoinType.ETH, ethNetworkListener );
    }


    public void getBalance( Planet planet ) {
        this.planet = planet;
        CoinType coinType = CoinType.of( planet.getCoinType( ) );
        new Get( networkInterfaces.get( coinType ) )
                .setDeviceKey( activity != null ? activity.getPlanetWalletApplication( ).getDeviceKey( ) : "" )
                .action( Route.URL( "balance", coinType.name( ), planet.getAddress( ) ), 0, planet.get_id( ), null );
    }

    public void getMainList( Planet planet ) {
        this.planet = planet;
        CoinType coinType = CoinType.of( planet.getCoinType( ) );

        if ( coinType == CoinType.BTC ) {

            new Get( networkInterfaces.get( coinType ) )
                    .setDeviceKey( activity != null ? activity.getPlanetWalletApplication( ).getDeviceKey( ) : "" )
                    .action( Route.URL( "tx", "list", coinType.name( ), planet.getAddress( ) ), 1, planet.get_id( ), null );

        } else if ( coinType == CoinType.ETH ) {

            if ( planet.getItems( ) != null ) {
                tokenCount = planet.getItems( ).size( );
                for ( int i = 0; i < planet.getItems( ).size( ); i++ ) {
                    if ( Utils.equals( CoinType.ETH.getCoinType( ), planet.getItems( ).get( i ).getCoinType( ) ) ) {

                        new Get( networkInterfaces.get( coinType ) ).
                                setDeviceKey( activity != null ? activity.getPlanetWalletApplication( ).getDeviceKey( ) : "" ).action( Route.URL( "balance", CoinType.ETH.name( ), planet.getName( ) ), planet.get_id( ), i, null );

                    } else {

                        new Get( networkInterfaces.get( coinType ) ).
                                setDeviceKey( activity != null ? activity.getPlanetWalletApplication( ).getDeviceKey( ) : "" ).action( Route.URL( "balance", planet.getItems( ).get( i ).getSymbol( ), planet.getName( ) ), planet.get_id( ), i, null );

                    }
                }
            }

        }
    }

    private NetworkInterface btcNetworkListener = new NetworkInterface( ) {
        @Override
        public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
            if ( !error ) {
                if ( requestCode == 0 && resultCode == planet.get_id( ) ) {
                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, MainItem.class );
                    if ( returnVO.isSuccess( ) ) {
                        MainItem mainItem = ( MainItem ) returnVO.getResult( );

                        planet.getMainItem( ).setBalance( mainItem.getBalance( ) );
                        MainItemStore.getInstance( ).update( planet.getMainItem( ) );

                        if ( onNodeServiceListener != null ) {
                            onNodeServiceListener.onBalance( planet, mainItem.getBalance( ), true );
                        }
                    }
                } else if ( requestCode == 1 && planet.get_id( ) == resultCode ) {
                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Tx.class );
                    if ( returnVO.isSuccess( ) ) {
                        ArrayList< Tx > txList = ( ArrayList< Tx > ) returnVO.getResult( );
                        if ( onNodeServiceListener != null ) {
                            onNodeServiceListener.onTxList( planet, txList, result, true );
                        }
                    }
                }
            } else {
                onNodeServiceListener.onTxList( null, null, null, false );
            }

        }
    };


    private NetworkInterface ethNetworkListener = new NetworkInterface( ) {
        @Override
        public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {

            if ( requestCode > 0 && planet.get_id( ) == requestCode ) {
                tokenCount -= 1;
            }

            if ( !error ) {
                if ( requestCode == 0 && resultCode == planet.get_id( ) ) {

                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, MainItem.class );
                    if ( returnVO.isSuccess( ) ) {
                        MainItem mainItem = ( MainItem ) returnVO.getResult( );

                        planet.getMainItem( ).setBalance( mainItem.getBalance( ) );
                        MainItemStore.getInstance( ).update( planet.getMainItem( ) );

                        if ( onNodeServiceListener != null ) {
                            onNodeServiceListener.onBalance( planet, mainItem.getBalance( ), true );
                        }
                    }

                } else {

                    if ( requestCode > 0 && planet.get_id( ) == requestCode ) {

                        ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, MainItem.class );
                        if ( returnVO.isSuccess( ) ) {
                            MainItem mainItem = ( MainItem ) returnVO.getResult( );

                            planet.getItems( ).get( resultCode ).setBalance( mainItem.getBalance( ) );
                            MainItemStore.getInstance( ).update( planet.getItems( ).get( resultCode ) );
                        }

                        if ( tokenCount == 0 ) {

                            if ( onNodeServiceListener != null ) {
                                onNodeServiceListener.onTokenBalance( planet, planet.getItems( ), true );
                            }

                        }
                    }

                }
            } else {
                onNodeServiceListener.onBalance( null, null, false );
                onNodeServiceListener.onTokenBalance( null, null, false );
            }

        }
    };

    public void setOnNodeServiceListener( OnNodeServiceListener onNodeServiceListener, PlanetWalletActivity activity ) {
        this.activity = activity;
        this.onNodeServiceListener = onNodeServiceListener;
    }

    public interface OnNodeServiceListener {

        void onBalance( Planet p, String balance, boolean complete );

        void onTokenBalance( Planet p, ArrayList< MainItem > tokenList, boolean complete );

        void onTxList( Planet p, ArrayList< Tx > txList, String result, boolean complete );

    }
}
