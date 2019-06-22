package com.example.androidcore_jobscheduler;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationJobService extends JobService {

    NotificationManager mNotifyManager;
    private static final String CHANNEL_ID = "PRIMARY_NOTIFICATION_CHANNEL";
    private static final int REQUEST_CODE = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        createNotificationChannel();
        NotificationCompat.Builder builder = getNotification(this);
        mNotifyManager.notify(REQUEST_CODE, builder.build());
        return false;
    }

    private NotificationCompat.Builder getNotification(Context context) {
        Intent contentIntent = new Intent(this, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setContentTitle("Job Service")
                .setContentText("Your job ran to Completion")
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_image)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        return builder;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "Job service notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setDescription("Notification from Job Service");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

}
