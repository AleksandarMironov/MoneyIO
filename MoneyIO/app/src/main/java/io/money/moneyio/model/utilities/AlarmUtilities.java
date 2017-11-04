package io.money.moneyio.model.utilities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.activities.HomeActivity;
import io.money.moneyio.activities.MainActivity;
import io.money.moneyio.model.Alarm;
import io.money.moneyio.model.receivers.AlarmReceiver;

public class AlarmUtilities {

    private static int reminderID = 0; //id generator for reminders // TODO add index to database

    //fires notification
    public static void notifyMe (Context context, String message){

        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.moneyioicon)
                        .setContentTitle(context.getString(R.string.remember))
                        .setContentText(message);

        Intent resultIntent = new Intent(context, HomeActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(HomeActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(Utilities.gnerateID(), mBuilder.build());
    }

    //sets list of alarms
    public static void setAlarms(Context context, List<Alarm> alarms){
        for (Alarm alarm : alarms) {
            setAlarm(context, alarm);
        }
    }

    //sets single alarm
    public static void setAlarm(Context context, Alarm alarm){

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int curentDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), alarm.getDate(), alarm.getHour(), alarm.getMinutes());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if(curentDay<=day) {
            switch (currentMonth){
                case 1:
                    if(day == 29 || day == 30 || day == 31) {
                        if(calendar.get(Calendar.YEAR)%4 == 0)
                        {
                            calendar.set(Calendar.DAY_OF_MONTH, 29);
                        } else {
                            calendar.set(Calendar.DAY_OF_MONTH, 28);
                        }
                    }
                    break;
                case 3:
                case 5:
                case 8:
                case 10:
                    if(day == 31) {
                        calendar.set(Calendar.DAY_OF_MONTH, 30);
                    }
                    break;
                default:
                    break;
            }
            AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(context, AlarmReceiver.class);
            myIntent.putExtra("message", alarm.getMassage());
            PendingIntent pi = PendingIntent.getBroadcast(context, reminderID++, myIntent, 0);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }
}
