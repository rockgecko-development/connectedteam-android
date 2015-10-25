package au.com.connectedteam.notification;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by bramleyt on 25/10/2015.
 */
public class ConnectedPushReceiver extends ParsePushBroadcastReceiver{

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        return super.getNotification(context, intent);
    }
}
