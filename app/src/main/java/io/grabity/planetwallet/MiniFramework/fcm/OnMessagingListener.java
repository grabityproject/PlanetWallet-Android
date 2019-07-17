package io.grabity.planetwallet.MiniFramework.fcm;

import com.google.firebase.messaging.RemoteMessage;

public interface OnMessagingListener {
    void onMessage( RemoteMessage remoteMessage );
}
