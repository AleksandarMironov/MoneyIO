package io.money.moneyio.model.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.money.moneyio.model.utilities.AlarmUtilities;
import io.money.moneyio.model.utilities.Utilities;

public class AlarmReceiver extends BroadcastReceiver {

    //receiver for notifications
    @Override
    public void onReceive(Context context, Intent intent) {
        //get notification text from intent
        String message = intent.getExtras().getString("message");

        //fires notification
        AlarmUtilities.notifyMe(context, message);
    }
}
