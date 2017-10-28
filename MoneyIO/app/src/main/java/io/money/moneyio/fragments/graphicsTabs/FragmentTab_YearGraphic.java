package io.money.moneyio.fragments.graphicsTabs;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
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
import java.util.Map;
import java.util.TreeMap;

import io.money.moneyio.R;
import io.money.moneyio.model.utilities.MoneyFlow;
import io.money.moneyio.model.utilities.MonthYearPicker;
import io.money.moneyio.model.utilities.Utilities;

public class FragmentTab_YearGraphic extends Fragment {

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
        setFilterClickListener();
        filterDataOnStart();
        incomeExpenseYear();
//        incomeExpenseDay();
        return view;
    }

    private void initialiseElements() {
        moneyFlowData = Utilities.data;
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
                        filterData(start, end);
                        calendar = Calendar.getInstance();
                        editDate.setText("Picked: " + monthYearPicker.getSelectedYear());
                        monthYearPicker = new MonthYearPicker(view.getContext());
                        incomeExpenseYear();
//                        incomeExpenseDay();
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

    private void incomeExpenseDay() {

        //=======START OF HORIZONTAL BAR CHART=======

        TreeMap<String, Float> structuredData = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        ArrayList<BarEntry> horizontalBarChartArr = new ArrayList<>();
        final ArrayList<String> names = new ArrayList<String>();

        for (int i = 0; i < filteredArr.size(); i++) {
            if (filteredArr.get(i).getExpense().equalsIgnoreCase("true")) {
                Log.e("ivan", filteredArr.get(i).getType());
                if (!structuredData.containsKey(filteredArr.get(i).getType())) {
                    structuredData.put(filteredArr.get(i).getType(),filteredArr.get(i).getSum());
                    Log.e("ivan", filteredArr.get(i).getType() + "--->" + filteredArr.get(i).getSum());
                } else {
                    structuredData.put(filteredArr.get(i).getType(), (structuredData.get(filteredArr.get(i).getType())+filteredArr.get(i).getSum()));
                }
            }
        }

        if (structuredData.isEmpty()) {
            horizontalBarChart.setVisibility(View.INVISIBLE);
            return;
        }

        if (names.isEmpty()) {
            names.addAll(structuredData.keySet());
        }

        int i = 0;
        for (Iterator<Map.Entry<String, Float>> iterator = structuredData.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Float> entry = iterator.next();
            Log.e("ivan", "test -->" + entry.getKey() + " - " + entry.getValue());
            //i*10 => starting position of the bar
            horizontalBarChartArr.add(new BarEntry((i*10), entry.getValue()));
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
        xAxis.setDrawGridLines(false); //remove lines X
        horizontalBarChart.setDescription(a);
        horizontalBarChart.invalidate();
        horizontalBarChart.animateY(1200);
        horizontalBarChart.setData(data);
        //=====END HORIZONTAL BAR CHART======


        //====START OF PIE CHART===========
        pieChart.setUsePercentValues(true);
        pieChart.setContentDescription("TEST");
        pieChart.setHoleColor(Color.YELLOW);
        pieChart.setHoleRadius(5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationEnabled(true);
        Legend leg = pieChart.getLegend();
        leg.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

        HashMap<String, Float> structuredData2 = new HashMap<>();
        ArrayList<MoneyFlow> utilitiesArray = Utilities.data;
        ArrayList<PieEntry> pieDataSave = new ArrayList<>();

        for (int z = 0; z < utilitiesArray.size(); z++) {
            if (structuredData2.containsKey(utilitiesArray.get(z).getExpense())) {
                structuredData2.put(utilitiesArray.get(z).getExpense(), structuredData2.get(utilitiesArray.get(z).getExpense())+utilitiesArray.get(z).getSum());
            } else {
                structuredData2.put(utilitiesArray.get(z).getExpense(),utilitiesArray.get(i).getSum());
            }
        }

        for (Iterator<Map.Entry<String, Float>> iterator = structuredData2.entrySet().iterator(); iterator.hasNext();) {
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


        private void incomeExpenseYear() {

            chart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.VISIBLE);
            horizontalBarChart.setVisibility(View.VISIBLE);

            incomeExpenseDay();

            //=======COMBINED BAR CHART=======
            float barWidth = 0.3f;
            float barSpace = 0f;
            float groupSpace = 0.4f;

            //Set the chart setting
            chart.setDescription(null);
            chart.setPinchZoom(true);
            chart.setDoubleTapToZoomEnabled(true);
            chart.setScaleEnabled(false);
            chart.setDrawBarShadow(false);
            chart.setDrawGridBackground(false);

            //create the dummy data for display the graph
            int groupCount = 12;

            ArrayList<String> xVals = new ArrayList();

            xVals.add("Jan    Feb");
            xVals.add("");
            xVals.add("Mar    Apr");
            xVals.add("");
            xVals.add("May    Jun");
            xVals.add("");
            xVals.add("Jul    Aug");
            xVals.add("");
            xVals.add("Sep    Oct");
            xVals.add("");
            xVals.add("Nov    Dec");
            xVals.add("");

            ArrayList<BarEntry> yVals1 = new ArrayList();
            ArrayList<BarEntry> yVals2 = new ArrayList();
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
            yVals1.add(new BarEntry(7, (float) 1));
            yVals2.add(new BarEntry(7, (float) 2));
            yVals1.add(new BarEntry(8, (float) 3));
            yVals2.add(new BarEntry(8, (float) 4));
            yVals1.add(new BarEntry(9, (float) 5));
            yVals2.add(new BarEntry(9, (float) 6));
            yVals1.add(new BarEntry(10, (float) 7));
            yVals2.add(new BarEntry(10, (float) 8));
            yVals1.add(new BarEntry(11, (float) 9));
            yVals2.add(new BarEntry(11, (float) 10));
            yVals1.add(new BarEntry(12, (float) 11));
            yVals2.add(new BarEntry(12, (float) 12));

            //draw the graph
                BarDataSet set1, set2;
                set1 = new BarDataSet(yVals1, "Expense");
                set1.setColor(Color.RED);
                set2 = new BarDataSet(yVals2, "Income");
                set2.setColor(Color.GREEN);
                BarData data = new BarData(set1, set2);
                data.setValueFormatter(new LargeValueFormatter());
                chart.setData(data);
                chart.getBarData().setBarWidth(barWidth);
                chart.getXAxis().setAxisMinimum(0);
                chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
                chart.groupBars(0, groupSpace, barSpace);
                chart.getData().setHighlightEnabled(false);
                chart.animateY(1200);
                chart.invalidate();

                //Draw the indicator
                Legend l = chart.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(true);
                l.setYOffset(20f);
                l.setXOffset(0f);
                l.setYEntrySpace(0f);
                l.setTextSize(8f);

                //Draw the X-Axis and Y-Axis
                //X-axis
                XAxis xAxis = chart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setCenterAxisLabels(true);
                xAxis.setDrawGridLines(false);
                xAxis.setAxisMaximum(12);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
                //Y-axis
                chart.getAxisRight().setEnabled(false);
                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setValueFormatter(new LargeValueFormatter());
                leftAxis.setDrawGridLines(true);
                leftAxis.setSpaceTop(35f);
                leftAxis.setAxisMinimum(0f);
        }
}
