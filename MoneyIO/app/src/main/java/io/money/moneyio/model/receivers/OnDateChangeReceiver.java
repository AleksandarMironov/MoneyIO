package io.money.moneyio.model.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

import io.money.moneyio.model.Alarm;
import io.money.moneyio.model.database.DatabaseHelperFirebase;
import io.money.moneyio.model.database.DatabaseHelperSQLite;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.PlannedFlow;
import io.money.moneyio.model.utilities.AlarmUtilities;
import io.money.moneyio.model.utilities.Utilities;

public class OnDateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //sets database to be persistent
        if(!Utilities.isFirebasePersistence()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Utilities.setIsFirebasePersistence(true);
        }
        DatabaseHelperFirebase fdb = DatabaseHelperFirebase.getInstance();

        //check for planned income
        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(context);
        List<PlannedFlow> allPlanned =  db.getAllPlaned();
        int date = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        for (PlannedFlow plannedFlow : allPlanned) {
            if(plannedFlow.getDate() == date){
                fdb.addData(plannedFlow.getUserID(), new MoneyFlow("false", plannedFlow.getType(), "Planned income", plannedFlow.getAmount()));
            }
        }

        //resets alarms, if it is first day of month
        if(date == 1){
                List<Alarm> alarms = db.getAllAlarms();
            AlarmUtilities.setAlarms(context, alarms);
        }


    }
}
