package io.grabity.planetwallet.Views.p4_Main.Etc;

import java.util.ArrayList;
import java.util.HashMap;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.networktask.NetworkInterface;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.cointype.CoinType;
import io.grabity.planetwallet.MiniFramework.wallet.store.ERC20Store;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.VO.MainItems.ERC20;
import io.grabity.planetwallet.VO.MainItems.ETH;
import io.grabity.planetwallet.VO.MainItems.MainItem;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Tx;

public class NodeService {

    private HashMap< CoinType, NetworkInterface > networkInterfaces = new HashMap<>( );
    private Planet planet;

    private OnNodeServiceListener onNodeServiceListener;

    private static final NodeService ourInstance = new NodeService( );

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

        if ( coinType == CoinType.BTC ) {

            new Get( networkInterfaces.get( coinType ) )
                    .setDeviceKey( C.DEVICE_KEY )
                    .action( Route.URL( "balance", coinType.name( ), planet.getAddress( ) ), 0, 0, null );

        } else if ( coinType == CoinType.ETH ) {

            // Token List add
            new Get( networkInterfaces.get( coinType ) )
                    .setDeviceKey( C.DEVICE_KEY )
                    .action( Route.URL( "balance", coinType.name( ), planet.getAddress( ) ), 0, 0, null );

        }

    }

    public void getMainList( Planet planet ) {
        this.planet = planet;
        CoinType coinType = CoinType.of( planet.getCoinType( ) );

        if ( coinType == CoinType.BTC ) {

            new Get( networkInterfaces.get( coinType ) )
                    .setDeviceKey( C.DEVICE_KEY )
                    .action( Route.URL( "tx", "list", coinType.name( ), planet.getAddress( ) ), 1, 0, null );

        } else if ( coinType == CoinType.ETH ) {

            if ( planet.getItems( ) != null ) {
                tokenCount = planet.getItems( ).size( );
                for ( int i = 0; i < planet.getItems( ).size( ); i++ ) {
                    if ( Utils.equals( CoinType.ETH.getCoinType( ), planet.getItems( ).get( i ).getCoinType( ) ) ) {

                        new Get( networkInterfaces.get( coinType ) ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "balance", CoinType.ETH.name( ), planet.getName( ) ), planet.get_id( ), i, null );

                    } else {

                        ERC20 erc20 = ( ERC20 ) planet.getItems( ).get( i );
                        new Get( networkInterfaces.get( coinType ) ).setDeviceKey( C.DEVICE_KEY ).action( Route.URL( "balance", erc20.getSymbol( ), planet.getName( ) ), planet.get_id( ), i, null );

                    }
                }
            }

        }
    }

    private NetworkInterface btcNetworkListener = new NetworkInterface( ) {
        @Override
        public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {

            if ( !error ) {
                if ( requestCode == 0 ) {

                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Planet.class );
                    if ( returnVO.isSuccess( ) ) {
                        Planet p = ( Planet ) returnVO.getResult( );
                        if ( onNodeServiceListener != null ) {
                            onNodeServiceListener.onBalance( planet, p.getBalance( ) );
                        }
                    }

                } else if ( requestCode == 1 ) {

                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Tx.class );
                    if ( returnVO.isSuccess( ) ) {
                        ArrayList< Tx > txList = ( ArrayList< Tx > ) returnVO.getResult( );
                        if ( onNodeServiceListener != null ) {
                            onNodeServiceListener.onTxList( planet, txList );
                        }
                    }

                }
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
                if ( requestCode == 0 ) {

                    ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Planet.class );
                    if ( returnVO.isSuccess( ) ) {
                        Planet p = ( Planet ) returnVO.getResult( );
                        p.setKeyId( planet.getKeyId( ) );
                        PlanetStore.getInstance( ).update( p );

                        if ( onNodeServiceListener != null ) {
                            planet.setBalance( p.getBalance( ) );
                            onNodeServiceListener.onBalance( planet, p.getBalance( ) );
                        }
                    }

                } else {

                    if ( requestCode > 0 && planet.get_id( ) == requestCode ) {

                        ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Planet.class );
                        if ( returnVO.isSuccess( ) ) {
                            Planet p = ( Planet ) returnVO.getResult( );
                            if ( resultCode != 0 ) {

                                ( ( ERC20 ) planet.getItems( ).get( resultCode ) ).setBalance( p.getBalance( ) );
                                ERC20Store.getInstance( ).update( ( ERC20 ) planet.getItems( ).get( resultCode ) );

                            } else {

                                ( ( ETH ) planet.getItems( ).get( resultCode ) ).setBalance( p.getBalance( ) );

                            }

                        }

                        if ( tokenCount == 0 ) {

                            if ( onNodeServiceListener != null ) {
                                onNodeServiceListener.onTokenBalance( planet, planet.getItems( ) );
                            }

                        }
                    }

                }
            }

        }
    };

    public void setOnNodeServiceListener( OnNodeServiceListener onNodeServiceListener ) {
        this.onNodeServiceListener = onNodeServiceListener;
    }

    public interface OnNodeServiceListener {

        void onBalance( Planet p, String balance );

        void onTokenBalance( Planet p, ArrayList< MainItem > tokenList );

        void onTxList( Planet p, ArrayList< Tx > txList );

    }
}
