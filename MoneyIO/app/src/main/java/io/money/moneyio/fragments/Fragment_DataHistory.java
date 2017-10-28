package io.money.moneyio.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.money.moneyio.R;
import io.money.moneyio.model.recyclers.HistoryRecyclerViewAdapter;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.PageAdapter;
import io.money.moneyio.model.utilities.Utilities;

public class Fragment_DataHistory extends Fragment {

    private View view;
//    private RecyclerView recyclerView;
//    private Calendar calendar;
//    private Button dayBtn, monthBtn, yearBtn, pickDateBtn;
//    private TextView currentDatePicked;
//    private MonthYearPicker monthYearPicker;
//    private ArrayList<MoneyFlow> filteredArr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_datahistory, container, false);
//        initialiseElements();
//        startRecycler(Utilities.data);
//        setDayBtnFilter();
//        setMonthBtnFilter();
//        setYearBtnFilter();

        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("DAY"));
        tabLayout.addTab(tabLayout.newTab().setText("MONTH"));
        tabLayout.addTab(tabLayout.newTab().setText("YEAR"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        final PageAdapter adapter = new PageAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

//    private void initialiseElements() {
//        recyclerView = (RecyclerView)view.findViewById(R.id.history_recycler_view);
//        dayBtn = view.findViewById(R.id.history_day_btn);
//        monthBtn = view.findViewById(R.id.history_month_btn);
//        yearBtn = view.findViewById(R.id.history_year_btn);
//        calendar = Calendar.getInstance();
//        monthYearPicker = new MonthYearPicker(view.getContext());
//        filteredArr = new ArrayList<>();
//        currentDatePicked = (TextView)view.findViewById(R.id.history_current_date_picked);
//        pickDateBtn = (Button)view.findViewById(R.id.history_pick_date_btn);
//    }

//
//    private void setYearBtnFilter(){
//
//        yearBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//
//                currentDatePicked.setText("Picked year");
//                pickDateBtn.setText("Pick year");
//
//                pickDateBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                calendar.set(monthYearPicker.getSelectedYear(), 1, 1, 0,0,0);
//
//                                long start = calendar.getTimeInMillis();
//
//                                calendar.set(Calendar.YEAR, monthYearPicker.getSelectedYear() + 1);
//
//                                long end = calendar.getTimeInMillis();
//                                filterData(start, end);
//                                startRecycler(filteredArr);
//                                calendar = Calendar.getInstance();
//                                monthYearPicker = new MonthYearPicker(view.getContext());
//                            }
//                        };
//
//                        DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                monthYearPicker = new MonthYearPicker(view.getContext());
//                            }
//                        };
//
//                        monthYearPicker.build(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick, false, true);
//                        monthYearPicker.show();
//                    }
//                });
//            }
//        });
//
//
//    }
//
//    private void filterData(long start, long end){
//        filteredArr = new ArrayList<>();
//        for (MoneyFlow f: Utilities.data) {
//            if(start <= f.getCalendar() && f.getCalendar() <= end){
//                filteredArr.add(f);
//            } else if(f.getCalendar() > end){
//                break;
//            }
//        }
//    }
//    // must add AsyncTask!
//    private void startRecycler(ArrayList<MoneyFlow> data) {
//        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(view.getContext(), data);
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        recyclerView.setAdapter(adapter);
//    }
}
