package io.money.moneyio.model.utilities;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import io.money.moneyio.R;
import io.money.moneyio.activities.HomeActivity;


public class Utilities {
    private static boolean isFirebasePersistence = false;
    private static boolean hasFriend = false;

    public static boolean isHasFriend() {
        return hasFriend;
    }

    public static void setHasFriend(boolean hasFriend) {
        Utilities.hasFriend = hasFriend;
    }

    public static boolean isFirebasePersistence() {
        return isFirebasePersistence;
    }

    public static void setIsFirebasePersistence(boolean isFirebasePersistence) {
        Utilities.isFirebasePersistence = isFirebasePersistence;
    }

    public static boolean checkString(String str){
        if (TextUtils.isEmpty(str) ||  str.matches("[&.\\;'\"]")){
            return false;
        }
        return true;
    }

    public static boolean isMail(String str){
        return !TextUtils.isEmpty(str) && android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public static boolean isNumber(String str){
        if (!TextUtils.isEmpty(str) ||  str.matches("[0-9.]+")){
            return true;
        }
        return false;
    }

    private static int idGeneratorNotifications = 0;
    public static void notifyFriend(Context context, String type, String sum){

        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.alarm_icon)
                        .setContentTitle("Your friend spend")
                        .setContentText(sum + " for " + type);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, HomeActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(idGeneratorNotifications++, mBuilder.build());
    }
}
