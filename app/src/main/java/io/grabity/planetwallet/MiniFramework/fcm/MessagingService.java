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
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.Views.p1_Splash.Activity.SplashActivity;
import io.grabity.planetwallet.Views.p4_Main.Activity.MainActivity;
import me.leolin.shortcutbadger.ShortcutBadger;


public class MessagingService extends FirebaseMessagingService {

    private final String channelID = "PlanetWallet";
    private final String channelName = "메시지";

    @Override
    public void onNewToken( String s ) {
        super.onNewToken( s );
    }

    @Override
    public void onMessageReceived( RemoteMessage remoteMessage ) {
        super.onMessageReceived( remoteMessage );
        if ( ( ( PlanetWalletApplication ) getApplication( ) ).getMessagingListeners( ) != null ) {
            for ( OnMessagingListener listener : ( ( PlanetWalletApplication ) getApplication( ) ).getMessagingListeners( ) ) {
                listener.onMessage( remoteMessage );
            }
        }
        if ( Utils.equals( Utils.getPreferenceData( this, C.pref.LAST_PLANET_KEYID ), "" ) ) return;
        sendNotification( remoteMessage );
    }

    private void sendNotification( RemoteMessage remoteMessage ) {
        int count = ( int ) Utils.getPreferenceData( this, C.pref.NOTIFIACTION_COUNT, 0 );
        count += 1;
        Utils.setPreferenceData( this, C.pref.NOTIFIACTION_COUNT, count );
        NotificationManager notificationManager = ( NotificationManager ) getSystemService( Context.NOTIFICATION_SERVICE );

        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ) {
            NotificationChannel channel = new NotificationChannel( channelID, channelName, NotificationManager.IMPORTANCE_HIGH );
            channel.setLockscreenVisibility( Notification.VISIBILITY_PUBLIC ); //lock Screen show notification

            if ( notificationManager.getNotificationChannel( channelID ) == null ) { // channel not exist create channel
                notificationManager.createNotificationChannel( channel );
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder( getApplicationContext( ), Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? channelID : "" )
                .setSmallIcon( R.drawable.ic_notification )
                .setWhen( System.currentTimeMillis( ) )
                .setShowWhen( true )
                .setAutoCancel( true )
                .setContentTitle( remoteMessage.getData( ).get( "title" ) )
                .setContentText( remoteMessage.getData( ).get( "body" ) )
                .setContentIntent( clickOnNotification( ) )
                .setStyle( new NotificationCompat.BigTextStyle( ).bigText( remoteMessage.getData( ).get( "body" ) ) )
                .setDeleteIntent( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? null : versionMinNougatNotificationSlide( ) )
                .setDefaults( Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE )
                .setPriority( NotificationCompat.PRIORITY_MAX );
        notificationManager.notify( 0, builder.build( ) );


        if ( ( ( PlanetWalletApplication ) getApplication( ) ).getTopActivity( ) != null ) {
            if ( Utils.equals( ( ( PlanetWalletApplication ) getApplication( ) ).getTopActivity( ).getClass( ).getSimpleName( ), MainActivity.class.getSimpleName( ) ) ) {
                Utils.setPreferenceData( this, C.pref.NOTIFIACTION_COUNT, 0 );
                ShortcutBadger.removeCount( this );
                return;
            }
        }

        if ( Build.MANUFACTURER.equalsIgnoreCase( "Xiaomi" ) ) {  //device Xiaomi
            Notification notification = builder.build( );
            ShortcutBadger.applyNotification( this, notification, ( int ) Utils.getPreferenceData( this, C.pref.NOTIFIACTION_COUNT, 0 ) );
        } else {
            ShortcutBadger.applyCount( this, ( int ) Utils.getPreferenceData( this, C.pref.NOTIFIACTION_COUNT, 0 ) );
        }
    }

    private PendingIntent versionMinNougatNotificationSlide( ) {
        return PendingIntent.getBroadcast( this, 0, new Intent( this, NotificationSlideListener.class )
                .setAction( C.action.NOTIFICATION_SLIDE ), PendingIntent.FLAG_ONE_SHOT );
    }

    private PendingIntent clickOnNotification( ) {
        return PendingIntent.getActivity( this, 0, new Intent( this, SplashActivity.class )
                .setAction( Intent.ACTION_MAIN )
                .addCategory( Intent.CATEGORY_LAUNCHER )
                .addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ), PendingIntent.FLAG_ONE_SHOT );
    }

}
