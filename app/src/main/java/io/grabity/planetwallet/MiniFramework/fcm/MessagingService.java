package io.grabity.planetwallet.MiniFramework.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.PlanetWalletApplication;
import io.grabity.planetwallet.MiniFramework.utils.PLog;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;


public class MessagingService extends FirebaseMessagingService {

    private String channelID = "ID_PlanetWallet";
    private String channelName = "Name_PlanetWallet";

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

            if ( Utils.equals( Utils.getPreferenceData( this, C.pref.LAST_PLANET_KEYID ), "" ) ) return;
            sendNotification( );
        }
    }

    private void sendNotification( ) {
        NotificationManager notificationManager = ( NotificationManager ) getSystemService( Context.NOTIFICATION_SERVICE );
        PendingIntent intent = PendingIntent.getActivity( this, 0, new Intent( ), PendingIntent.FLAG_ONE_SHOT );

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            NotificationChannel channel = new NotificationChannel( channelID, channelName, NotificationManager.IMPORTANCE_HIGH );
            channel.setLockscreenVisibility( Notification.VISIBILITY_PUBLIC ); //lock Screen show notification

            if ( notificationManager.getNotificationChannel( channelID ) == null ) {
                notificationManager.createNotificationChannel( channel );
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder( getApplicationContext(), Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? channelID : ""  )
                .setSmallIcon( R.drawable.icon_planet_noti )
                .setWhen( System.currentTimeMillis( ) )
                .setShowWhen( true )
                .setAutoCancel( true )
                .setContentTitle( "노티알림" )
                .setContentText( "서브메시지" )
                .setContentIntent( intent )
                .setDefaults( Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE )
                .setPriority( NotificationCompat.PRIORITY_MAX );
        notificationManager.notify( 0, builder.build( ) );

    }
}
