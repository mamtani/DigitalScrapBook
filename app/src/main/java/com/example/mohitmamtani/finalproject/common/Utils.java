package com.example.mohitmamtani.finalproject.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.mohitmamtani.finalproject.MainActivity;
import com.example.mohitmamtani.finalproject.R;
import com.example.mohitmamtani.finalproject.model.User;


public class Utils {

    private static volatile Utils _instance = null;
    private NotificationManager mNotificationManager;
    final private static String PRIMARY_CHANNEL = "default";

    public static Utils Instance() {
        if (_instance == null) {
            synchronized (Utils.class) {
                _instance = new Utils();
            }
        }
        return _instance;
    }




    public static void saveLogin(Context context, User user) {
        SharedPreferences pref = context.getSharedPreferences(Const.SCRAP_PREF, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(Const.USER_ID, user.getId());
        editor.commit();
    }

    public static int getLoggedInUser(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Const.SCRAP_PREF, 0); // 0 - for private mode
        return pref.getInt(Const.USER_ID, -1);
    }

    public static void clearLoggedInUser(Context context) {
        SharedPreferences pref = context.getSharedPreferences(Const.SCRAP_PREF, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(Const.USER_ID);
        editor.commit();
    }

    public void showNotification(Context mContext, String text) {
        Intent notificationIntent = new Intent(mContext, MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder notificationBuilder = new Notification.Builder(mContext,
                    PRIMARY_CHANNEL)
                    .setContentTitle(mContext.getString(R.string.app_name))
                    .setContentText(text)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentIntent(notificationPendingIntent);

            getNotificationManager(mContext).notify(0, notificationBuilder.build());
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, PRIMARY_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(mContext.getString(R.string.app_name))
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

            notificationManager.notify(0, mBuilder.build());
        }

    }


    public NotificationManager getNotificationManager(Context mContext) {

        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) mContext.getSystemService(
                    Context.NOTIFICATION_SERVICE);
        }

        return mNotificationManager;
    }
}

