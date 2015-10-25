package au.com.connectedteam.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import au.com.connectedteam.R;
import au.com.connectedteam.activity.GenericDetailActivity;
import au.com.connectedteam.activity.feed.EventDetailsFragment;
import au.com.connectedteam.util.IntentUtil;
import au.com.connectedteam.util.Reflect;
import au.com.connectedteam.util.StringUtils;

/**
 * Created by bramleyt on 25/10/2015.
 */
public class ConnectedPushReceiver extends ParsePushBroadcastReceiver{

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        JSONObject pushData = getPushData(intent);
        if(pushData!=null && pushData.has("payload")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL);
            builder.setContentTitle(pushData.optString("alert"));
            builder.setSmallIcon(R.drawable.ic_launcher);
            String payloadStr =pushData.optString("payload");

            JSONObject payload = null;
            try {
                payload = new JSONObject(payloadStr);
            } catch (JSONException e) {
                e.printStackTrace();
                return super.getNotification(context, intent);
            }
            builder.setContentText(payload.optString("blurb"));
            String category = payload.optString("category");
            category = StringUtils.isNullOrEmpty(category, "lecture").toLowerCase();
            int categoryRes = Reflect.getImageResId("ic_event_" + category);
            if(categoryRes==0) categoryRes=R.drawable.ic_event_lecture;
            BitmapDrawable largeIcon = (BitmapDrawable) context.getResources().getDrawable(categoryRes);
            builder.setLargeIcon(largeIcon.getBitmap());
            builder.setContentIntent(getEventDetailIntent(context, payload.optString("id")));
            builder.addAction(R.drawable.ic_action_done, "Reserve", getJoinEventIntent(context, payload.optString("id")));
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(payload.optString("blurb")));
            return builder.build();
        }
        else return super.getNotification(context, intent);
    }
    private PendingIntent getEventDetailIntent(Context c, String objectID){
        Intent intent = GenericDetailActivity.makeIntent(c, EventDetailsFragment.class, EventDetailsFragment.TAG, "Session Details");
        intent.putExtra(IntentUtil.ARG_EVENT_ID, objectID);
        intent.setData(Uri.parse("connectedteam.com.au/"+objectID));
        return PendingIntent.getActivity(c, 0, intent,  0);
    }
    private PendingIntent getJoinEventIntent(Context c, String objectID){
        Intent intent = GenericDetailActivity.makeIntent(c, EventDetailsFragment.class, EventDetailsFragment.TAG, "Session Details");
        intent.putExtra(IntentUtil.ARG_EVENT_ID, objectID);
        intent.setData(Uri.parse("connectedteam.com.au/" + objectID + "/join"));
        intent.putExtra(EventDetailsFragment.ARG_JOIN_EVENT, true);
        return PendingIntent.getActivity(c, 0, intent,  0);
    }
    private JSONObject getPushData(Intent intent) {
        try {
            return new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
        } catch (JSONException e) {
            return null;
        }
    }
}
