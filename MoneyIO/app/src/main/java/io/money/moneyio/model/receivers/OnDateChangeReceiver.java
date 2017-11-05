package io.money.moneyio.model.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
        DatabaseHelperFirebase fdb = DatabaseHelperFirebase.getInstance(context);

        //check for planned income
        DatabaseHelperSQLite db = DatabaseHelperSQLite.getInstance(context);
        List<PlannedFlow> allPlanned =  db.getAllPlaned();

        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);

        //check length of month. If selected date is out of month, alarm will be triggered on last day of month
        for (PlannedFlow plannedFlow : allPlanned) {
            switch (currentMonth){
                //february
                case 1:
                    if(calendar.get(Calendar.YEAR)%4 == 0)
                    {
                        if(date == 29){
                            if(plannedFlow.getDate() > 29){
                                fdb.addData(plannedFlow.getUserID(), new MoneyFlow(plannedFlow.getUserID(), "in", plannedFlow.getType(), "Planned income", plannedFlow.getAmount()));
                            }
                        }
                    } else {
                        if(date == 28){
                            if(plannedFlow.getDate() > 28){
                                fdb.addData(plannedFlow.getUserID(), new MoneyFlow(plannedFlow.getUserID(), "in", plannedFlow.getType(), "Planned income", plannedFlow.getAmount()));
                            }
                        }
                    }

                    break;
                //april, june, september, november
                case 3:
                case 5:
                case 8:
                case 10:
                    if(date == 30){
                        if(plannedFlow.getDate() == 31){
                            fdb.addData(plannedFlow.getUserID(), new MoneyFlow(plannedFlow.getUserID(), "in", plannedFlow.getType(), "Planned income", plannedFlow.getAmount()));
                        }
                    }
                    break;
                default:
                    break;
            }
            if(plannedFlow.getDate() == date){
                fdb.addData(plannedFlow.getUserID(), new MoneyFlow(plannedFlow.getUserID(), "in", plannedFlow.getType(), "Planned income", plannedFlow.getAmount()));
            }
        }
        
        //resets alarms, if it is first day of month
        if(date == 1){
            List<Alarm> alarms = db.getAllAlarms();
            AlarmUtilities.setAlarms(context, alarms);
        }
    }
}
