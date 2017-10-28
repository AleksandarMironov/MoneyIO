package io.money.moneyio.fragments.graphicsTabs;

import android.app.DatePickerDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.Utilities;

public class FragmentTab_DayGraphic extends Fragment {

    private View view;
    private Calendar calendar;
    private EditText editDate;
    private ArrayList<MoneyFlow> filteredArr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_graphics, container, false);
        initialiseElements();
        setFilterClickListener();
        filterDataOnStart();

        return view;
    }

    private void initialiseElements() {
        calendar = Calendar.getInstance();
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.graphics_date_edit);
        editDate.setText("Picked: " + calendar.get(Calendar.YEAR) + " / " +
                (calendar.get(Calendar.MONTH)+1) + " / " +  calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setFilterClickListener(){

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth, 0,0,0);
                long start = calendar.getTimeInMillis();
                long end = start + 1000*60*60*24;
                filterData(start, end);
                editDate.setText("Picked: " + dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
                ////do something
                calendar = Calendar.getInstance();
            }
        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                new DatePickerDialog(view.getContext(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void filterDataOnStart(){
        long end = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long start = calendar.getTimeInMillis();
        filterData(start, end);
    }

    private void filterData(long start, long end){
        filteredArr = new ArrayList<>();
        for (MoneyFlow f: Utilities.data) {
            if(start <= f.getCalendar() && f.getCalendar() <= end){
                filteredArr.add(f);
            } else if(f.getCalendar() > end){
                break;
            }
        }
    }
}
