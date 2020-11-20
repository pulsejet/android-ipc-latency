package com.lrptest.daemon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class LrpService extends Service {
    private static final int ONGOING_NOTIFICATION_ID = 1000;
    final static String TAG = "LRP_LOG_DAEMON_SRV";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        String channelId = createNotificationChannel("LrpService", "LTE Accelerator");
        Notification notification =
                new Notification.Builder(this, channelId)
                        .setContentTitle("LRP Service")
                        .setContentText("Keeps your LTE responsive")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,  channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LrpService.ActionReceiver receiver = new LrpService.ActionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.lrptest.daemon.toast");
        filter.addAction("com.lrptest.daemon.measure");
        registerReceiver(receiver, filter);

        Toast.makeText(this, "LRP service started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    public class ActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceiveService: " + intent.getAction());
            LrpHandler.handleIntent(intent, getApplicationContext());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "LRP service terminated", Toast.LENGTH_SHORT).show();
    }
}
