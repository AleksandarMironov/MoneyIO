package io.money.moneyio.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.money.moneyio.R;
import io.money.moneyio.model.MoneyFlow;
import io.money.moneyio.model.Utilities;


public class Fragment_Statistics extends Fragment {

    private View view;
    private PieChart pie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistics, container, false);
        pie = (PieChart) view.findViewById(R.id.pie);
//        GraphView graph = (GraphView) view.findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        graph.addSeries(series);
        pie.setUsePercentValues(true);
        pie.setContentDescription("TEST");
        pie.setHoleColor(Color.GRAY);
        pie.setHoleRadius(7);
        pie.setDrawHoleEnabled(true);
        pie.setRotationEnabled(true);
        Legend l = pie.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_CENTER);

        HashMap<String, Float> structuredData = new HashMap<>();
        ArrayList<MoneyFlow> utilitiesArray = Utilities.data;
        ArrayList<PieEntry> pieDataSave = new ArrayList<>();

        for (int i = 0; i < Utilities.data.size(); i++) {
            if (structuredData.containsKey(utilitiesArray.get(i).getType())) {
                structuredData.put(utilitiesArray.get(i).getType(), structuredData.get(utilitiesArray.get(i).getType())+utilitiesArray.get(i).getSum());
            } else {
                structuredData.put(utilitiesArray.get(i).getType(),utilitiesArray.get(i).getSum());
            }
        }

        for (Iterator<Map.Entry<String, Float>> iterator = structuredData.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, Float> entry = iterator.next();
            Log.e("ivan", "test -->" + entry.getKey() + " - " + entry.getValue());
            pieDataSave.add(new PieEntry(entry.getValue(), entry.getKey()));
        }






        PieDataSet pieDataSet = new PieDataSet(pieDataSave, "TESTVAM MALKO");
        pieDataSet.setSliceSpace(8);

        pieDataSet.setColor(Color.GRAY);
        PieData pieData = new PieData(pieDataSet);
        pie.setData(pieData);
        return view;
    }
}
