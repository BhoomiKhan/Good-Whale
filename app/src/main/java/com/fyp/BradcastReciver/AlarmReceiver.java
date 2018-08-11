package com.fyp.BradcastReciver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;
import com.fyp.Activities.MainActivity;
import com.fyp.R;
import com.fyp.Utils.Utilities;
import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.singhajit.notificationDemo.channelId";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        String tabName=intent.getStringExtra("tab_name");
        String tabTaskCounterName=intent.getStringExtra("tab_task_counter_name");
        int lastTaskNumber=Utilities.getLastTaskNumber(context, tabTaskCounterName);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = builder.setContentTitle("Task Management")
                .setContentText("Starts new "+tabName+" Task T" + (lastTaskNumber+1))
                .setTicker("Starts new "+tabName+" Task T" + (lastTaskNumber+1))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationDemo",
                    IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notification);
        //because we have 5 descriptins so last upgrade will be done when task number will be 4 but alarm will be run in case after complted the task num5
        Log.d("onReceiveforSocial","task number "+lastTaskNumber);

        if (lastTaskNumber <=15) {
            Log.d("ReceiverConditional","tab name "+tabName);
            Utilities.setAlarm(context,tabName,tabTaskCounterName);
            Utilities.upgradeTaskCounter(context, tabTaskCounterName);
        } else {
            Toast.makeText(context, "Tasks has been completed", Toast.LENGTH_SHORT).show();
        }
    }
}