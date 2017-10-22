package io.money.moneyio.model;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getExtras().getString("message");
        Utilities.notifyMe(context, message);
    }
}
