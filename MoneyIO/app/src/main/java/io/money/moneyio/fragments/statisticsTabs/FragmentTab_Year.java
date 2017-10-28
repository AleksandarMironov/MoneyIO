package io.money.moneyio.fragments.statisticsTabs;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.model.recyclers.HistoryRecyclerViewAdapter;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.Utilities;

public class FragmentTab_Year extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private Calendar calendar;
    private Button pickDateBtn;
    private TextView currentDatePicked;
    private MonthYearPicker monthYearPicker;
    private ArrayList<MoneyFlow> filteredArr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_year_datahistroy, container, false);
        initialiseElements();
        setYearBtnFilter();
        filterData(calendar.getTimeInMillis() - 1000*60*60*24, calendar.getTimeInMillis()); //da se napravi za konkretnata godina
        startRecycler(filteredArr);
        return view;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteractionHome(Uri uri);
    }

    private void initialiseElements() {
        recyclerView = (RecyclerView)view.findViewById(R.id.history_recycler_view);
        calendar = Calendar.getInstance();
        monthYearPicker = new MonthYearPicker(view.getContext());
        filteredArr = new ArrayList<>();
        currentDatePicked = (TextView)view.findViewById(R.id.history_current_date_picked);
        pickDateBtn = (Button)view.findViewById(R.id.history_pick_date_btn);
        currentDatePicked.setText("Picked year");
        pickDateBtn.setText("Pick year");
    }

    private void setYearBtnFilter(){

        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                        DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                calendar.set(monthYearPicker.getSelectedYear(), 1, 1, 0,0,0);

                                long start = calendar.getTimeInMillis();

                                calendar.set(Calendar.YEAR, monthYearPicker.getSelectedYear() + 1);

                                long end = calendar.getTimeInMillis();
                                filterData(start, end);
                                startRecycler(filteredArr);
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

                        monthYearPicker.build(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick, false, true);
                        monthYearPicker.show();
                    }
                });
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
    // must add AsyncTask!
    private void startRecycler(ArrayList<MoneyFlow> data) {
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(view.getContext(), data);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);
    }

}
