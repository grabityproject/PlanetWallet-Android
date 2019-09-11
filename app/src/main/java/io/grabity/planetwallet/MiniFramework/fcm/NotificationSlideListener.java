package io.grabity.planetwallet.MiniFramework.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import me.leolin.shortcutbadger.ShortcutBadger;

public class NotificationSlideListener extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {
        if ( Utils.equals( intent.getAction( ), C.action.NOTIFICATION_SLIDE ) ) {
            ShortcutBadger.removeCount( context );
        }
    }
}
