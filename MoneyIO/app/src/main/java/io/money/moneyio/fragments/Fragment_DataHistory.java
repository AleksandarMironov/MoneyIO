package io.money.moneyio.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.model.HistoryRecyclerViewAdapter;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.Utilities;

public class Fragment_DataHistory extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private DatePicker datePicker;
    private Calendar calendar;
    private Button dayBtn, monthBtn, yearBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_datahistory, container, false);
        initialiseElements();
        startRecycler(Utilities.data);
        setDayBtnFilter();
        setMonthBtnFilter();
        setYearBtnFilter();
        return view;
    }

    private void initialiseElements() {
        recyclerView = (RecyclerView)view.findViewById(R.id.history_recycler_view);
        dayBtn = view.findViewById(R.id.history_day_btn);
        monthBtn = view.findViewById(R.id.history_month_btn);
        yearBtn = view.findViewById(R.id.history_year_btn);
        calendar = Calendar.getInstance();
    }

    private void setDayBtnFilter(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                long start = calendar.getTimeInMillis();
                long end = start + 1000*60*60*24;
                ArrayList<MoneyFlow> a = new ArrayList<>();
                for (MoneyFlow f: Utilities.data) {
                    if(start <= f.getCalendar() && f.getCalendar() <= end){
                        a.add(f);
                    } else if(f.getCalendar() > end){
                        break;
                    }
                }
                startRecycler(a);
            }
        };
        dayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(view.getContext(), android.R.style.Theme_Holo_Dialog, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setMonthBtnFilter(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, 1);

                long start = calendar.getTimeInMillis();

                if(monthOfYear == 12){
                    calendar.set(Calendar.YEAR, year + 1);
                    calendar.set(Calendar.MONTH, 1);
                } else {
                    calendar.set(Calendar.MONTH, monthOfYear + 1);
                }
                long end = calendar.getTimeInMillis();

                ArrayList<MoneyFlow> a = new ArrayList<>();
                for (MoneyFlow f: Utilities.data) {
                    if(start <= f.getCalendar() && f.getCalendar() <= end){
                        a.add(f);
                    } else if(f.getCalendar() > end){
                        break;
                    }
                }
                startRecycler(a);
            }
        };
        monthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), android.R.style.Theme_Holo_Dialog, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        1);

                dialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);

                dialog.show();
            }
        });
    }

    private void setYearBtnFilter(){
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);

                long start = calendar.getTimeInMillis();

                calendar.set(Calendar.YEAR, year + 1);

                long end = calendar.getTimeInMillis();

                ArrayList<MoneyFlow> a = new ArrayList<>();
                for (MoneyFlow f: Utilities.data) {
                    if(start <= f.getCalendar() && f.getCalendar() <= end){
                        a.add(f);
                    } else if(f.getCalendar() > end){
                        break;
                    }
                }
                startRecycler(a);
            }
        };
        yearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), android.R.style.Theme_Holo_Dialog, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        1);

                dialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
                dialog.getDatePicker().findViewById(getResources().getIdentifier("month","id","android")).setVisibility(View.GONE);

                dialog.show();
            }
        });
    }

    // must add AsyncTask!
    private void startRecycler(ArrayList<MoneyFlow> data) {
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(view.getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
