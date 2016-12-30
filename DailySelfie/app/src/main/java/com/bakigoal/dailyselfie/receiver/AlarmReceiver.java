package com.bakigoal.dailyselfie.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bakigoal.dailyselfie.SelfieListActivity;

public class AlarmReceiver extends BroadcastReceiver {

  private static final String TAG = "AlarmReceiver";

  public static final int NOTIFICATION_ID = 12345;

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "received intent broadcast from alarm");
    PendingIntent pendingIntent = PendingIntent.getActivity(
        context.getApplicationContext(),
        0,
        new Intent(context.getApplicationContext(), SelfieListActivity.class),
        PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
    Notification notification = new Notification.Builder(context.getApplicationContext())
        .setContentTitle("DailySelfie")
        .setContentText("Take your daily selfie")
        .setSmallIcon(android.R.drawable.ic_menu_camera)
        .setTicker("selfie time!")
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build();

    notificationManager.notify(NOTIFICATION_ID, notification);

  }
}
