package com.heygays.jarvis.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationUtil {
    public static NotificationManager mNotificationManager = null;

    public static void buildNotify(Context context, int iconId, String title, String content, int notifyId, Class<?> clazz) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title).setContentText(content)
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL, context))
                // .setNumber(number)//显示数量
                .setTicker(title)// 通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setPriority(Notification.DEFAULT_ALL)// 设置该通知优先级
                .setAutoCancel(true)// 设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_SOUND)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                // Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
                //Notification.DEFAULT_SOUND
                // requires VIBRATE permission
                .setSmallIcon(iconId);
        // 点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(context, clazz);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

    /**
     * 清除当前创建的通知栏
     */
    public static void clearNotify(int notifyId) {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(notifyId);// 删除一个特定的通知ID对应的通知
            // mNotification.cancel(getResources().getString(R.string.app_name));
        }
    }

    /**
     * 清除所有通知栏
     */
    public static void clearAllNotify() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();// 删除你发的所有通知
        }
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT 点击去除：
     * Notification.FLAG_AUTO_CANCEL
     */
    public static PendingIntent getDefalutIntent(int flags, Context context) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, new Intent(), flags);
        return pendingIntent;
    }
}
