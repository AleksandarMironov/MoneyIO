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
import java.util.List;

import io.money.moneyio.R;
import io.money.moneyio.model.database.DatabaseHelperFirebase;
import io.money.moneyio.model.utilities.GraphicUtilities;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.Utilities;

public class FragmentTab_YearGraphic extends Fragment {

    private View view;
    private EditText editDate;
    private DatabaseHelperFirebase fdb;
    private List<MoneyFlow> filteredArr;
    private PieChart pieChart;
    private BarChart chart;
    private HorizontalBarChart horizontalBarChart;
    private List<MoneyFlow> moneyFlowData; ///filtered arr, equal to Utilities.data onCreate
    private Calendar calendar;
    private MonthYearPicker monthYearPicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_fragment_graphics, container, false);
        initialiseElements();
        setFilterClickListener();
        filterDataOnStart();
        incomeExpenseYear();
//        incomeExpenseDay();
        return view;
    }

    private void initialiseElements() {
        fdb = DatabaseHelperFirebase.getInstance();
        moneyFlowData = fdb.getData();
        pieChart = (PieChart) view.findViewById(R.id.statistics_income_expense_year_pie);
        chart = (BarChart) view.findViewById(R.id.statistics_income_expense_year_combined);
        horizontalBarChart = (HorizontalBarChart)view.findViewById(R.id.statistics_income_expense_year_horizontal_bar_chart);
        monthYearPicker = new MonthYearPicker(view.getContext());
        calendar = Calendar.getInstance();
        filteredArr = new ArrayList<>();
        editDate = view.findViewById(R.id.graphics_date_edit);
        editDate.setText("Picked: " + calendar.get(Calendar.YEAR));
    }

    private void setFilterClickListener(){

        editDate.setOnClickListener(new View.OnClickListener() {
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
                        filteredArr = fdb.filterData(start, end);
                        calendar = Calendar.getInstance();
                        editDate.setText("Picked: " + monthYearPicker.getSelectedYear());
                        monthYearPicker = new MonthYearPicker(view.getContext());
                        incomeExpenseYear();
                    }
                };

                DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        monthYearPicker = new MonthYearPicker(view.getContext());
                    }
                };

                monthYearPicker.build(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick, false, false, true);
                monthYearPicker.show();
            }
        });
    }

    private void filterDataOnStart(){
        long end = calendar.getTimeInMillis();
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long start = calendar.getTimeInMillis();
        filteredArr = fdb.filterData(start, end);
    }

    private void incomeExpenseYear() {

        pieChart.setVisibility(View.VISIBLE);
        chart.setVisibility(View.VISIBLE);
        horizontalBarChart.setVisibility(View.VISIBLE);

        GraphicUtilities.pieChart(pieChart, filteredArr);
        GraphicUtilities.combinedBarChart(chart, filteredArr);
        GraphicUtilities.horizontalBarChart(horizontalBarChart, filteredArr);

    }
}
