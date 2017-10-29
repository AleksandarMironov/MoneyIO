package io.money.moneyio.model.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.model.database.DatabaseHelperSQLite;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.PlannedFlow;
import io.money.moneyio.model.utilities.Utilities;

public class OnDateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Utilities.isFirebasePersistence){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Utilities.isFirebasePersistence = true;
        }
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.keepSynced(true);

        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(context);
        ArrayList<PlannedFlow> allPlanned =  db.getAllPlaned();
        int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        for (PlannedFlow plannedFlow : allPlanned) {
            if(plannedFlow.getDate() == date){
                myRef.child(plannedFlow.getUserID())
                        .push()
                        .setValue(new MoneyFlow("false", plannedFlow.getType(), "Planned income", plannedFlow.getAmount()));
            }
        }
    }
}
