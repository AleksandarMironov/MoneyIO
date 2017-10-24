package io.money.moneyio.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.activities.HomeActivity;
import io.money.moneyio.activities.MainActivity;
import io.money.moneyio.model.AlarmReceiver;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.Utilities;

import static android.content.Context.ALARM_SERVICE;


public class Fragment_Alarm extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);
       ((Button)view.findViewById(R.id.test_alert)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent myIntent = new Intent(view.getContext(), AlarmReceiver.class);
                final Intent myIntent2 = new Intent(view.getContext(), AlarmReceiver.class);

                // Get the alarm manager service
                AlarmManager alarmManager = (AlarmManager) view.getContext().getSystemService(ALARM_SERVICE);
                myIntent.putExtra("message", "test text send");
                PendingIntent pending_intent = PendingIntent.getBroadcast(view.getContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                myIntent2.putExtra("message", "second notification");
                PendingIntent pending_intent2 = PendingIntent.getBroadcast(view.getContext(), 0, myIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 60000, pending_intent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 120000, pending_intent2);
                //Utilities.notifyMe(view.getContext(), "Working :)");
            }
        });
        StringBuffer sb =  new StringBuffer();
        for(MoneyFlow m : Utilities.data){
            sb.append(m.getType() + " " + m.getSum() + "\n");
        }
        ((TextView)view.findViewById(R.id.text_test)).setText(sb.toString());
        return view;
    }
}
