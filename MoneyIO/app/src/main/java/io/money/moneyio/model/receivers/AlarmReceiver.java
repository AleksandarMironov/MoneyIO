package io.money.moneyio.model.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.money.moneyio.model.utilities.Utilities;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getExtras().getString("message");
        Utilities.notifyMe(context, message);
    }
}
