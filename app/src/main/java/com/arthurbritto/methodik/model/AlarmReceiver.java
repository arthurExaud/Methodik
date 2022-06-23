package com.arthurbritto.methodik.model;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.arthurbritto.methodik.R;
import com.arthurbritto.methodik.view.DestinationAlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, DestinationAlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,i,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ThisIsAnAlarm")
                .setSmallIcon(R.drawable.ic_add_white_24dp)
                .setContentTitle("ThisIsAnAlarm Alarm Manager")
                .setContentText("ThisIsAnBeautifulAlarm")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());
    }
}
