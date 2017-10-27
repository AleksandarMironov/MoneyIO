package io.money.moneyio.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.Utilities;


public class Fragment_Statistics extends Fragment {

    private View view;
    private PieChart pie;
    private CombinedChart combinedChart;
    private HorizontalBarChart horizontalBarChart;
    private Button incomeExpenseYearBtn, incomeExpenseMonthBtn, incomeExpenseDayBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistics, container, false);
        initialise();
        incomeExpenseYearListener();
        incomeExpenseMonthListener();
        incomeExpenseDayListener();
        return view;
    }

    private void incomeExpenseMonthListener() {
        incomeExpenseMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                combinedChart.setVisibility(View.GONE);
                pie.setVisibility(View.VISIBLE);
                horizontalBarChart.setVisibility(View.VISIBLE);
            }
        });
    }

    private void incomeExpenseDayListener() {
        incomeExpenseDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                combinedChart.setVisibility(View.GONE);
                pie.setVisibility(View.GONE);
                horizontalBarChart.setVisibility(View.VISIBLE);
            }
        });
    }

    private void incomeExpenseYearListener() {
        incomeExpenseYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                combinedChart.setVisibility(View.VISIBLE);
                pie.setVisibility(View.VISIBLE);
                horizontalBarChart.setVisibility(View.VISIBLE);

                pie.setUsePercentValues(true);
                pie.setContentDescription("TEST");
                pie.setHoleColor(Color.YELLOW);
                pie.setHoleRadius(5);
                pie.setDrawHoleEnabled(true);
                pie.setRotationEnabled(true);
                Legend l = pie.getLegend();
                l.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

                HashMap<String, Float> structuredData = new HashMap<>();
                ArrayList<MoneyFlow> utilitiesArray = Utilities.data;
                ArrayList<PieEntry> pieDataSave = new ArrayList<>();

                for (int i = 0; i < Utilities.data.size(); i++) {
                    if (structuredData.containsKey(utilitiesArray.get(i).getExpense())) {
                        structuredData.put(utilitiesArray.get(i).getExpense(), structuredData.get(utilitiesArray.get(i).getExpense())+utilitiesArray.get(i).getSum());
                    } else {
                        structuredData.put(utilitiesArray.get(i).getExpense(),utilitiesArray.get(i).getSum());
                    }
                }

                for (Iterator<Map.Entry<String, Float>> iterator = structuredData.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<String, Float> entry = iterator.next();
                    Log.e("ivan", "test -->" + entry.getKey() + " - " + entry.getValue());
                    pieDataSave.add(new PieEntry(entry.getValue(), entry.getKey().equalsIgnoreCase("true") ?  "Income" : "Expense"));
                }

                PieDataSet pieDataSet = new PieDataSet(pieDataSave, "Income/Expense year");
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.RED);
                colors.add(Color.GREEN);
                pieDataSet.setColors(colors);
                pieDataSet.setSliceSpace(5);

                pieDataSet.setValueTextSize(15f);
//        pieDataSet.setSelectionShift(15f);
                pieDataSet.setValueTextColor(Color.BLACK);
                PieData pieData = new PieData(pieDataSet);
                pieDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return (int) Math.floor(value) + "%";
                    }
                });
                Description a = new Description();
                a.setText("");
                pie.setDescription(a);
                pie.animateY(1200);
                pie.setData(pieData);
            }
        });

    }

    private void initialise() {
        pie = (PieChart) view.findViewById(R.id.statistics_income_expense_year_pie);
        combinedChart = (CombinedChart)view.findViewById(R.id.statistics_income_expense_year_combined);
        horizontalBarChart = (HorizontalBarChart)view.findViewById(R.id.statistics_income_expense_year_horizontal_bar_chart);
        incomeExpenseYearBtn = (Button)view.findViewById(R.id.statistics_year_btn);
        incomeExpenseMonthBtn = (Button)view.findViewById(R.id.statistics_month_btn);
        incomeExpenseDayBtn = (Button)view.findViewById(R.id.statistics_day_btn);
    }
}
