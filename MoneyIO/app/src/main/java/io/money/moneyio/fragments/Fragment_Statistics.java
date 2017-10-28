package io.money.moneyio.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.BarChart;
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
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.Utilities;


public class Fragment_Statistics extends Fragment {

    private View view;
    private PieChart pieChart;
    private BarChart chart;
    private HorizontalBarChart horizontalBarChart;
    private Button incomeExpenseYearBtn, incomeExpenseMonthBtn, incomeExpenseDayBtn;
    private ArrayList<MoneyFlow> moneyFlowData; ///filtered arr, equal to Utilities.data onCreate
    private Calendar calendar;
    private MonthYearPicker monthYearPicker;

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
            public void onClick(View v) {
                chart.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                horizontalBarChart.setVisibility(View.VISIBLE);

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

                        filterData(start, end);
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

                monthYearPicker.build(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), positiveClick, negativeClick, true, true);
                monthYearPicker.show();
            }
        });
    }

    private void incomeExpenseDayListener() {
        ///build calendar dialog
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(year, monthOfYear, dayOfMonth, 0,0,0);

                long start = calendar.getTimeInMillis();
                long end = start + 1000*60*60*24;
                filterData(start, end);
                calendar = Calendar.getInstance();
            }
        };
        incomeExpenseDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///show calendar dialog
                new DatePickerDialog(view.getContext(), date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                ////
                chart.setVisibility(View.GONE);
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
//                    Log.e("ivan", "test -->" + names.get(i));
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
            public void onClick(View v) {
                chart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.VISIBLE);
                horizontalBarChart.setVisibility(View.VISIBLE);

                ////show dialog and filter arr
                DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        calendar.set(monthYearPicker.getSelectedYear(), 1, 1, 0,0,0);

                        long start = calendar.getTimeInMillis();

                        calendar.set(Calendar.YEAR, monthYearPicker.getSelectedYear() + 1);

                        long end = calendar.getTimeInMillis();
                        filterData(start, end);
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

                ///////////////////////////////barchart
                float barWidth;
                float barSpace;
                float groupSpace;

                barWidth = 0.3f;
                barSpace = 0f;
                groupSpace = 0.4f;

                chart.setDescription(null);
                chart.setPinchZoom(false);
                chart.setScaleEnabled(false);
                chart.setDrawBarShadow(false);
                chart.setDrawGridBackground(false);

                int groupCount = 6;

                ArrayList xVals = new ArrayList();

                xVals.add("Jan");
                xVals.add("Feb");
                xVals.add("Mar");
                xVals.add("Apr");
                xVals.add("May");
                xVals.add("Jun");

                ArrayList yVals1 = new ArrayList();
                ArrayList yVals2 = new ArrayList();
                yVals1.add(new BarEntry(1, (float) 1));
                yVals2.add(new BarEntry(1, (float) 2));
                yVals1.add(new BarEntry(2, (float) 3));
                yVals2.add(new BarEntry(2, (float) 4));
                yVals1.add(new BarEntry(3, (float) 5));
                yVals2.add(new BarEntry(3, (float) 6));
                yVals1.add(new BarEntry(4, (float) 7));
                yVals2.add(new BarEntry(4, (float) 8));
                yVals1.add(new BarEntry(5, (float) 9));
                yVals2.add(new BarEntry(5, (float) 10));
                yVals1.add(new BarEntry(6, (float) 11));
                yVals2.add(new BarEntry(6, (float) 12));


                BarDataSet set1, set2;
                set1 = new BarDataSet(yVals1, "A");
                set1.setColor(Color.RED);
                set2 = new BarDataSet(yVals2, "B");
                set2.setColor(Color.BLUE);
                BarData data = new BarData(set1, set2);
                data.setValueFormatter(new LargeValueFormatter());
                chart.setData(data);
                chart.getBarData().setBarWidth(barWidth);
                chart.getXAxis().setAxisMinimum(0);
                chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                chart.groupBars(0, groupSpace, barSpace);
                chart.getData().setHighlightEnabled(false);
                chart.invalidate();

                Legend l = chart.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(true);
                l.setYOffset(20f);
                l.setXOffset(0f);
                l.setYEntrySpace(0f);
                l.setTextSize(8f);

                //X-axis
                XAxis xAxis = chart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setCenterAxisLabels(true);
                xAxis.setDrawGridLines(false);
                xAxis.setAxisMaximum(6);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
//Y-axis
                chart.getAxisRight().setEnabled(false);
                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setValueFormatter(new LargeValueFormatter());
                leftAxis.setDrawGridLines(true);
                leftAxis.setSpaceTop(35f);
                leftAxis.setAxisMinimum(0f);


                ///////////////////////////////////piechart
                pieChart.setUsePercentValues(true);
                pieChart.setContentDescription("TEST");
                pieChart.setHoleColor(Color.YELLOW);
                pieChart.setHoleRadius(5);
                pieChart.setDrawHoleEnabled(true);
                pieChart.setRotationEnabled(true);
                Legend leg = pieChart.getLegend();
                leg.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

                HashMap<String, Float> structuredData = new HashMap<>();
                ArrayList<MoneyFlow> utilitiesArray = Utilities.data;
                ArrayList<PieEntry> pieDataSave = new ArrayList<>();

                for (int i = 0; i < utilitiesArray.size(); i++) {
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
                Description description = new Description();
                description.setText("");
                pieChart.setDescription(description);
                pieChart.animateY(1200);
                pieChart.invalidate();
                pieChart.setData(pieData);
            }
        });

    }

    private void initialise() {
        moneyFlowData = Utilities.data;
        pieChart = (PieChart) view.findViewById(R.id.statistics_income_expense_year_pie);
        chart = (BarChart) view.findViewById(R.id.statistics_income_expense_year_combined);
        horizontalBarChart = (HorizontalBarChart)view.findViewById(R.id.statistics_income_expense_year_horizontal_bar_chart);
        incomeExpenseYearBtn = (Button)view.findViewById(R.id.statistics_year_btn);
        incomeExpenseMonthBtn = (Button)view.findViewById(R.id.statistics_month_btn);
        incomeExpenseDayBtn = (Button)view.findViewById(R.id.statistics_day_btn);
        calendar = Calendar.getInstance();
        monthYearPicker = new MonthYearPicker(view.getContext());
    }

    //filtering arr for curent user option
    private void filterData(long start, long end){
        moneyFlowData = new ArrayList<>();
        for (MoneyFlow f: Utilities.data) {
            if(start <= f.getCalendar() && f.getCalendar() <= end){
                moneyFlowData.add(f);
            } else if(f.getCalendar() > end){
                break;
            }
        }
    }
}
