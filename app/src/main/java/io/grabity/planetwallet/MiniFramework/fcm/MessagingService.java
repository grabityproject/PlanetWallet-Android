package io.grabity.planetwallet.MiniFramework.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.grabity.planetwallet.Common.components.PlanetWalletApplication;
import io.grabity.planetwallet.MiniFramework.utils.PLog;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken( String s ) {
        super.onNewToken( s );
        PLog.e( "newToken : " + s );
    }

    @Override
    public void onMessageReceived( RemoteMessage remoteMessage ) {
        super.onMessageReceived( remoteMessage );
        if ( ( ( PlanetWalletApplication ) getApplication( ) ).getMessagingListeners( ) != null ) {
            for ( OnMessagingListener listener : ( ( PlanetWalletApplication ) getApplication( ) ).getMessagingListeners( ) ) {
                listener.onMessage( remoteMessage );
            }
        }
    }
}
