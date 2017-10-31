package io.money.moneyio.model.utilities;


import android.graphics.Color;
import android.util.EventLogTags;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultXAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.money.moneyio.model.MoneyFlow;

public abstract class GraphicUtilities {


    public static void combinedBarChart(BarChart chart, List<MoneyFlow> filteredArr){

        //Set the chart setting
        chart.setDescription(null);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);
        chart.setScaleEnabled(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        ArrayList<String> xVals = new ArrayList();

        xVals.add("Jan");
        xVals.add("Feb");
        xVals.add("Mar");
        xVals.add("Apr");
        xVals.add("May");
        xVals.add("Jun");
        xVals.add("Jul");
        xVals.add("Aug");
        xVals.add("Sep");
        xVals.add("Oct");
        xVals.add("Nov");
        xVals.add("Dec");

        float [] income = {0,0,0,0,0,0,0,0,0,0,0,0};
        float [] expense = {0,0,0,0,0,0,0,0,0,0,0,0};
        Calendar cal = Calendar.getInstance();
        for(MoneyFlow m : filteredArr){
            if(m.getExpense().equalsIgnoreCase("true")){
                cal.setTimeInMillis(m.getCalendar());
                income[cal.get(Calendar.MONTH)] += m.getSum();
            } else {
                cal.setTimeInMillis(m.getCalendar());
                expense[cal.get(Calendar.MONTH)] += m.getSum();
            }
        }

        Log.e("ivan", Arrays.toString(income) + " -->> \n" +  Arrays.toString(expense));

        ArrayList<BarEntry> yVals1 = new ArrayList(); //expense
        ArrayList<BarEntry> yVals2 = new ArrayList(); //income

        for(int i =0; i<income.length; i++){
            yVals1.add(new BarEntry((float) expense[i], i));
            yVals2.add(new BarEntry((float) income[i], i));
        }

        //draw the graph
        BarDataSet set1, set2;
        set1 = new BarDataSet(yVals1, "Expense");
        set1.setColor(Color.RED);
        set2 = new BarDataSet(yVals2, "Income");
        set2.setColor(Color.GREEN);

        ArrayList<IBarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        BarData data = new BarData(xVals, sets);
        data.setValueFormatter(new LargeValueFormatter());
        chart.setData(data);
        chart.getXAxis().setValueFormatter(new DefaultXAxisValueFormatter());
        chart.animateY(1000);
        chart.invalidate();
    }

    public static void pieChart(PieChart pieChart, List<MoneyFlow> utilitiesArray){
        pieChart.setUsePercentValues(true);
        pieChart.setContentDescription("TEST");
        pieChart.setHoleColor(Color.YELLOW);
        pieChart.setHoleRadius(5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationEnabled(true);
        Legend leg = pieChart.getLegend();
        leg.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

        HashMap<String, Float> structuredData2 = new HashMap<>();
        ArrayList<Entry> pieDataSave = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for (int z = 0; z < utilitiesArray.size(); z++) {
            if (structuredData2.containsKey(utilitiesArray.get(z).getExpense())) {
                structuredData2.put(utilitiesArray.get(z).getExpense(), structuredData2.get(utilitiesArray.get(z).getExpense())+utilitiesArray.get(z).getSum());
            } else {
                structuredData2.put(utilitiesArray.get(z).getExpense(),utilitiesArray.get(z).getSum());
            }
        }

        int i = 0;
        for (Iterator<Map.Entry<String, Float>> iterator = structuredData2.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Float> entry = iterator.next();
            pieDataSave.add(new Entry(entry.getValue(), i));
            if (entry.getKey().equalsIgnoreCase("true")) {
                names.add("Income");
            } else {
                names.add("Expense");
            }
            i++;
        }

        PieDataSet pieDataSet = new PieDataSet(pieDataSave, "- Income/Expense");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(5);

        pieDataSet.setValueTextSize(15f);
        pieDataSet.setValueTextColor(Color.BLACK);
        PieData pieData = new PieData(names, pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setDescription("");
        pieChart.setData(pieData);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    public static void horizontalBarChart(HorizontalBarChart horizontalBarChart, List<MoneyFlow> filteredArr){

        TreeMap<String, Float> structuredData = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });

        ArrayList<BarEntry> horizontalBarChartArr = new ArrayList<>();
        final List<String> names = new ArrayList<String>();

        for (int i = 0; i < filteredArr.size(); i++) {
            if (filteredArr.get(i).getExpense().equalsIgnoreCase("true")) {
                if (!structuredData.containsKey(filteredArr.get(i).getType())) {
                    structuredData.put(filteredArr.get(i).getType(),filteredArr.get(i).getSum());
                } else {
                    structuredData.put(filteredArr.get(i).getType(), (structuredData.get(filteredArr.get(i).getType())+filteredArr.get(i).getSum()));
                }
            }
        }

        if (structuredData.isEmpty()) {
            horizontalBarChart.setVisibility(View.INVISIBLE);
            return;
        }

        int i = 0;
        ArrayList<Float> values = new ArrayList<>();
        for (Iterator<Map.Entry<String, Float>> iterator = structuredData.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Float> entry = iterator.next();
            names.add(entry.getKey());
            values.add(entry.getValue());
        }

        int valuesLen = values.size();
        horizontalBarChart.getLayoutParams().height = valuesLen * 50;

        for (int z = 0; z < values.size(); z++) {
            horizontalBarChartArr.add(new BarEntry(values.get(z), z));
        }

        BarDataSet bardataset = new BarDataSet(horizontalBarChartArr, "Expenses");
        bardataset.setColor(Color.RED);
        BarData data = new BarData(names, bardataset);
        horizontalBarChart.setDoubleTapToZoomEnabled(false);
        horizontalBarChart.setDescription("");
        horizontalBarChart.invalidate();
        horizontalBarChart.animateY(1000);
        horizontalBarChart.setData(data);
    }
}
