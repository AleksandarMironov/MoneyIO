package io.money.moneyio.fragments;

import android.app.AlarmManager;
import android.support.v4.app.Fragment;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.Alarm;
import io.money.moneyio.model.recyclers.AlarmsRecyclerViewAdapter;
import io.money.moneyio.model.database.DatabaseHelper;
import io.money.moneyio.model.receivers.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;


public class Fragment_Alarm extends Fragment implements AlarmsRecyclerViewAdapter.ItemClickListener {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseHelper db;
    private FirebaseUser firebaseUser;
    private long mLastClickTime;
    private AlarmsRecyclerViewAdapter adapter;
    private ArrayList<Alarm> alarms;
    private EditText dateEdit, timeEdit, massageEdit;
    private Button addAlarmBtn;
    private Calendar calendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);
        initialiseElements();
        startRecycler();
        onTimeEditClickListener();
        onAddAlarmBtnListener();


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
        calendar = Calendar.getInstance();
        mLastClickTime = SystemClock.elapsedRealtime();
        dateEdit = view.findViewById(R.id.alarm_date_set_edit);
        dateEdit.setText("Day: " + Calendar.DAY_OF_MONTH);
        timeEdit = view.findViewById(R.id.alarm_time_set_edit);
        timeEdit.setText("Time:" + Calendar.HOUR_OF_DAY + " : " + Calendar.MINUTE);
        massageEdit = view.findViewById(R.id.alarm_massage_set_edit);
        addAlarmBtn = view.findViewById(R.id.alarm_add_btn);

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

    public void onTimeEditClickListener(){
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        timeEdit.setText("Time: " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });
    }

    public void onAddAlarmBtnListener(){
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
            }
        });
    }

}
