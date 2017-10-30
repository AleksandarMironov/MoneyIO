package io.money.moneyio.fragments.graphicsTabs;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.util.ArrayList;
import java.util.Calendar;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.GraphicUtilities;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.Utilities;

public class FragmentTab_MonthGraphic extends Fragment {

    private View view;
    private EditText editDate;
    private ArrayList<MoneyFlow> filteredArr;
    private PieChart pieChart;
    private BarChart chart;
    private HorizontalBarChart horizontalBarChart;
    private ArrayList<MoneyFlow> moneyFlowData; ///filtered arr, equal to Utilities.data onCreate
    private Calendar calendar;
    private MonthYearPicker monthYearPicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_graphics, container, false);
        initialiseElements();
        filterDataOnStart();
        setFilterClickListener();
        incomeExpenseDay();
        return view;
    }

    private void initialiseElements() {
        moneyFlowData = Utilities.data;
        pieChart = (PieChart) view.findViewById(R.id.statistics_income_expense_year_pie);
        chart = (BarChart) view.findViewById(R.id.statistics_income_expense_year_combined);
        horizontalBarChart = (HorizontalBarChart)view.findViewById(R.id.statistics_income_expense_year_horizontal_bar_chart);
        monthYearPicker = new MonthYearPicker(view.getContext());
        calendar = Calendar.getInstance();
        monthYearPicker = new MonthYearPicker(view.getContext());
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.graphics_date_edit);
        editDate.setText("Picked: " + calendar.get(Calendar.YEAR) + " / " + (calendar.get(Calendar.MONTH)+1));
    }

    private void setFilterClickListener(){

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
                        editDate.setText("Picked: " + monthYearPicker.getSelectedYear() + " / " + (monthYearPicker.getSelectedMonth()+1));
                        calendar = Calendar.getInstance();
                        monthYearPicker = new MonthYearPicker(view.getContext());
                        incomeExpenseDay();
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

    private void incomeExpenseDay() {

        chart.setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);
        horizontalBarChart.setVisibility(View.VISIBLE);

        GraphicUtilities.pieChart(pieChart, filteredArr);
        GraphicUtilities.horizontalBarChart(horizontalBarChart, filteredArr);
    }
}
