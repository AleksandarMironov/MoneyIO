package io.money.moneyio.model.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import io.money.moneyio.model.Alarm;
import io.money.moneyio.model.database.DatabaseHelperSQLite;
import io.money.moneyio.model.utilities.Utilities;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(context);

        if (firebaseAuth.getCurrentUser() != null) {
            String userID = firebaseAuth.getCurrentUser().getUid();
            ArrayList<Alarm> alarms = db.getUserAlarms(userID);

            Utilities.setAlarms(context, alarms);

        }
    }
}
