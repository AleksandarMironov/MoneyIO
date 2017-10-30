package io.money.moneyio.fragments.statisticsTabs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.model.recyclers.HistoryRecyclerViewAdapter;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.Utilities;

public class FragmentTab_Month extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private Calendar calendar;
    private EditText editDate;
    private MonthYearPicker monthYearPicker;
    private ArrayList<MoneyFlow> filteredArr;

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
        recyclerView = view.findViewById(R.id.history_recycler_view);
        calendar = Calendar.getInstance();
        monthYearPicker = new MonthYearPicker(view.getContext());
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.history_date_edit);
        editDate.setText("Picked: " + calendar.get(Calendar.YEAR) + " / " + (calendar.get(Calendar.MONTH)+1));
    }

    private void setFilter(){

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        calendar.set(monthYearPicker.getSelectedYear(), monthYearPicker.getSelectedMonth(), 1, 0,0,0);

                        long start = calendar.getTimeInMillis();

                        if(monthYearPicker.getSelectedMonth() == 12){
                            calendar.set(Calendar.YEAR, monthYearPicker.getSelectedYear() + 1);
                            calendar.set(Calendar.MONTH, 1);
                        } else {
                            calendar.set(Calendar.MONTH, monthYearPicker.getSelectedMonth() + 1);
                        }

                        long end = calendar.getTimeInMillis();

                        filteredArr = Utilities.filterData(start, end);
                        startRecycler(filteredArr);
                        editDate.setText("Picked: " + monthYearPicker.getSelectedYear() + " / " + (monthYearPicker.getSelectedMonth()+1));
                        calendar = Calendar.getInstance();
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                monthYearPicker.build(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick,false, true, true);
                monthYearPicker.show();
            }
        });
    }

    private void filterDataOnStart(){
        long end = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long start = calendar.getTimeInMillis();
        filteredArr = Utilities.filterData(start, end);
    }

    // must add AsyncTask!
    private void startRecycler(ArrayList<MoneyFlow> data) {
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(view.getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }
}
