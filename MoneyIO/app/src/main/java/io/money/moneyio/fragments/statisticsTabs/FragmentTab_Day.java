package io.money.moneyio.fragments.statisticsTabs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.model.database.DatabaseHelperFirebase;
import io.money.moneyio.model.recyclers.HistoryRecyclerViewAdapter;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.utilities.Utilities;

public class FragmentTab_Day extends Fragment {

    private View view;
    DatabaseHelperFirebase fdb;
    private RecyclerView recyclerView;
    private Calendar calendar;
    private EditText editDate;
    private List<MoneyFlow> filteredArr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_day_datahistory, container, false);
        initialiseElements();
        setFilter();
        filterDataOnStart();
        startRecycler(filteredArr);
        return view;
    }

    private void initialiseElements() {
        fdb = DatabaseHelperFirebase.getInstance();
        recyclerView = view.findViewById(R.id.history_recycler_view);
        calendar = Calendar.getInstance();
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.history_date_edit);
        editDate.setText("Picked: " + calendar.get(Calendar.YEAR) + " / " +
                        (calendar.get(Calendar.MONTH)+1) + " / " +  calendar.get(Calendar.DAY_OF_MONTH));
    }

        private void setFilter(){

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    calendar.set(year, monthOfYear, dayOfMonth, 0,0,0);
                    long start = calendar.getTimeInMillis();
                    long end = start + 1000*60*60*24;
                    filteredArr = fdb.filterData(start, end);
                    startRecycler(filteredArr);
                    editDate.setText("Picked: " + dayOfMonth + " / " + (monthOfYear + 1) + " / " + year);
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
        filteredArr = fdb.filterData(start, end);
    }

    // must add AsyncTask!
    private void startRecycler(List<MoneyFlow> data) {
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(view.getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
