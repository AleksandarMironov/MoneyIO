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
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.Utilities;


public class Fragment_Statistics extends Fragment {

    private View view;
    private PieChart pieChart;
    private CombinedChart combinedChart;
    private HorizontalBarChart horizontalBarChart;
    private Button incomeExpenseYearBtn, incomeExpenseMonthBtn, incomeExpenseDayBtn;

    public int j = 0;

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
                pieChart.setVisibility(View.VISIBLE);
                horizontalBarChart.setVisibility(View.VISIBLE);
            }
        });
    }

    private void incomeExpenseDayListener() {
        incomeExpenseDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                combinedChart.setVisibility(View.GONE);
                pieChart.setVisibility(View.GONE);
                horizontalBarChart.setVisibility(View.VISIBLE);

                TreeMap<String, Float> structuredData = new TreeMap<>(new Comparator<String>() {
                    @Override
                    public int compare(String s, String t1) {
                        return s.compareTo(t1);
                    }
                });
                ArrayList<BarEntry> horizontalBarChartArr = new ArrayList<>();
                final ArrayList<String> names = new ArrayList<String>();

                for (int i = 0; i < Utilities.data.size(); i++) {
                    if (Utilities.data.get(i).getExpense().equalsIgnoreCase("true")) {
                        if (structuredData.containsKey(Utilities.data.get(i).getExpense())) {
                            structuredData.put(Utilities.data.get(i).getType(), structuredData.get(Utilities.data.get(i).getType())+Utilities.data.get(i).getSum());
                        } else {
                            structuredData.put(Utilities.data.get(i).getType(),Utilities.data.get(i).getSum());
                        }
                    }
                }

                int i = 0;
                for (Iterator<Map.Entry<String, Float>> iterator = structuredData.entrySet().iterator(); iterator.hasNext();) {
                    Map.Entry<String, Float> entry = iterator.next();
//                    Log.e("ivan", "test -->" + entry.getKey() + " - " + entry.getValue());
                    //i*10 => starting position of the bar
                    horizontalBarChartArr.add(new BarEntry((i*10), entry.getValue()));
                    names.add(entry.getKey());
                    Log.e("ivan", "test -->" + names.get(i));
                    i++;
                }

                XAxis xAxis = horizontalBarChart.getXAxis();
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return names.get((int)value % names.size());
                    }
                });

                xAxis.setDrawGridLines(true);
                xAxis.setDrawAxisLine(true);
                BarDataSet barDataSet = new BarDataSet(horizontalBarChartArr, "Day Expenses");
                barDataSet.setColors(Color.RED);
                BarData data = new BarData(barDataSet);
                data.setBarWidth(9f);
                horizontalBarChart.setDoubleTapToZoomEnabled(false);
                Description a = new Description();
                a.setText("");
                horizontalBarChart.setDescription(a);
                horizontalBarChart.invalidate();
                horizontalBarChart.animateY(1200);
                horizontalBarChart.setData(data);
            }
        });
    }

    private void incomeExpenseYearListener() {
        incomeExpenseYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                combinedChart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.VISIBLE);
                horizontalBarChart.setVisibility(View.VISIBLE);

                pieChart.setUsePercentValues(true);
                pieChart.setContentDescription("TEST");
                pieChart.setHoleColor(Color.YELLOW);
                pieChart.setHoleRadius(5);
                pieChart.setDrawHoleEnabled(true);
                pieChart.setRotationEnabled(true);
                Legend l = pieChart.getLegend();
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
                pieChart.setDescription(a);
                pieChart.animateY(1200);
                pieChart.invalidate();
                pieChart.setData(pieData);
            }
        });

    }

    private void initialise() {
        pieChart = (PieChart) view.findViewById(R.id.statistics_income_expense_year_pie);
        combinedChart = (CombinedChart)view.findViewById(R.id.statistics_income_expense_year_combined);
        horizontalBarChart = (HorizontalBarChart)view.findViewById(R.id.statistics_income_expense_year_horizontal_bar_chart);
        incomeExpenseYearBtn = (Button)view.findViewById(R.id.statistics_year_btn);
        incomeExpenseMonthBtn = (Button)view.findViewById(R.id.statistics_month_btn);
        incomeExpenseDayBtn = (Button)view.findViewById(R.id.statistics_day_btn);
    }
}
