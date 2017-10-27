package io.money.moneyio.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.model.Alarm;
import io.money.moneyio.model.AlarmsRecyclerViewAdapter;
import io.money.moneyio.model.DatabaseHelper;
import io.money.moneyio.model.ShowCustomTypesRecyclerViewAdapter;
import io.money.moneyio.model.Type;
import io.money.moneyio.model.receivers.AlarmReceiver;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.Utilities;

import static android.content.Context.ALARM_SERVICE;


public class Fragment_Alarm extends Fragment implements AlarmsRecyclerViewAdapter.ItemClickListener {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseHelper db;
    private FirebaseUser firebaseUser;
    private long mLastClickTime;
    private AlarmsRecyclerViewAdapter adapter;
    private ArrayList<Alarm> alarms;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);
        initialiseElements();
        startRecycler();



        ////////////////////////
       ((Button)view.findViewById(R.id.test_alert)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent myIntent = new Intent(view.getContext(), AlarmReceiver.class);

                // Get the alarm manager service
                AlarmManager alarmManager = (AlarmManager) view.getContext().getSystemService(ALARM_SERVICE);
                myIntent.putExtra("message", "test text send");
                PendingIntent pending_intent = PendingIntent.getBroadcast(view.getContext(), 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 60000, pending_intent);
                //Utilities.notifyMe(view.getContext(), "Working :)");
            }
        });
        ///////////////////////////
        return view;
    }

    private void initialiseElements() {
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_alarms);
        db = DatabaseHelper.getInstance(view.getContext());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    private void startRecycler() {
        alarms = db.getUserAlarms(firebaseUser.getUid());
        adapter = new AlarmsRecyclerViewAdapter(view.getContext(), alarms);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 700){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        db.deleteAlarm(firebaseUser.getUid(), alarms.get(position).getDate(), alarms.get(position).getHour(),
                alarms.get(position).getMinutes(), alarms.get(position).getMassage());
        alarms.remove(position);
        recyclerView.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, alarms.size());
        Toast.makeText(view.getContext(), "DELETED", Toast.LENGTH_SHORT).show();
    }
}
