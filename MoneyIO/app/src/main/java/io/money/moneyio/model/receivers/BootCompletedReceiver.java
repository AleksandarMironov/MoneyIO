package io.money.moneyio.model.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import io.money.moneyio.model.Alarm;
import io.money.moneyio.model.database.DatabaseHelperSQLite;
import io.money.moneyio.model.utilities.AlarmUtilities;

public class BootCompletedReceiver extends BroadcastReceiver {

    //sets all alarms when phone is turned on
    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(context);

        if (firebaseAuth.getCurrentUser() != null) {
            String userID = firebaseAuth.getCurrentUser().getUid();
            List<Alarm> alarms = db.getUserAlarms(userID);

            //set all alarms
            AlarmUtilities.setAlarms(context, alarms);
        }
    }
}
