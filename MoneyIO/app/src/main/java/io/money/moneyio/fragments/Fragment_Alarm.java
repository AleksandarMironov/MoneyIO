package io.money.moneyio.fragments;

import android.app.AlarmManager;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
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
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.Alarm;
import io.money.moneyio.model.recyclers.AlarmsRecyclerViewAdapter;
import io.money.moneyio.model.database.DatabaseHelperSQLite;
import io.money.moneyio.model.receivers.AlarmReceiver;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.Utilities;

import static android.content.Context.ALARM_SERVICE;


public class Fragment_Alarm extends Fragment implements AlarmsRecyclerViewAdapter.ItemClickListener {

    private View view;
    private RecyclerView recyclerView;
    private DatabaseHelperSQLite db;
    private FirebaseUser user;
    private long mLastClickTime;
    private AlarmsRecyclerViewAdapter adapter;
    private ArrayList<Alarm> alarms;
    private EditText dateEdit, timeEdit, massageEdit;
    private Button addAlarmBtn;
    private Calendar calendar;
    private int hour, minute, date;
    private MonthYearPicker monthYearPicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_alarm, container, false);
        initialiseElements();
        startRecycler();
        onTimeEditClickListener();
        onAddAlarmBtnListener();
        setInitialStateDateTimeFields();
        onDateEditClickListener();
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
        recyclerView = view.findViewById(R.id.recycler_alarms);
        db = DatabaseHelperSQLite.getInstance(view.getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        mLastClickTime = SystemClock.elapsedRealtime();
        dateEdit = view.findViewById(R.id.alarm_date_set_edit);
        timeEdit = view.findViewById(R.id.alarm_time_set_edit);
        massageEdit = view.findViewById(R.id.alarm_massage_set_edit);
        addAlarmBtn = view.findViewById(R.id.alarm_add_btn);
        monthYearPicker = new MonthYearPicker(view.getContext());
    }

    private void startRecycler() {
        alarms = db.getUserAlarms(user.getUid());
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
        db.deleteAlarm(user.getUid(), alarms.get(position).getDate(), alarms.get(position).getHour(),
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
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
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

    public void onDateEditClickListener(){
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        date = monthYearPicker.getSelectedDay();

                        dateEdit.setText("Day: " + date);
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                monthYearPicker.build(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick, true, false, false);
                monthYearPicker.show();
            }
        });
    }


    public void onAddAlarmBtnListener(){
        addAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String massage = massageEdit.getText().toString();
                boolean isAdded = db.addAlarm(user.getUid(), date, hour, minute,
                                                (Utilities.checkString(massage)? massage : ""));
                if(isAdded){
                    Toast.makeText(view.getContext(), "Added", Toast.LENGTH_SHORT).show();
                    startRecycler();
                    setInitialStateDateTimeFields();

                } else {
                    Toast.makeText(view.getContext(), "Sorry, alarm is not added (already exists)", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setInitialStateDateTimeFields(){
        calendar = Calendar.getInstance();
        dateEdit.setText("Day: " + calendar.get(Calendar.DAY_OF_MONTH));
        timeEdit.setText("Time:" + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE));
        massageEdit.setText("");
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        date = calendar.get(Calendar.DAY_OF_MONTH);
    }
}
