package com.tsl.app.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tsl.app.R;
import com.tsl.app.repository.server.HttpServer;

import java.io.IOException;

public class HttpService extends Service {
    private String notificationChannelId = "info_show";
    private String notificationChannelName = "请勿关闭";
    private HttpServer httpServer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 注意第一个参数不能为0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, getNotification());
        }
        httpServer = new HttpServer();
        try {
            httpServer.start(30000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (httpServer != null) {
            httpServer.stop();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {
        NotificationChannel channel = new NotificationChannel(notificationChannelId,
                notificationChannelName,
                NotificationManager.IMPORTANCE_LOW);
        channel.setShowBadge(false);
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        return new Notification.Builder(this, notificationChannelId)
                .setContentTitle("本地服务")
                .setContentText("本地微服务，请勿关闭！")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }
    /**
     * 启动服务
     *
     * @param context
     */
    public static final void startService(Context context) {
        Intent intent = new Intent(context,
                HttpService.class);
        context.startService(intent);
    }


    /**
     * 关闭服务
     */
    public static void stopService(Context context) {
        Intent intent = new Intent(context, HttpService.class);
        context.stopService(intent);
    }
}