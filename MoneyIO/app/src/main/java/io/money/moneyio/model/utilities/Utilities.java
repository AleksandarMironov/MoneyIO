package io.money.moneyio.model.utilities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.activities.MainActivity;
import io.money.moneyio.model.Alarm;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.receivers.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

public class Utilities {
    public static ArrayList<MoneyFlow> data;
    public static boolean isFirebasePersistence = false;

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

    public static void notifyMe(Context context, String message){
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context.getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.ic_menu_help)
                        .setContentTitle("Remember!")
                        .setContentText(message)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, builder.build());

        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
        mediaPlayer.start();
    }

    public static ArrayList<MoneyFlow> filterData(long start, long end){
        ArrayList<MoneyFlow> filteredArr = new ArrayList<>();
        for (MoneyFlow f: Utilities.data) {
            if(start <= f.getCalendar() && f.getCalendar() <= end){
                filteredArr.add(f);
            } else if(f.getCalendar() > end){
                break;
            }
        }
        return filteredArr;
    }

    public static void setAlarms(Context context, ArrayList<Alarm> alarms){
        for (Alarm alarm : alarms) {
            setAlarm(context, alarm);
        }
    }

    public static void setAlarm(Context context, Alarm alarm){

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 9, alarm.getDate(), alarm.getHour(), alarm.getMinutes());


        Intent myIntent = new Intent(context, AlarmReceiver.class);

        // Get the alarm manager service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        myIntent.putExtra("message", alarm.getMassage());
        PendingIntent pending_intent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);
    }
}
