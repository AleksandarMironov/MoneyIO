package io.money.moneyio.model.utilities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.List;

import io.money.moneyio.activities.MainActivity;
import io.money.moneyio.model.Alarm;
import io.money.moneyio.model.receivers.AlarmReceiver;

public class AlarmUtilities {

    private static int notificationID = 0; //generate ID for notifications
    private static int reminderID = 0; //id generator for reminders // TODO add index to database

    //fires notification
    public static void notifyMe(Context context, String message){
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context.getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_menu_help)
                        .setContentTitle("Remember!")
                        .setContentText(message)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID++, builder.build()); ///static i

        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
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
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), alarm.getDate(), alarm.getHour(), alarm.getMinutes());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
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
