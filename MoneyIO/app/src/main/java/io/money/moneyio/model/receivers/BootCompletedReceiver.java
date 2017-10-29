package io.money.moneyio.model.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import io.money.moneyio.model.utilities.Alarm;
import io.money.moneyio.model.database.DatabaseHelperSQLite;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(context);

        if (firebaseAuth.getCurrentUser() != null) {
            String userID = firebaseAuth.getCurrentUser().getUid();
            ArrayList<Alarm> alarms = db.getUserAlarms(userID);

            for (Alarm alarm : alarms) {
                //TODO do something
                Toast.makeText(context, alarm.getMassage(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
